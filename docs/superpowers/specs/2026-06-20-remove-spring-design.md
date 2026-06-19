# Design: Remove Spring from ureport2 (v4.0.0)

**Date:** 2026-06-20
**Status:** Approved
**Target version:** 4.0.0 (hard break)
**Branch:** `remove-spring`

---

## 1. Motivation

Make ureport2 **framework-agnostic**. Today the engine is tightly coupled to
Spring (3 XML context files, 12 `ApplicationContextAware` classes, Spring JDBC,
`WebApplicationContext` bootstrap, Spring Bean Datasource feature). Consumers
must run a Spring `ContextLoaderListener` and import `ureport-console-context.xml`.

After this change ureport2-core / -console / -font contain **zero** Spring
dependencies and run in any servlet container. Spring users get a drop-in
`ureport2-spring-boot-starter` (Spring Boot 3.x+) with full auto-configuration.

This is a **hard break**: version bumps to 4.0.0, the legacy
`ContextLoaderListener` + XML-import integration contract is removed, and
custom plugins migrate from `<bean>` declarations to Java SPI
(`META-INF/services/...`).

---

## 2. Module Structure

```
ureport2-parent/                    (pom) — revision bumped to 4.0.0
ureport2-core/                      (pom) — 100% Spring-free, pure Java library
ureport2-console/                   (pom) — 100% Spring-free, servlet-based console
ureport2-font/                      (pom) — 100% Spring-free, font registry
ureport2-spring-boot-starter/       (NEW) — Spring Boot 3.x+ auto-configuration
```

### pom changes

- **`ureport2-core/pom.xml`**: remove `spring-web`, `spring-jdbc`,
  `spring-beans`, `spring-context`. No replacement deps (JDK + existing libs).
- **`ureport2-spring-boot-starter/pom.xml`** (new): depends on
  `ureport2-console` (transitively pulls core + font),
  `spring-boot-autoconfigure` (3.x+, compile), `jakarta.servlet-api` (provided).
  Targets Spring Framework 6.x / Jakarta namespace, Java 17.
- **`ureport2-parent/pom.xml`**: add `ureport2-spring-boot-starter` to
  `<modules>`; update `<description>` from "基于Spring架构" to
  "framework-agnostic".
- **`revision`** property → `4.0.0`.

---

## 3. Bootstrap Engine & SPI Registry

Replaces Spring's `ApplicationContext` and the 12 `ApplicationContextAware`
classes with a ureport-owned facade.

### New central class: `com.bstek.ureport.UReportEngine` (in core)

Single bootstrap entry point:

```java
public final class UReportEngine {
    public static synchronized UReportEngine initialize(
            Properties config, ServletContext servletContext);
    public static UReportEngine getInstance();

    public ServletContext getServletContext();
    public Properties getConfig();

    // one registry per SPI (see table below)
    public BuildinDatasourceRegistry getBuildinDatasourceRegistry();
    public ImageProviderRegistry getImageProviderRegistry();
    public ReportProviderRegistry getReportProviderRegistry();
    public ReportCacheRegistry getReportCacheRegistry();
    public DatasourceProviderRegistry getDatasourceProviderRegistry();
    public FunctionRegistry getFunctionRegistry();
    public FormParserRegistry getFormParserRegistry();
    public FontRegistry getFontRegistry();
    public ServletActionRegistry getServletActionRegistry();
    public BeanDatasourceRegistry getBeanDatasourceRegistry();
}
```

`initialize(...)` is **idempotent** (first call wins; later calls return the
existing instance). This lets the Spring Boot `@Bean` method and
`UReportServlet.init()` both call it safely.

`initialize(...)` does, in order:

1. Load `ureport.properties` via `java.util.Properties` (replaces
   `UReportPropertyPlaceholderConfigurer`). Caller-supplied `Properties`
   override classpath defaults.
2. Stash `ServletContext` (needed by `FileReportProvider` /
   `DefaultImageProvider` for `getRealPath("/")`).
3. Run the three module bootstraps (Section 4) to wire built-in beans.
4. For each SPI, call `ServiceLoader.load(<SPI>.class)` and merge results
   into the corresponding registry.

### SPI registries

One registry per extension point. Each exposes `register(impl)`, `all()`,
and where applicable `getByName(name)`. Built-ins are registered by the
module bootstraps; user plugins are discovered via `ServiceLoader` and
registered on top.

| SPI interface (existing) | Registry | Today's holder |
|---|---|---|
| `BuildinDatasource` | `BuildinDatasourceRegistry` | `Utils` |
| `ImageProvider` | `ImageProviderRegistry` | `Utils` |
| `ReportProvider` | `ReportProviderRegistry` | `ReportRender` |
| `ReportCache` / `ReportDefinitionCache` | `ReportCacheRegistry` | `CacheUtils` |
| `DatasourceProvider` | `DatasourceProviderRegistry` | `ReportBuilder` |
| `Function` | `FunctionRegistry` | `ExpressionUtils` |
| `FormParser` | `FormParserRegistry` | `FormParserUtils` |
| `FontRegister` | `FontRegistry` | `FontBuilder` |
| `ServletAction` | `ServletActionRegistry` | `UReportServlet` |
| **`BeanDatasourceProvider`** (NEW) | `BeanDatasourceRegistry` | (replaces `SpringBeanDatasourceDefinition`'s Spring lookup) |

### Lifecycle of the 12 `ApplicationContextAware` classes

- Drop `implements ApplicationContextAware` and `setApplicationContext(...)`.
- Drop imports of `org.springframework.beans.BeansException`,
  `org.springframework.context.ApplicationContext`,
  `org.springframework.context.ApplicationContextAware`.
- Their init logic moves into either `UReportEngine.initialize()` (built-in
  wiring) or the registry's lazy load.
- The existing static-holder classes (`Utils`, `CacheUtils`,
  `ExpressionUtils`, `FontBuilder`, `FormParserUtils`) keep their public
  static API but delegate to the registries, so internal call sites stay
  mostly unchanged.

---

## 4. XML → Programmatic Wiring

**Delete all 3 Spring XML context files:**

- `ureport2-core/src/main/resources/ureport-core-context.xml` (~80 beans)
- `ureport2-console/src/main/resources/ureport-console-context.xml` (~13 beans)
- `ureport2-font/src/main/resources/ureport-font-context.xml` (~9 beans)

**Replace with 3 programmatic bootstrap classes**, one per module, called by
`UReportEngine.initialize()`:

1. **`com.bstek.ureport.bootstrap.CoreBootstrap`** (core) — wires:
   - Singletons: `ExportManagerImpl`, `ReportRender`, `ReportBuilder`,
     `HideRowColumnBuilder`, `ReportParser`, `Utils`, `CacheUtils`,
     `FontBuilder`, `ExpressionUtils`, `FormParserUtils`.
   - Providers: `DefaultImageProvider`, `FileReportProvider`,
     `HttpImageProvider`, `HttpsImageProvider`, `ClasspathReportProvider`.
   - 8 search-form parsers (register into `FormParserRegistry`).
   - ~55 expression `Function` beans (register into `FunctionRegistry`).
   - Property values (`${ureport.fileStoreDir}` etc.) read from the loaded
     `Properties`.
   - `UReportPropertyPlaceholderConfigurer` is **deleted**; not needed.

2. **`com.bstek.ureport.console.bootstrap.ConsoleBootstrap`** (console) —
   wires the 13 `ServletAction` beans + `HttpSessionReportCache` into
   `ServletActionRegistry` / `ReportCacheRegistry`. Runs after core
   bootstrap.

3. **`com.bstek.ureport.font.bootstrap.FontBootstrap`** (font) — wires the
   9 `FontRegister` beans into `FontRegistry`.

**Keep `ureport.properties`** (4 lines) — loaded by `UReportEngine` via
`Properties.load(...)` from classpath, overridable by caller-supplied
`Properties` (e.g. Spring Boot `@ConfigurationProperties` → `Properties`).

---

## 5. BeanDatasourceProvider SPI (replaces Spring Bean Datasource)

The "Spring Bean Datasource" designer feature currently does
`applicationContext.getBean(beanId)`. We abstract this behind a new SPI so
any framework can provide it.

### New interface in core

```java
public interface BeanDatasourceProvider {
    String frameworkName();                          // "spring", "solon", ...
    boolean supports(String beanId);                 // can this provider resolve the bean?
    Object getBean(String beanId);                   // lookup
    List<MethodInfo> getMethods(String beanId);      // for designer "select method" UI
    List<Map<String,Object>> invoke(String beanId, String methodName,
            String datasetName, Map<String,Object> params);  // runtime
}
```

`MethodInfo` is a small DTO (`name`, `returnType`, parameter types) used by
the designer UI.

### Core / console changes

- **`SpringBeanDatasourceDefinition`** (core) is rewritten: instead of
  `applicationContext.getBean(beanId)`, iterate
  `UReportEngine.getInstance().getBeanDatasourceRegistry().all()` and call
  the first provider whose `supports(beanId)` returns true. Class name
  stays the same for XML/report-definition compatibility (it's referenced
  by name in saved report files), but the class no longer touches Spring.
- **`DatasourceServletAction`** (console) — the designer "load methods"
  call — uses the same registry.

### Spring Boot bridge

The `ureport2-spring-boot-starter` ships `SpringBeanDatasourceProvider`
(depending on `ApplicationContext`) and registers it programmatically into
`BeanDatasourceRegistry` during auto-config (not via `ServiceLoader`, since
it needs the live `ApplicationContext`). See Section 7.

### Non-Spring frameworks

Solon / JFinal / Quarkus users implement `BeanDatasourceProvider` themselves
(~20 lines) and register it via `META-INF/services/...` or programmatically.
Documented in README.

---

## 6. JDBC & Resource Loading Replacements

### Spring JDBC → `UReportJdbcUtils` (new, core, ~80 lines)

- `parseNamedSql(String sql) → ParsedSql` (replaces
  `NamedParameterUtils.parseSqlStatement`) — splits `:param` / `&param`
  into `?` placeholders + ordered param-name list.
- `queryForList(Connection conn, String sql, Map<String,Object> params)
  → List<Map<String,Object>>` (replaces
  `NamedParameterJdbcTemplate.queryForList`) using a plain
  `PreparedStatement`.
- `buildValueArray(ParsedSql, Map<String,Object> params) → Object[]`
  (replaces `NamedParameterUtils.buildValueArray`).
- `closeQuietly(Connection / Statement / ResultSet)` (replaces
  `JdbcUtils.close*`).
- `PreparedStatementCreator` / `PreparedStatementCreatorFactory` /
  `PreparedStatementCallback` usages in `DatasourceServletAction` collapse
  into direct `PreparedStatement` calls using the helper.

**Affected files:**
- `ureport2-core/.../definition/dataset/SqlDatasetDefinition.java`
- `ureport2-console/.../designer/DatasourceServletAction.java`
- `ureport2-core/.../utils/ProcedureUtils.java`

`SingleConnectionDataSource` is dropped — every call site already has a
raw `Connection`, so we use it directly.

### Resource loading → `UReportResourceUtils` (new, core)

- `getResourceAsStream(String path, ServletContext)`:
  - path starts with `classpath:` → `ClassLoader.getResourceAsStream(...)`
  - else if `ServletContext` available → `ServletContext.getResourceAsStream(...)`
  - else treat as file path.
- Replaces the 6 `ApplicationContext.getResource()` call sites and the
  `ResourceUtils.CLASSPATH_URL_PREFIX` constant usage.
- `FileReportProvider` and `DefaultImageProvider` get `ServletContext` from
  `UReportEngine.getInstance().getServletContext()` (registered during init)
  instead of casting to `WebApplicationContext`.

**Affected files (resource loading):**
- `ureport2-core/.../image/StaticImageProcessor.java`
- `ureport2-core/.../export/pdf/font/FontBuilder.java`
- `ureport2-core/.../provider/report/classpath/ClasspathReportProvider.java`
- `ureport2-core/.../provider/report/file/FileReportProvider.java`
- `ureport2-core/.../provider/image/DefaultImageProvider.java`
- `ureport2-console/.../res/ResourceLoaderServletAction.java`

---

## 7. Web Bootstrap

### `UReportServlet` rewrite (console)

- Drop imports of `org.springframework.web.context.WebApplicationContext`
  and `org.springframework.web.context.support.WebApplicationContextUtils`.
- In `init(ServletConfig)`:
  1. Call
     `UReportEngine.initialize(loadPropertiesFromInitParams(config), config.getServletContext())`
     — idempotent.
  2. Discover `ServletAction` beans via
     `UReportEngine.getInstance().getServletActionRegistry().all()`
     instead of `applicationContext.getBeansOfType(ServletAction.class)`.
- No `@WebServlet` added (preserves the current
  "consumer registers the servlet" contract).
- `loadPropertiesFromInitParams(config)` collects any `servlet`/`context`
  init params starting with `ureport.` into a `Properties` instance, so
  non-Spring consumers can still override config without editing
  `ureport.properties`.

### Consumer migration — non-Spring

- Add `ureport2-console` dependency.
- Register `UReportServlet` at `/ureport/*` (web.xml or
  `@WebServlet`/`ServletContainerInitializer` depending on container).
- The servlet self-initializes on first request (or via an optional
  `UReportServletContextListener` for explicit ordering).
- Custom plugins register via `META-INF/services/<SPI-interface>`.

### Consumer migration — Spring Boot 3.x+

- Add `ureport2-spring-boot-starter` dependency. Done. (See Section 8.)

### Consumer migration — plain Spring 6 (non-Boot)

- **Not officially supported.** Implement `BeanDatasourceProvider` yourself
  (~20 lines, documented in README) and register it programmatically.
  Servlet registration is the same as the non-Spring path.

---

## 8. Spring Boot Starter

`ureport2-spring-boot-starter` provides full auto-configuration. Users add
the dependency and start the app — no `web.xml`, no listener, no manual
bootstrap.

### Contents (3 Java files + 1 imports file + 1 pom)

1. **`UReportProperties.java`** — `@ConfigurationProperties(prefix = "ureport")`:

   ```java
   @ConfigurationProperties(prefix = "ureport")
   public class UReportProperties {
       private String fileStoreDir = "/WEB-INF/ureportfiles";
       private boolean debug = true;
       private boolean disableFileProvider = false;
       private boolean disableHttpSessionReportCache = false;
       // getters/setters
       public Properties toProperties() { /* assemble java.util.Properties */ }
   }
   ```

2. **`UReportAutoConfiguration.java`** — `@AutoConfiguration`:

   ```java
   @AutoConfiguration
   @EnableConfigurationProperties(UReportProperties.class)
   @ConditionalOnWebApplication
   public class UReportAutoConfiguration {

       @Bean
       public UReportEngine ureportEngine(UReportProperties props,
               ServletContext servletContext, ApplicationContext ctx) {
           UReportEngine engine = UReportEngine.initialize(
               props.toProperties(), servletContext);   // idempotent
           engine.getBeanDatasourceRegistry()
                 .register(new SpringBeanDatasourceProvider(ctx));
           return engine;
       }

       @Bean
       public ServletRegistrationBean<UReportServlet> ureportServlet() {
           return new ServletRegistrationBean<>(new UReportServlet(), "/ureport/*");
       }
   }
   ```

   - `@ConditionalOnWebApplication` — starter only activates in web apps.
   - `UReportEngine.initialize(...)` is idempotent, so the `@Bean` call and
     any later `UReportServlet.init()` self-init do not conflict.
   - `ServletContext` is injectable as a resolvable dependency in Spring
     Boot's embedded container.

3. **`SpringBeanDatasourceProvider.java`** — implements the core SPI;
   delegates `getBean(beanId)` / `getMethods(beanId)` to the injected
   `ApplicationContext`. Reflection handles `invoke(...)` (method must have
   signature `(String, String, Map)` per current docs).

4. **`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`**
   — single line:

   ```
   com.bstek.ureport.spring.boot.autoconfigure.UReportAutoConfiguration
   ```

### User integration (entire README for Spring Boot users)

```xml
<dependency>
    <groupId>io.github.jiangood</groupId>
    <artifactId>ureport2-spring-boot-starter</artifactId>
    <version>4.0.0</version>
</dependency>
```

Add the dependency. Start the app. Visit
`http://localhost:8080/ureport/designer`.

### Optional `application.yml` knobs

```yaml
ureport:
  file-store-dir: /WEB-INF/ureportfiles
  debug: true
  disable-file-provider: false
  disable-http-session-report-cache: false
```

---

## 9. Files Touched (summary)

### Modified (21 Java files)

**ureport2-core (16):**
- `Utils.java`, `UReportPropertyPlaceholderConfigurer.java` (deleted),
  `build/ReportBuilder.java`, `build/Context.java`, `cache/CacheUtils.java`,
  `export/ReportRender.java`, `export/pdf/font/FontBuilder.java`,
  `expression/ExpressionUtils.java`, `image/StaticImageProcessor.java`,
  `parser/impl/searchform/FormParserUtils.java`,
  `definition/datasource/SpringBeanDatasourceDefinition.java`,
  `definition/dataset/SqlDatasetDefinition.java`,
  `provider/report/file/FileReportProvider.java`,
  `provider/report/classpath/ClasspathReportProvider.java`,
  `provider/image/DefaultImageProvider.java`,
  `utils/ProcedureUtils.java`.

**ureport2-console (5):**
- `UReportServlet.java`, `RenderPageServletAction.java`,
  `designer/DesignerServletAction.java`,
  `designer/DatasourceServletAction.java`,
  `res/ResourceLoaderServletAction.java`.

### Deleted (3 XML files + 1 Java file)
- `ureport2-core/src/main/resources/ureport-core-context.xml`
- `ureport2-console/src/main/resources/ureport-console-context.xml`
- `ureport2-font/src/main/resources/ureport-font-context.xml`
- `ureport2-core/.../UReportPropertyPlaceholderConfigurer.java`

### New Java files
- `ureport2-core`: `UReportEngine.java`, 10 registry classes
  (`*Registry.java`), `UReportJdbcUtils.java`, `UReportResourceUtils.java`,
  `bootstrap/CoreBootstrap.java`, `BeanDatasourceProvider.java`,
  `MethodInfo.java` (DTO).
- `ureport2-console`: `bootstrap/ConsoleBootstrap.java`.
- `ureport2-font`: `bootstrap/FontBootstrap.java`.
- `ureport2-spring-boot-starter` (new module): `pom.xml`,
  `UReportProperties.java`, `UReportAutoConfiguration.java`,
  `SpringBeanDatasourceProvider.java`,
  `META-INF/spring/...AutoConfiguration.imports`.

### Built-in registration strategy (no `META-INF/services` for built-ins)
- The `*Bootstrap` classes register built-in implementations **directly**
  into the registries during `UReportEngine.initialize()`.
- `ServiceLoader` is used **only** to discover user-supplied plugins; it
  runs after the built-in registration step.
- No `META-INF/services` files are shipped for built-in impls. This avoids
  requiring public no-arg constructors on internal classes and keeps
  built-in wiring explicit/visible in the bootstrap classes.
- User plugins ship their own `META-INF/services/<SPI-interface>` files as
  normal.

### Modified poms (4)
- `ureport2-parent/pom.xml` (modules + description + revision)
- `ureport2-core/pom.xml` (remove spring-* deps)
- `ureport2-console/pom.xml` (unchanged deps, but parent revision bump)
- `ureport2-font/pom.xml` (unchanged deps, parent revision bump)

### Docs
- `README.md`, `README-zh_CN.md` — remove "基于Spring架构", remove
  `ContextLoaderListener` instructions, add Spring Boot starter
  instructions + non-Spring bootstrap instructions + SPI registration.
- `docs/STORAGE-DATASOURCE.md` — rewrite the Spring-bean datasource section
  to use `BeanDatasourceProvider` SPI + `ureport2-spring-boot-starter`;
  remove `ApplicationContextAware` code examples.

---

## 10. Out of Scope

- No test framework introduced (project has zero tests today; adding tests
  is a separate effort).
- No change to the report-definition XML schema (`ureport2.xsd`) — saved
  reports remain compatible.
- No change to the designer UI / front-end (`ureport2-js` not touched).
- No migration tool for old Spring XML configs — users do a clean migration
  per the README.
- Plain Spring 6 (non-Boot) is not officially supported; documented as
  DIY via `BeanDatasourceProvider`.

---

## 11. Risks & Mitigations

| Risk | Mitigation |
|---|---|
| Breaking every existing consumer (hard break) | Bump to 4.0.0; provide turn-key Spring Boot starter so the most common path is one-line migration. Document non-Spring path. |
| `ServiceLoader` ordering is non-deterministic | Registries preserve insertion order (built-ins first, then ServiceLoader in iteration order); where order matters (e.g. `ReportProvider` lookup by name) use `getByName` not position. |
| `UReportEngine.initialize` called twice (Boot `@Bean` + servlet `init`) | Idempotent: first call wins, later calls return existing instance. |
| Named-parameter SQL reimplementation bugs | Port `NamedParameterUtils` logic faithfully; add focused unit tests for `parseNamedSql` + `buildValueArray` + `queryForList` (this is one of the few spots where tests are worth adding despite the "no tests" out-of-scope note). |
| `SpringBeanDatasourceDefinition` class name kept but no longer Spring — confusing | Document clearly; the class name is preserved for saved-report XML compatibility. |
