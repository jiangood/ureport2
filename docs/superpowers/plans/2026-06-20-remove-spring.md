# Remove Spring from ureport2 — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make ureport2-core/console/font 100% Spring-free and framework-agnostic (v4.0.0 hard break), with a new `ureport2-spring-boot-starter` providing full Spring Boot 3.x+ auto-configuration.

**Architecture:** Replace Spring's `ApplicationContext` + 3 XML context files with a ureport-owned `UReportEngine` facade holding per-SPI `Registry<T>` collections. Built-in beans are wired by three programmatic `*Bootstrap` classes (one per module, discovered via `ServiceLoader<UReportBootstrap>`). User plugins are discovered via `ServiceLoader` per SPI. Spring JDBC is replaced by a small `UReportJdbcUtils` named-parameter helper. Resource loading goes through `UReportResourceUtils`. The "Spring Bean Datasource" feature becomes a framework-agnostic `BeanDatasourceProvider` SPI, with the Spring Boot starter shipping the Spring-backed implementation.

**Tech Stack:** Java 17+, Maven 3.9+, Jakarta Servlet 6, Spring Boot 3.x (starter only, optional), JUnit 5 (one focused test for the JDBC helper).

**Spec:** `docs/superpowers/specs/2026-06-20-remove-spring-design.md`

---

## File Structure

### New files (core)
- `ureport2-core/src/main/java/com/bstek/ureport/registry/Registry.java` — generic ordered registry.
- `ureport2-core/src/main/java/com/bstek/ureport/bootstrap/UReportBootstrap.java` — module-init interface.
- `ureport2-core/src/main/java/com/bstek/ureport/UReportEngine.java` — central facade/singleton.
- `ureport2-core/src/main/java/com/bstek/ureport/bootstrap/CoreBootstrap.java` — wires ~80 core built-ins.
- `ureport2-core/src/main/java/com/bstek/ureport/utils/UReportJdbcUtils.java` — named-param SQL + closeQuietly.
- `ureport2-core/src/main/java/com/bstek/ureport/utils/UReportJdbcUtilsTest.java` — focused unit test.
- `ureport2-core/src/main/java/com/bstek/ureport/utils/UReportResourceUtils.java` — classpath/web resource loading.
- `ureport2-core/src/main/java/com/bstek/ureport/utils/ParsedSql.java` — DTO for named-param parse result.
- `ureport2-core/src/main/java/com/bstek/ureport/definition/datasource/BeanDatasourceProvider.java` — new SPI.
- `ureport2-core/src/main/java/com/bstek/ureport/definition/datasource/MethodInfo.java` — DTO.
- `ureport2-core/src/main/resources/META-INF/services/com.bstek.ureport.bootstrap.UReportBootstrap` — registers `CoreBootstrap`.

### New files (font)
- `ureport2-font/src/main/java/com/bstek/ureport/font/bootstrap/FontBootstrap.java` — wires 9 FontRegister beans.
- `ureport2-font/src/main/resources/META-INF/services/com.bstek.ureport.bootstrap.UReportBootstrap` — registers `FontBootstrap`.

### New files (console)
- `ureport2-console/src/main/java/com/bstek/ureport/console/bootstrap/ConsoleBootstrap.java` — wires 13 ServletAction beans.
- `ureport2-console/src/main/resources/META-INF/services/com.bstek.ureport.bootstrap.UReportBootstrap` — registers `ConsoleBootstrap`.

### New module (starter)
- `ureport2-spring-boot-starter/pom.xml`
- `ureport2-spring-boot-starter/src/main/java/com/bstek/ureport/spring/boot/autoconfigure/UReportProperties.java`
- `ureport2-spring-boot-starter/src/main/java/com/bstek/ureport/spring/boot/autoconfigure/UReportAutoConfiguration.java`
- `ureport2-spring-boot-starter/src/main/java/com/bstek/ureport/spring/boot/bean/SpringBeanDatasourceProvider.java`
- `ureport2-spring-boot-starter/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

### Deleted
- `ureport2-core/src/main/java/com/bstek/ureport/UReportPropertyPlaceholderConfigurer.java`
- `ureport2-core/src/main/resources/ureport-core-context.xml`
- `ureport2-console/src/main/resources/ureport-console-context.xml`
- `ureport2-font/src/main/resources/ureport-font-context.xml`

### Modified (21 Java + 4 pom + 3 docs)
See spec Section 9 for the full list. Order below is chosen so the project compiles after every task; the Spring dependencies are only removed in Task 18 (after every `org.springframework` import is gone from `.java` files).

---

## Task 1: Create registry, bootstrap interface, and UReportEngine facade

**Files:**
- Create: `ureport2-core/src/main/java/com/bstek/ureport/registry/Registry.java`
- Create: `ureport2-core/src/main/java/com/bstek/ureport/bootstrap/UReportBootstrap.java`
- Create: `ureport2-core/src/main/java/com/bstek/ureport/UReportEngine.java`
- Create: `ureport2-core/src/main/java/com/bstek/ureport/definition/datasource/BeanDatasourceProvider.java`
- Create: `ureport2-core/src/main/java/com/bstek/ureport/definition/datasource/MethodInfo.java`

- [ ] **Step 1: Create `Registry.java`**

```java
package com.bstek.ureport.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Registry<T> {
	private final List<T> items = new ArrayList<T>();
	private final Map<String, T> byName = new LinkedHashMap<String, T>();

	public void register(T item) {
		items.add(item);
	}

	public void register(String name, T item) {
		byName.put(name, item);
		items.add(item);
	}

	public List<T> all() {
		return Collections.unmodifiableList(items);
	}

	public T getByName(String name) {
		return byName.get(name);
	}
}
```

- [ ] **Step 2: Create `UReportBootstrap.java`**

```java
package com.bstek.ureport.bootstrap;

import com.bstek.ureport.UReportEngine;

public interface UReportBootstrap {
	void bootstrap(UReportEngine engine);
	int order();
}
```

- [ ] **Step 3: Create `MethodInfo.java`**

```java
package com.bstek.ureport.definition.datasource;

import java.util.List;

public class MethodInfo {
	private String name;
	private Class<?> returnType;
	private List<Class<?>> parameterTypes;
	public MethodInfo(String name, Class<?> returnType, List<Class<?>> parameterTypes) {
		this.name = name;
		this.returnType = returnType;
		this.parameterTypes = parameterTypes;
	}
	public String getName() { return name; }
	public Class<?> getReturnType() { return returnType; }
	public List<Class<?>> getParameterTypes() { return parameterTypes; }
}
```

- [ ] **Step 4: Create `BeanDatasourceProvider.java`**

```java
package com.bstek.ureport.definition.datasource;

import java.util.List;
import java.util.Map;

public interface BeanDatasourceProvider {
	String frameworkName();
	boolean supports(String beanId);
	Object getBean(String beanId);
	List<MethodInfo> getMethods(String beanId);
	List<Map<String, Object>> invoke(String beanId, String methodName, String datasetName, Map<String, Object> params);
}
```

- [ ] **Step 5: Create `UReportEngine.java`**

```java
package com.bstek.ureport;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;

import com.bstek.ureport.bootstrap.UReportBootstrap;
import com.bstek.ureport.cache.ReportCache;
import com.bstek.ureport.cache.ReportDefinitionCache;
import com.bstek.ureport.definition.datasource.BeanDatasourceProvider;
import com.bstek.ureport.definition.datasource.BuildinDatasource;
import com.bstek.ureport.definition.datasource.DatasourceProvider;
import com.bstek.ureport.export.ExportManager;
import com.bstek.ureport.export.ReportRender;
import com.bstek.ureport.export.pdf.font.FontRegister;
import com.bstek.ureport.build.ReportBuilder;
import com.bstek.ureport.console.ServletAction;
import com.bstek.ureport.expression.function.Function;
import com.bstek.ureport.parser.ReportParser;
import com.bstek.ureport.parser.impl.searchform.FormParser;
import com.bstek.ureport.provider.image.ImageProvider;
import com.bstek.ureport.provider.report.ReportProvider;
import com.bstek.ureport.registry.Registry;

import jakarta.servlet.ServletContext;

public final class UReportEngine {
	private static UReportEngine instance;

	private final Properties config;
	private final ServletContext servletContext;

	private final Registry<BuildinDatasource> buildinDatasourceRegistry = new Registry<BuildinDatasource>();
	private final Registry<ImageProvider> imageProviderRegistry = new Registry<ImageProvider>();
	private final Registry<ReportProvider> reportProviderRegistry = new Registry<ReportProvider>();
	private final Registry<ReportCache> reportCacheRegistry = new Registry<ReportCache>();
	private final Registry<ReportDefinitionCache> reportDefinitionCacheRegistry = new Registry<ReportDefinitionCache>();
	private final Registry<DatasourceProvider> datasourceProviderRegistry = new Registry<DatasourceProvider>();
	private final Registry<Function> functionRegistry = new Registry<Function>();
	private final Registry<FormParser<?>> formParserRegistry = new Registry<FormParser<?>>();
	private final Registry<FontRegister> fontRegistry = new Registry<FontRegister>();
	private final Registry<BeanDatasourceProvider> beanDatasourceRegistry = new Registry<BeanDatasourceProvider>();

	private ExportManager exportManager;
	private ReportRender reportRender;
	private ReportBuilder reportBuilder;
	private ReportParser reportParser;

	private UReportEngine(Properties config, ServletContext servletContext) {
		this.config = config;
		this.servletContext = servletContext;
	}

	public static synchronized UReportEngine initialize(Properties config, ServletContext servletContext) {
		if (instance != null) {
			return instance;
		}
		Properties merged = loadDefaultProperties();
		if (config != null) {
			merged.putAll(config);
		}
		UReportEngine engine = new UReportEngine(merged, servletContext);
		engine.runBootstraps();
		engine.loadUserPlugins();
		instance = engine;
		return engine;
	}

	public static UReportEngine getInstance() {
		if (instance == null) {
			throw new IllegalStateException("UReportEngine not initialized. Call initialize(Properties, ServletContext) first.");
		}
		return instance;
	}

	private void runBootstraps() {
		List<UReportBootstrap> bootstraps = new ArrayList<UReportBootstrap>();
		for (UReportBootstrap b : ServiceLoader.load(UReportBootstrap.class)) {
			bootstraps.add(b);
		}
		bootstraps.sort((a, b) -> Integer.compare(a.order(), b.order()));
		for (UReportBootstrap b : bootstraps) {
			b.bootstrap(this);
		}
	}

	private void loadUserPlugins() {
		registerAll(BuildinDatasource.class, buildinDatasourceRegistry);
		registerAll(ImageProvider.class, imageProviderRegistry);
		registerAll(ReportProvider.class, reportProviderRegistry);
		registerAll(ReportCache.class, reportCacheRegistry);
		registerAll(ReportDefinitionCache.class, reportDefinitionCacheRegistry);
		registerAll(DatasourceProvider.class, datasourceProviderRegistry);
		registerAll(Function.class, functionRegistry);
		registerAll(FormParser.class, formParserRegistry);
		registerAll(FontRegister.class, fontRegistry);
		registerAll(BeanDatasourceProvider.class, beanDatasourceRegistry);
	}

	private <T> void registerAll(Class<T> spi, Registry<T> registry) {
		for (T impl : ServiceLoader.load(spi)) {
			registry.register(impl);
		}
	}

	private static Properties loadDefaultProperties() {
		Properties props = new Properties();
		try {
			java.io.InputStream in = UReportEngine.class.getClassLoader().getResourceAsStream("ureport.properties");
			if (in != null) {
				props.load(in);
				in.close();
			}
		} catch (Exception ignored) {
		}
		return props;
	}

	public Properties getConfig() { return config; }
	public ServletContext getServletContext() { return servletContext; }

	public Registry<BuildinDatasource> getBuildinDatasourceRegistry() { return buildinDatasourceRegistry; }
	public Registry<ImageProvider> getImageProviderRegistry() { return imageProviderRegistry; }
	public Registry<ReportProvider> getReportProviderRegistry() { return reportProviderRegistry; }
	public Registry<ReportCache> getReportCacheRegistry() { return reportCacheRegistry; }
	public Registry<ReportDefinitionCache> getReportDefinitionCacheRegistry() { return reportDefinitionCacheRegistry; }
	public Registry<DatasourceProvider> getDatasourceProviderRegistry() { return datasourceProviderRegistry; }
	public Registry<Function> getFunctionRegistry() { return functionRegistry; }
	public Registry<FormParser<?>> getFormParserRegistry() { return formParserRegistry; }
	public Registry<FontRegister> getFontRegistry() { return fontRegistry; }
	public Registry<BeanDatasourceProvider> getBeanDatasourceRegistry() { return beanDatasourceRegistry; }

	public ExportManager getExportManager() { return exportManager; }
	public ReportRender getReportRender() { return reportRender; }
	public ReportBuilder getReportBuilder() { return reportBuilder; }
	public ReportParser getReportParser() { return reportParser; }

	public void setExportManager(ExportManager exportManager) { this.exportManager = exportManager; }
	public void setReportRender(ReportRender reportRender) { this.reportRender = reportRender; }
	public void setReportBuilder(ReportBuilder reportBuilder) { this.reportBuilder = reportBuilder; }
	public void setReportParser(ReportParser reportParser) { this.reportParser = reportParser; }
}
```

- [ ] **Step 6: Verify it compiles**

Run: `mvn -q -pl ureport2-core compile`
Expected: PASS (the new files reference `com.bstek.ureport.console.ServletAction` which is in the console module — see Step 7 note).

Note: `UReportEngine` imports `com.bstek.ureport.console.ServletAction` (console module) and `com.bstek.ureport.export.pdf.font.FontRegister` (font module). Core depends on neither today. To avoid adding circular module deps, drop the `ServletAction` registry and `FontRegister` registry from `UReportEngine` and instead let `ConsoleBootstrap` keep its own servlet-action list and `FontBootstrap` keep its own font list locally. Revise Step 5: remove the `servletActionRegistry` and `fontRegistry` fields + their getters + their `loadUserPlugins` lines, and remove the `ServletAction` and `FontRegister` imports. `FontBuilder` will obtain `FontRegister` instances directly from `ServiceLoader.load(FontRegister.class)` (Task 9). `UReportServlet` will obtain `ServletAction` instances directly from `ServiceLoader.load(ServletAction.class)` merged with `ConsoleBootstrap`'s registered built-ins (Task 17).

- [ ] **Step 7: Apply the revision from Step 6 note**

Edit `UReportEngine.java`: remove the `ServletAction` import, the `FontRegister` import, the `fontRegistry` field, the `servletActionRegistry` field (was not added — confirm absent), the `getFontRegistry()` getter, the `getServletActionRegistry()` getter (was not added), and the two `registerAll(...)` lines for `FontRegister` and `ServletAction` inside `loadUserPlugins()`. Keep `FontRegister` out of `UReportEngine` entirely.

Run: `mvn -q -pl ureport2-core compile`
Expected: PASS

- [ ] **Step 8: Commit**

```bash
git add ureport2-core/src/main/java/com/bstek/ureport/registry/Registry.java ureport2-core/src/main/java/com/bstek/ureport/bootstrap/UReportBootstrap.java ureport2-core/src/main/java/com/bstek/ureport/UReportEngine.java ureport2-core/src/main/java/com/bstek/ureport/definition/datasource/BeanDatasourceProvider.java ureport2-core/src/main/java/com/bstek/ureport/definition/datasource/MethodInfo.java
git commit -m "feat(core): add UReportEngine facade, Registry, BeanDatasourceProvider SPI"
```

---

## Task 2: Create UReportJdbcUtils with focused unit test

**Files:**
- Modify: `ureport2-core/pom.xml` (add JUnit 5 test dependency)
- Create: `ureport2-core/src/main/java/com/bstek/ureport/utils/ParsedSql.java`
- Create: `ureport2-core/src/main/java/com/bstek/ureport/utils/UReportJdbcUtils.java`
- Create: `ureport2-core/src/test/java/com/bstek/ureport/utils/UReportJdbcUtilsTest.java`

- [ ] **Step 1: Add JUnit 5 to `ureport2-core/pom.xml`**

Add inside `<dependencies>` (after the jakarta.servlet-api dependency block, before `</dependencies>`):

```xml
	<dependency>
		<groupId>org.junit.jupiter</groupId>
		<artifactId>junit-jupiter</artifactId>
		<version>5.10.2</version>
		<scope>test</scope>
	</dependency>
```

- [ ] **Step 2: Create `ParsedSql.java`**

```java
package com.bstek.ureport.utils;

import java.util.ArrayList;
import java.util.List;

public class ParsedSql {
	private String sql;
	private final List<String> parameterNames = new ArrayList<String>();
	public String getSql() { return sql; }
	public void setSql(String sql) { this.sql = sql; }
	public List<String> getParameterNames() { return parameterNames; }
	public void addParameter(String name) { parameterNames.add(name); }
}
```

- [ ] **Step 3: Write the failing test `UReportJdbcUtilsTest.java`**

```java
package com.bstek.ureport.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class UReportJdbcUtilsTest {

	@Test
	public void parseNamedSql_replacesColonParamsWithQuestionMarks() {
		ParsedSql parsed = UReportJdbcUtils.parseNamedSql("select * from t where a=:a and b=:b");
		assertEquals("select * from t where a=? and b=?", parsed.getSql());
		assertEquals(2, parsed.getParameterNames().size());
		assertEquals("a", parsed.getParameterNames().get(0));
		assertEquals("b", parsed.getParameterNames().get(1));
	}

	@Test
	public void parseNamedSql_ignoresParamsInsideQuotes() {
		ParsedSql parsed = UReportJdbcUtils.parseNamedSql("select ':a' as x where b=:b");
		assertEquals("select ':a' as x where b=?", parsed.getSql());
		assertEquals(1, parsed.getParameterNames().size());
		assertEquals("b", parsed.getParameterNames().get(0));
	}

	@Test
	public void parseNamedSql_keepsPositionalQuestionMarks() {
		ParsedSql parsed = UReportJdbcUtils.parseNamedSql("select * from t where a=? and b=:b");
		assertEquals("select * from t where a=? and b=?", parsed.getSql());
		assertEquals(1, parsed.getParameterNames().size());
		assertEquals("b", parsed.getParameterNames().get(0));
	}

	@Test
	public void buildValueArray_ordersValuesByParameterNames() {
		ParsedSql parsed = new ParsedSql();
		parsed.setSql("where a=? and b=?");
		parsed.addParameter("a");
		parsed.addParameter("b");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("a", 1);
		params.put("b", "two");
		Object[] values = UReportJdbcUtils.buildValueArray(parsed, params);
		assertArrayEquals(new Object[]{1, "two"}, values);
	}
}
```

- [ ] **Step 4: Run the test to verify it fails**

Run: `mvn -q -pl ureport2-core test -Dtest=UReportJdbcUtilsTest`
Expected: FAIL (compilation error — `UReportJdbcUtils` does not exist)

- [ ] **Step 5: Create `UReportJdbcUtils.java`**

```java
package com.bstek.ureport.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UReportJdbcUtils {

	public static ParsedSql parseNamedSql(String sql) {
		ParsedSql parsed = new ParsedSql();
		StringBuilder sqlToUse = new StringBuilder(sql.length());
		char[] chars = sql.toCharArray();
		int i = 0;
		while (i < chars.length) {
			char c = chars[i];
			if (c == '\'' || c == '"') {
				char quote = c;
				sqlToUse.append(c);
				i++;
				while (i < chars.length) {
					char q = chars[i];
					sqlToUse.append(q);
					if (q == quote) {
						if (i + 1 < chars.length && chars[i + 1] == quote) {
							sqlToUse.append(chars[i + 1]);
							i += 2;
							continue;
						}
						i++;
						break;
					}
					i++;
				}
				continue;
			}
			if (c == ':') {
				int j = i + 1;
				if (j < chars.length && chars[j] == ':') {
					sqlToUse.append(c);
					i++;
					continue;
				}
				StringBuilder name = new StringBuilder();
				while (j < chars.length && Character.isJavaIdentifierPart(chars[j])) {
					name.append(chars[j]);
					j++;
				}
				if (name.length() == 0) {
					sqlToUse.append(c);
					i++;
					continue;
				}
				parsed.addParameter(name.toString());
				sqlToUse.append('?');
				i = j;
				continue;
			}
			sqlToUse.append(c);
			i++;
		}
		parsed.setSql(sqlToUse.toString());
		return parsed;
	}

	public static Object[] buildValueArray(ParsedSql parsedSql, Map<String, Object> params) {
		List<String> names = parsedSql.getParameterNames();
		Object[] values = new Object[names.size()];
		for (int k = 0; k < names.size(); k++) {
			values[k] = params == null ? null : params.get(names.get(k));
		}
		return values;
	}

	public static List<Map<String, Object>> queryForList(Connection conn, String sql, Map<String, Object> params) {
		ParsedSql parsed = parseNamedSql(sql);
		Object[] values = buildValueArray(parsed, params);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(parsed.getSql());
			for (int k = 0; k < values.length; k++) {
				ps.setObject(k + 1, values[k]);
			}
			rs = ps.executeQuery();
			ResultSetMetaData metadata = rs.getMetaData();
			int columnCount = metadata.getColumnCount();
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			while (rs.next()) {
				Map<String, Object> row = new LinkedHashMap<String, Object>();
				for (int c = 1; c <= columnCount; c++) {
					row.put(metadata.getColumnLabel(c), rs.getObject(c));
				}
				result.add(row);
			}
			return result;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			closeQuietly(rs);
			closeQuietly(ps);
		}
	}

	public static void closeQuietly(Connection conn) {
		if (conn != null) {
			try { conn.close(); } catch (SQLException ignored) {}
		}
	}

	public static void closeQuietly(Statement stmt) {
		if (stmt != null) {
			try { stmt.close(); } catch (SQLException ignored) {}
		}
	}

	public static void closeQuietly(ResultSet rs) {
		if (rs != null) {
			try { rs.close(); } catch (SQLException ignored) {}
		}
	}
}
```

- [ ] **Step 6: Run the test to verify it passes**

Run: `mvn -q -pl ureport2-core test -Dtest=UReportJdbcUtilsTest`
Expected: PASS — `Tests run: 4, Failures: 0`

- [ ] **Step 7: Commit**

```bash
git add ureport2-core/pom.xml ureport2-core/src/main/java/com/bstek/ureport/utils/ParsedSql.java ureport2-core/src/main/java/com/bstek/ureport/utils/UReportJdbcUtils.java ureport2-core/src/test/java/com/bstek/ureport/utils/UReportJdbcUtilsTest.java
git commit -m "feat(core): add UReportJdbcUtils named-parameter helper with tests"
```

---

## Task 3: Create UReportResourceUtils

**Files:**
- Create: `ureport2-core/src/main/java/com/bstek/ureport/utils/UReportResourceUtils.java`

- [ ] **Step 1: Create `UReportResourceUtils.java`**

```java
package com.bstek.ureport.utils;

import java.io.InputStream;

import jakarta.servlet.ServletContext;

public class UReportResourceUtils {
	public static final String CLASSPATH_URL_PREFIX = "classpath:";

	public static InputStream getResourceAsStream(String path, ServletContext servletContext) {
		if (path == null) {
			return null;
		}
		if (path.startsWith(CLASSPATH_URL_PREFIX)) {
			String resourcePath = path.substring(CLASSPATH_URL_PREFIX.length());
			ClassLoader cl = UReportResourceUtils.class.getClassLoader();
			InputStream in = cl.getResourceAsStream(resourcePath);
			if (in == null) {
				in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
			}
			return in;
		}
		if (servletContext != null) {
			InputStream in = servletContext.getResourceAsStream(path);
			if (in != null) {
				return in;
			}
		}
		return null;
	}
}
```

- [ ] **Step 2: Verify it compiles**

Run: `mvn -q -pl ureport2-core compile`
Expected: PASS

- [ ] **Step 3: Commit**

```bash
git add ureport2-core/src/main/java/com/bstek/ureport/utils/UReportResourceUtils.java
git commit -m "feat(core): add UReportResourceUtils for classpath/web resource loading"
```

---

## Task 4: Create CoreBootstrap and register it via ServiceLoader

**Files:**
- Create: `ureport2-core/src/main/java/com/bstek/ureport/bootstrap/CoreBootstrap.java`
- Create: `ureport2-core/src/main/resources/META-INF/services/com.bstek.ureport.bootstrap.UReportBootstrap`

- [ ] **Step 1: Create `CoreBootstrap.java`**

This wires all ~80 built-in beans from `ureport-core-context.xml` programmatically.

```java
package com.bstek.ureport.bootstrap;

import java.util.List;

import com.bstek.ureport.UReportEngine;
import com.bstek.ureport.Utils;
import com.bstek.ureport.cache.CacheUtils;
import com.bstek.ureport.expression.ExpressionUtils;
import com.bstek.ureport.expression.function.CountFunction;
import com.bstek.ureport.expression.function.SumFunction;
import com.bstek.ureport.expression.function.MaxFunction;
import com.bstek.ureport.expression.function.MinFunction;
import com.bstek.ureport.expression.function.ListFunction;
import com.bstek.ureport.expression.function.AvgFunction;
import com.bstek.ureport.expression.function.OrderFunction;
import com.bstek.ureport.expression.function.date.WeekFunction;
import com.bstek.ureport.expression.function.date.DayFunction;
import com.bstek.ureport.expression.function.date.MonthFunction;
import com.bstek.ureport.expression.function.date.YearFunction;
import com.bstek.ureport.expression.function.date.DateFunction;
import com.bstek.ureport.expression.function.FormatNumberFunction;
import com.bstek.ureport.expression.function.FormatDateFunction;
import com.bstek.ureport.expression.function.GetFunction;
import com.bstek.ureport.expression.function.math.AbsFunction;
import com.bstek.ureport.expression.function.math.CeilFunction;
import com.bstek.ureport.expression.function.math.ChnFunction;
import com.bstek.ureport.expression.function.math.ChnMoneyFunction;
import com.bstek.ureport.expression.function.math.CosFunction;
import com.bstek.ureport.expression.function.math.ExpFunction;
import com.bstek.ureport.expression.function.math.FloorFunction;
import com.bstek.ureport.expression.function.math.Log10Function;
import com.bstek.ureport.expression.function.math.LogFunction;
import com.bstek.ureport.expression.function.math.PowFunction;
import com.bstek.ureport.expression.function.math.RandomFunction;
import com.bstek.ureport.expression.function.math.RoundFunction;
import com.bstek.ureport.expression.function.math.SinFunction;
import com.bstek.ureport.expression.function.math.SqrtFunction;
import com.bstek.ureport.expression.function.math.TanFunction;
import com.bstek.ureport.expression.function.math.StdevpFunction;
import com.bstek.ureport.expression.function.math.VaraFunction;
import com.bstek.ureport.expression.function.math.ModeFunction;
import com.bstek.ureport.expression.function.math.MedianFunction;
import com.bstek.ureport.expression.function.string.LengthFunction;
import com.bstek.ureport.expression.function.string.LowerFunction;
import com.bstek.ureport.expression.function.string.IndexOfFunction;
import com.bstek.ureport.expression.function.string.ReplaceFunction;
import com.bstek.ureport.expression.function.string.SubstringFunction;
import com.bstek.ureport.expression.function.string.TrimFunction;
import com.bstek.ureport.expression.function.string.UpperFunction;
import com.bstek.ureport.expression.function.page.PageTotalFunction;
import com.bstek.ureport.expression.function.page.PageNumberFunction;
import com.bstek.ureport.expression.function.page.PageAvgFunction;
import com.bstek.ureport.expression.function.page.PageCountFunction;
import com.bstek.ureport.expression.function.page.PageMaxFunction;
import com.bstek.ureport.expression.function.page.PageMinFunction;
import com.bstek.ureport.expression.function.page.PageRowsFunction;
import com.bstek.ureport.expression.function.page.PageSumFunction;
import com.bstek.ureport.expression.function.ParameterFunction;
import com.bstek.ureport.expression.function.ParameterIsEmptyFunction;
import com.bstek.ureport.expression.function.JsonFunction;
import com.bstek.ureport.expression.function.RowFunction;
import com.bstek.ureport.expression.function.ColumnFunction;
import com.bstek.ureport.export.ExportManagerImpl;
import com.bstek.ureport.export.ReportRender;
import com.bstek.ureport.export.pdf.font.FontBuilder;
import com.bstek.ureport.build.HideRowColumnBuilder;
import com.bstek.ureport.build.ReportBuilder;
import com.bstek.ureport.parser.ReportParser;
import com.bstek.ureport.parser.impl.searchform.FormParserUtils;
import com.bstek.ureport.parser.impl.searchform.CheckboxParser;
import com.bstek.ureport.parser.impl.searchform.GridParser;
import com.bstek.ureport.parser.impl.searchform.RadioInputParser;
import com.bstek.ureport.parser.impl.searchform.TextInputParser;
import com.bstek.ureport.parser.impl.searchform.ResetButtonParser;
import com.bstek.ureport.parser.impl.searchform.SubmitButtonParser;
import com.bstek.ureport.parser.impl.searchform.SelectInputParser;
import com.bstek.ureport.parser.impl.searchform.DatetimeInputParser;
import com.bstek.ureport.provider.image.DefaultImageProvider;
import com.bstek.ureport.provider.image.HttpImageProvider;
import com.bstek.ureport.provider.image.HttpsImageProvider;
import com.bstek.ureport.provider.report.file.FileReportProvider;
import com.bstek.ureport.provider.report.classpath.ClasspathReportProvider;

public class CoreBootstrap implements UReportBootstrap {

	@Override
	public int order() {
		return 0;
	}

	@Override
	public void bootstrap(UReportEngine engine) {
		Utils utils = new Utils();
		utils.setDebug(Boolean.parseBoolean(engine.getConfig().getProperty("ureport.debug", "true")));
		engine.getImageProviderRegistry().register(new DefaultImageProvider());
		engine.getImageProviderRegistry().register(new HttpImageProvider());
		engine.getImageProviderRegistry().register(new HttpsImageProvider());

		FileReportProvider fileReportProvider = new FileReportProvider();
		fileReportProvider.setFileStoreDir(engine.getConfig().getProperty("ureport.fileStoreDir", "/WEB-INF/ureportfiles"));
		fileReportProvider.setDisabled(Boolean.parseBoolean(engine.getConfig().getProperty("ureport.disableFileProvider", "false")));
		engine.getReportProviderRegistry().register(fileReportProvider);
		engine.getReportProviderRegistry().register(new ClasspathReportProvider());

		ReportParser reportParser = new ReportParser();
		HideRowColumnBuilder hideRowColumnBuilder = new HideRowColumnBuilder();
		ReportBuilder reportBuilder = new ReportBuilder();
		reportBuilder.setHideRowColumnBuilder(hideRowColumnBuilder);
		ReportRender reportRender = new ReportRender();
		reportRender.setReportParser(reportParser);
		reportRender.setReportBuilder(reportBuilder);
		ExportManagerImpl exportManager = new ExportManagerImpl();
		exportManager.setReportRender(reportRender);

		engine.setReportParser(reportParser);
		engine.setReportBuilder(reportBuilder);
		engine.setReportRender(reportRender);
		engine.setExportManager(exportManager);

		new FontBuilder();
		new CacheUtils();
		new ExpressionUtils();
		new FormParserUtils();

		engine.getFormParserRegistry().register(new CheckboxParser());
		engine.getFormParserRegistry().register(new GridParser());
		engine.getFormParserRegistry().register(new RadioInputParser());
		engine.getFormParserRegistry().register(new TextInputParser());
		engine.getFormParserRegistry().register(new ResetButtonParser());
		engine.getFormParserRegistry().register(new SubmitButtonParser());
		engine.getFormParserRegistry().register(new SelectInputParser());
		engine.getFormParserRegistry().register(new DatetimeInputParser());

		engine.getFunctionRegistry().register(new CountFunction());
		engine.getFunctionRegistry().register(new SumFunction());
		engine.getFunctionRegistry().register(new MaxFunction());
		engine.getFunctionRegistry().register(new MinFunction());
		engine.getFunctionRegistry().register(new ListFunction());
		engine.getFunctionRegistry().register(new AvgFunction());
		engine.getFunctionRegistry().register(new OrderFunction());
		engine.getFunctionRegistry().register(new WeekFunction());
		engine.getFunctionRegistry().register(new DayFunction());
		engine.getFunctionRegistry().register(new MonthFunction());
		engine.getFunctionRegistry().register(new YearFunction());
		engine.getFunctionRegistry().register(new DateFunction());
		engine.getFunctionRegistry().register(new FormatNumberFunction());
		engine.getFunctionRegistry().register(new FormatDateFunction());
		engine.getFunctionRegistry().register(new GetFunction());
		engine.getFunctionRegistry().register(new AbsFunction());
		engine.getFunctionRegistry().register(new CeilFunction());
		engine.getFunctionRegistry().register(new ChnFunction());
		engine.getFunctionRegistry().register(new ChnMoneyFunction());
		engine.getFunctionRegistry().register(new CosFunction());
		engine.getFunctionRegistry().register(new ExpFunction());
		engine.getFunctionRegistry().register(new FloorFunction());
		engine.getFunctionRegistry().register(new Log10Function());
		engine.getFunctionRegistry().register(new LogFunction());
		engine.getFunctionRegistry().register(new PowFunction());
		engine.getFunctionRegistry().register(new RandomFunction());
		engine.getFunctionRegistry().register(new RoundFunction());
		engine.getFunctionRegistry().register(new SinFunction());
		engine.getFunctionRegistry().register(new SqrtFunction());
		engine.getFunctionRegistry().register(new TanFunction());
		engine.getFunctionRegistry().register(new StdevpFunction());
		engine.getFunctionRegistry().register(new VaraFunction());
		engine.getFunctionRegistry().register(new ModeFunction());
		engine.getFunctionRegistry().register(new MedianFunction());
		engine.getFunctionRegistry().register(new LengthFunction());
		engine.getFunctionRegistry().register(new LowerFunction());
		engine.getFunctionRegistry().register(new IndexOfFunction());
		engine.getFunctionRegistry().register(new ReplaceFunction());
		engine.getFunctionRegistry().register(new SubstringFunction());
		engine.getFunctionRegistry().register(new TrimFunction());
		engine.getFunctionRegistry().register(new UpperFunction());
		engine.getFunctionRegistry().register(new PageTotalFunction());
		engine.getFunctionRegistry().register(new PageNumberFunction());
		engine.getFunctionRegistry().register(new PageAvgFunction());
		engine.getFunctionRegistry().register(new PageCountFunction());
		engine.getFunctionRegistry().register(new PageMaxFunction());
		engine.getFunctionRegistry().register(new PageMinFunction());
		engine.getFunctionRegistry().register(new PageRowsFunction());
		engine.getFunctionRegistry().register(new PageSumFunction());
		engine.getFunctionRegistry().register(new ParameterFunction());
		engine.getFunctionRegistry().register(new ParameterIsEmptyFunction());
		engine.getFunctionRegistry().register(new JsonFunction());
		engine.getFunctionRegistry().register(new RowFunction());
		engine.getFunctionRegistry().register(new ColumnFunction());

		fileReportProvider.init(engine);
		((DefaultImageProvider) engine.getImageProviderRegistry().all().get(0)).init(engine);
	}
}
```

Note: `new Utils()`, `new FontBuilder()`, `new CacheUtils()`, `new ExpressionUtils()`, `new FormParserUtils()` are instantiated so their static fields initialize from `UReportEngine.getInstance()` — this works because by the time `CoreBootstrap.bootstrap(engine)` runs, `instance` is being set inside `initialize()` but is not yet assigned. To handle this, `UReportEngine.initialize` must set `instance = engine` BEFORE calling `runBootstraps()`. Fix Step 5 of Task 1: in `initialize`, set `instance = engine;` right after constructing `engine` and before `engine.runBootstraps()`. The holder classes (`Utils`/`CacheUtils`/etc., rewritten in Tasks 7-9) will call `UReportEngine.getInstance()` in their constructors. Apply that fix to `UReportEngine.java` now:

- [ ] **Step 2: Fix initialize() ordering in UReportEngine**

In `UReportEngine.java`, change `initialize` so `instance` is assigned before bootstraps run:

```java
	public static synchronized UReportEngine initialize(Properties config, ServletContext servletContext) {
		if (instance != null) {
			return instance;
		}
		Properties merged = loadDefaultProperties();
		if (config != null) {
			merged.putAll(config);
		}
		UReportEngine engine = new UReportEngine(merged, servletContext);
		instance = engine;
		engine.runBootstraps();
		engine.loadUserPlugins();
		return engine;
	}
```

- [ ] **Step 3: Create the ServiceLoader registration file**

Create `ureport2-core/src/main/resources/META-INF/services/com.bstek.ureport.bootstrap.UReportBootstrap` with single line:

```
com.bstek.ureport.bootstrap.CoreBootstrap
```

- [ ] **Step 4: Verify it compiles**

Run: `mvn -q -pl ureport2-core compile`
Expected: PASS (the holder classes still use Spring at this point, but `CoreBootstrap` only constructs them; unused `List` import is fine). If the `fileReportProvider.init(engine)` / `DefaultImageProvider.init(engine)` calls fail to compile because those `init` methods don't exist yet, they are added in Task 13 — remove those two lines from `CoreBootstrap` now and re-add them in Task 13's final step.

- [ ] **Step 5: Commit**

```bash
git add ureport2-core/src/main/java/com/bstek/ureport/bootstrap/CoreBootstrap.java ureport2-core/src/main/resources/META-INF/services/com.bstek.ureport.bootstrap.UReportBootstrap ureport2-core/src/main/java/com/bstek/ureport/UReportEngine.java
git commit -m "feat(core): add CoreBootstrap wiring built-in beans via ServiceLoader"
```

---

## Task 5: Create FontBootstrap

**Files:**
- Create: `ureport2-font/src/main/java/com/bstek/ureport/font/bootstrap/FontBootstrap.java`
- Create: `ureport2-font/src/main/resources/META-INF/services/com.bstek.ureport.bootstrap.UReportBootstrap`

- [ ] **Step 1: Create `FontBootstrap.java`**

```java
package com.bstek.ureport.font.bootstrap;

import com.bstek.ureport.UReportEngine;
import com.bstek.ureport.bootstrap.UReportBootstrap;
import com.bstek.ureport.export.pdf.font.FontRegister;
import com.bstek.ureport.font.arial.ArialFontRegister;
import com.bstek.ureport.font.comicsansms.ComicSansMSFontRegister;
import com.bstek.ureport.font.couriernew.CourierNewFontRegister;
import com.bstek.ureport.font.fangsong.FangSongFontRegister;
import com.bstek.ureport.font.heiti.HeiTiFontRegister;
import com.bstek.ureport.font.kaiti.KaiTiFontRegister;
import com.bstek.ureport.font.songti.SongTiFontRegister;
import com.bstek.ureport.font.timesnewroman.TimesNewRomanFontRegister;
import com.bstek.ureport.font.yahei.YaheiFontRegister;

public class FontBootstrap implements UReportBootstrap {
	@Override
	public int order() { return 10; }
	@Override
	public void bootstrap(UReportEngine engine) {
		FontRegister[] registers = new FontRegister[] {
			new ArialFontRegister(),
			new ComicSansMSFontRegister(),
			new CourierNewFontRegister(),
			new FangSongFontRegister(),
			new HeiTiFontRegister(),
			new KaiTiFontRegister(),
			new SongTiFontRegister(),
			new TimesNewRomanFontRegister(),
			new YaheiFontRegister(),
		};
		for (FontRegister r : registers) {
			FontBootstrapContext.register(r);
		}
	}
}
```

`FontBootstrapContext` is a small holder so `FontBuilder` (in core) can read the registered `FontRegister` instances without core depending on the font module. Create it in core as part of this task:

- [ ] **Step 2: Create `FontBootstrapContext.java` in core**

Create `ureport2-core/src/main/java/com/bstek/ureport/export/pdf/font/FontBootstrapContext.java`:

```java
package com.bstek.ureport.export.pdf.font;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FontBootstrapContext {
	private static final List<FontRegister> registers = new ArrayList<FontRegister>();
	public static void register(FontRegister r) { registers.add(r); }
	public static List<FontRegister> all() { return Collections.unmodifiableList(registers); }
}
```

- [ ] **Step 3: Create the ServiceLoader registration file**

Create `ureport2-font/src/main/resources/META-INF/services/com.bstek.ureport.bootstrap.UReportBootstrap` with single line:

```
com.bstek.ureport.font.bootstrap.FontBootstrap
```

- [ ] **Step 4: Verify it compiles**

Run: `mvn -q -pl ureport2-font -am compile`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add ureport2-font/src/main/java/com/bstek/ureport/font/bootstrap/FontBootstrap.java ureport2-font/src/main/resources/META-INF/services/com.bstek.ureport.bootstrap.UReportBootstrap ureport2-core/src/main/java/com/bstek/ureport/export/pdf/font/FontBootstrapContext.java
git commit -m "feat(font): add FontBootstrap wiring 9 FontRegister beans"
```

---

## Task 6: Create ConsoleBootstrap

**Files:**
- Create: `ureport2-console/src/main/java/com/bstek/ureport/console/bootstrap/ConsoleBootstrap.java`
- Create: `ureport2-console/src/main/resources/META-INF/services/com.bstek.ureport.bootstrap.UReportBootstrap`

- [ ] **Step 1: Create `ConsoleBootstrap.java`**

```java
package com.bstek.ureport.console.bootstrap;

import com.bstek.ureport.UReportEngine;
import com.bstek.ureport.bootstrap.UReportBootstrap;
import com.bstek.ureport.console.cache.HttpSessionReportCache;
import com.bstek.ureport.console.chart.ChartServletAction;
import com.bstek.ureport.console.designer.DatasourceServletAction;
import com.bstek.ureport.console.designer.DesignerServletAction;
import com.bstek.ureport.console.designer.SearchFormDesignerAction;
import com.bstek.ureport.console.excel.ExportExcel97ServletAction;
import com.bstek.ureport.console.excel.ExportExcelServletAction;
import com.bstek.ureport.console.html.HtmlPreviewServletAction;
import com.bstek.ureport.console.image.ImageServletAction;
import com.bstek.ureport.console.importexcel.ImportExcelServletAction;
import com.bstek.ureport.console.pdf.ExportPdfServletAction;
import com.bstek.ureport.console.res.ResourceLoaderServletAction;
import com.bstek.ureport.console.word.ExportWordServletAction;
import com.bstek.ureport.console.ServletActionRegistry;

public class ConsoleBootstrap implements UReportBootstrap {
	@Override
	public int order() { return 20; }
	@Override
	public void bootstrap(UReportEngine engine) {
		ServletActionRegistry registry = ServletActionRegistry.getInstance();

		DatasourceServletAction datasourceServletAction = new DatasourceServletAction();
		ResourceLoaderServletAction resourceLoaderServletAction = new ResourceLoaderServletAction();
		DesignerServletAction designerServletAction = new DesignerServletAction();
		designerServletAction.setReportRender(engine.getReportRender());
		designerServletAction.setReportParser(engine.getReportParser());
		SearchFormDesignerAction searchFormDesignerAction = new SearchFormDesignerAction();

		HtmlPreviewServletAction htmlPreviewServletAction = new HtmlPreviewServletAction();
		htmlPreviewServletAction.setExportManager(engine.getExportManager());
		htmlPreviewServletAction.setReportBuilder(engine.getReportBuilder());
		htmlPreviewServletAction.setReportRender(engine.getReportRender());

		ExportWordServletAction exportWordServletAction = new ExportWordServletAction();
		exportWordServletAction.setExportManager(engine.getExportManager());
		exportWordServletAction.setReportBuilder(engine.getReportBuilder());

		ExportPdfServletAction exportPdfServletAction = new ExportPdfServletAction();
		exportPdfServletAction.setExportManager(engine.getExportManager());
		exportPdfServletAction.setReportBuilder(engine.getReportBuilder());
		exportPdfServletAction.setReportRender(engine.getReportRender());

		ExportExcelServletAction exportExcelServletAction = new ExportExcelServletAction();
		exportExcelServletAction.setExportManager(engine.getExportManager());
		exportExcelServletAction.setReportBuilder(engine.getReportBuilder());

		ExportExcel97ServletAction exportExcel97ServletAction = new ExportExcel97ServletAction();
		exportExcel97ServletAction.setExportManager(engine.getExportManager());
		exportExcel97ServletAction.setReportBuilder(engine.getReportBuilder());

		ImageServletAction imageServletAction = new ImageServletAction();
		ImportExcelServletAction importExcelServletAction = new ImportExcelServletAction();
		ChartServletAction chartServletAction = new ChartServletAction();

		HttpSessionReportCache httpSessionReportCache = new HttpSessionReportCache();
		httpSessionReportCache.setDisabled(Boolean.parseBoolean(engine.getConfig().getProperty("ureport.disableHttpSessionReportCache", "false")));
		engine.getReportCacheRegistry().register(httpSessionReportCache);

		registry.register(datasourceServletAction);
		registry.register(resourceLoaderServletAction);
		registry.register(designerServletAction);
		registry.register(searchFormDesignerAction);
		registry.register(htmlPreviewServletAction);
		registry.register(exportWordServletAction);
		registry.register(exportPdfServletAction);
		registry.register(exportExcelServletAction);
		registry.register(exportExcel97ServletAction);
		registry.register(imageServletAction);
		registry.register(importExcelServletAction);
		registry.register(chartServletAction);

		registry.initAll();
	}
}
```

- [ ] **Step 2: Create `ServletActionRegistry.java` in console**

Create `ureport2-console/src/main/java/com/bstek/ureport/console/ServletActionRegistry.java`:

```java
package com.bstek.ureport.console;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ServletActionRegistry {
	private static final ServletActionRegistry INSTANCE = new ServletActionRegistry();
	private final Map<String, ServletAction> byUrl = new LinkedHashMap<String, ServletAction>();
	private final List<ServletAction> items = new ArrayList<ServletAction>();

	public static ServletActionRegistry getInstance() { return INSTANCE; }

	public void register(ServletAction action) {
		items.add(action);
		byUrl.put(action.url(), action);
	}
	public List<ServletAction> all() { return Collections.unmodifiableList(items); }
	public ServletAction getByUrl(String url) { return byUrl.get(url); }
	public void initAll() {
		for (ServletAction a : items) {
			if (a instanceof RenderPageServletAction) {
				((RenderPageServletAction) a).init();
			}
		}
	}
}
```

- [ ] **Step 3: Create the ServiceLoader registration file**

Create `ureport2-console/src/main/resources/META-INF/services/com.bstek.ureport.bootstrap.UReportBootstrap` with single line:

```
com.bstek.ureport.console.bootstrap.ConsoleBootstrap
```

- [ ] **Step 4: Verify it compiles**

Run: `mvn -q -pl ureport2-console -am compile`
Expected: PASS (`RenderPageServletAction.init()` does not exist yet — it is added in Task 16. If compile fails on `initAll()` calling `init()`, temporarily change `initAll()` to a no-op body `{}` and re-add the init call in Task 16.)

- [ ] **Step 5: Commit**

```bash
git add ureport2-console/src/main/java/com/bstek/ureport/console/bootstrap/ConsoleBootstrap.java ureport2-console/src/main/java/com/bstek/ureport/console/ServletActionRegistry.java ureport2-console/src/main/resources/META-INF/services/com.bstek.ureport.bootstrap.UReportBootstrap
git commit -m "feat(console): add ConsoleBootstrap wiring 13 ServletAction beans"
```

---

## Task 7: Convert Utils and CacheUtils to use registries

**Files:**
- Modify: `ureport2-core/src/main/java/com/bstek/ureport/Utils.java`
- Modify: `ureport2-core/src/main/java/com/bstek/ureport/cache/CacheUtils.java`

- [ ] **Step 1: Rewrite `Utils.java`**

Replace the Spring imports and `ApplicationContextAware` machinery. New full content:

```java
package com.bstek.ureport;

import java.math.BigDecimal;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bstek.ureport.utils.UPropertyUtils;

import com.bstek.ureport.build.Context;
import com.bstek.ureport.definition.datasource.BuildinDatasource;
import com.bstek.ureport.exception.ConvertException;
import com.bstek.ureport.exception.ReportComputeException;
import com.bstek.ureport.model.Cell;
import com.bstek.ureport.model.Report;
import com.bstek.ureport.provider.image.ImageProvider;

public class Utils {
	private static boolean debug;

	public Utils() {
		UReportEngine engine = UReportEngine.getInstance();
		debug = Boolean.parseBoolean(engine.getConfig().getProperty("ureport.debug", "true"));
	}

	public static boolean isDebug() { return debug; }

	public static void logToConsole(String msg) {
		if (debug) {
			System.out.println(msg);
		}
	}

	public static List<BuildinDatasource> getBuildinDatasources() {
		return UReportEngine.getInstance().getBuildinDatasourceRegistry().all();
	}

	public static List<ImageProvider> getImageProviders() {
		return UReportEngine.getInstance().getImageProviderRegistry().all();
	}

	public static Connection getBuildinConnection(String name) {
		for (BuildinDatasource datasource : getBuildinDatasources()) {
			if (name.equals(datasource.name())) {
				return datasource.getConnection();
			}
		}
		return null;
	}

	public static List<Cell> fetchTargetCells(Cell cell, Context context, String cellName) {
		while (!context.isCellPocessed(cellName)) {
			context.getReportBuilder().buildCell(context, null);
		}
		List<Cell> leftCells = fetchCellsByLeftParent(context, cell, cellName);
		List<Cell> topCells = fetchCellsByTopParent(context, cell, cellName);
		if (leftCells != null && topCells != null) {
			int leftSize = leftCells.size(), topSize = topCells.size();
			if (leftSize == 1 || topSize == 0) { return leftCells; }
			if (topSize == 1 || leftSize == 0) { return topCells; }
			if (leftSize == 0 && topSize == 0) { return new ArrayList<Cell>(); }
			List<Cell> list = new ArrayList<Cell>();
			if (leftSize <= topSize) {
				for (Cell c : leftCells) { if (topCells.contains(c)) { list.add(c); } }
			} else {
				for (Cell c : topCells) { if (leftCells.contains(c)) { list.add(c); } }
			}
			return list;
		} else if (leftCells != null && topCells == null) {
			return leftCells;
		} else if (leftCells == null && topCells != null) {
			return topCells;
		} else {
			Report report = context.getReport();
			return report.getCellsMap().get(cellName);
		}
	}

	private static List<Cell> fetchCellsByLeftParent(Context context, Cell cell, String cellName) {
		Cell leftParentCell = cell.getLeftParentCell();
		if (leftParentCell == null) { return null; }
		if (leftParentCell.getName().equals(cellName)) {
			List<Cell> list = new ArrayList<Cell>();
			list.add(leftParentCell);
			return list;
		}
		Map<String, List<Cell>> childrenCellsMap = leftParentCell.getRowChildrenCellsMap();
		List<Cell> targetCells = childrenCellsMap.get(cellName);
		if (targetCells != null) { return targetCells; }
		return fetchCellsByLeftParent(context, leftParentCell, cellName);
	}

	private static List<Cell> fetchCellsByTopParent(Context context, Cell cell, String cellName) {
		Cell topParentCell = cell.getTopParentCell();
		if (topParentCell == null) { return null; }
		if (topParentCell.getName().equals(cellName)) {
			List<Cell> list = new ArrayList<Cell>();
			list.add(topParentCell);
			return list;
		}
		Map<String, List<Cell>> childrenCellsMap = topParentCell.getColumnChildrenCellsMap();
		List<Cell> targetCells = childrenCellsMap.get(cellName);
		if (targetCells != null) { return targetCells; }
		return fetchCellsByTopParent(context, topParentCell, cellName);
	}

	public static Object getProperty(Object obj, String property) {
		if (obj == null) return null;
		try {
			if (obj instanceof Map && property.indexOf(".") == -1) {
				Map<?, ?> map = (Map<?, ?>) obj;
				return map.get(property);
			}
			return UPropertyUtils.getProperty(obj, property);
		} catch (Exception ex) {
			throw new ReportComputeException(ex);
		}
	}

	public static Date toDate(Object obj) {
		if (obj instanceof Date) { return (Date) obj; }
		else if (obj instanceof String) {
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
			try { return sd.parse(obj.toString()); }
			catch (Exception ex) {
				sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try { return sd.parse(obj.toString()); }
				catch (Exception e) { throw new ReportComputeException("Can not convert " + obj + " to Date."); }
			}
		}
		throw new ReportComputeException("Can not convert " + obj + " to Date.");
	}

	public static BigDecimal toBigDecimal(Object obj) {
		if (obj == null) { return null; }
		if (obj instanceof BigDecimal) { return (BigDecimal) obj; }
		else if (obj instanceof String) {
			if (obj.toString().trim().equals("")) { return new BigDecimal(0); }
			try { return new BigDecimal(obj.toString().trim()); }
			catch (Exception ex) { throw new ConvertException("Can not convert " + obj + " to BigDecimal."); }
		} else if (obj instanceof Number) {
			Number n = (Number) obj;
			return new BigDecimal(n.doubleValue());
		}
		throw new ConvertException("Can not convert " + obj + " to BigDecimal.");
	}

	public void setDebug(boolean debug) { Utils.debug = debug; }
}
```

Note: `getBuildinDatasources()` / `getImageProviders()` now return `List` (was `Collection`). `List` is a `Collection`, so all existing call sites that iterate or treat as `Collection` still compile. If any caller assigned to a `Collection<BuildinDatasource>` variable, the `List` return is still assignable.

- [ ] **Step 2: Rewrite `CacheUtils.java`**

```java
package com.bstek.ureport.cache;

import java.util.Map;

import com.bstek.ureport.UReportEngine;
import com.bstek.ureport.chart.ChartData;
import com.bstek.ureport.definition.ReportDefinition;

public class CacheUtils {
	private static ReportCache reportCache;
	private static ReportDefinitionCache reportDefinitionCache;
	private static String CHART_DATA_key = "_chart_data_";

	public CacheUtils() {
		UReportEngine engine = UReportEngine.getInstance();
		for (ReportCache cache : engine.getReportCacheRegistry().all()) {
			if (cache.disabled()) { continue; }
			reportCache = cache;
			break;
		}
		if (reportDefinitionCache == null) {
			java.util.List<ReportDefinitionCache> defCaches = engine.getReportDefinitionCacheRegistry().all();
			if (defCaches.isEmpty()) {
				reportDefinitionCache = new DefaultMemoryReportDefinitionCache();
			} else {
				reportDefinitionCache = defCaches.get(0);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static ChartData getChartData(String chartId) {
		String key = CHART_DATA_key;
		if (reportCache != null) {
			Map<String, ChartData> chartDataMap = (Map<String, ChartData>) reportCache.getObject(key);
			if (chartDataMap != null) { return chartDataMap.get(chartId); }
		}
		return null;
	}

	public static void storeChartDataMap(Map<String, ChartData> map) {
		if (reportCache != null) { reportCache.storeObject(CHART_DATA_key, map); }
	}

	public static Object getObject(String file) {
		if (reportCache != null) { return reportCache.getObject(file); }
		return null;
	}

	public static void storeObject(String file, Object obj) {
		if (reportCache != null) { reportCache.storeObject(file, obj); }
	}

	public static ReportDefinition getReportDefinition(String file) {
		return reportDefinitionCache.getReportDefinition(file);
	}

	public static void cacheReportDefinition(String file, ReportDefinition reportDefinition) {
		reportDefinitionCache.cacheReportDefinition(file, reportDefinition);
	}
}
```

- [ ] **Step 3: Verify it compiles**

Run: `mvn -q -pl ureport2-core compile`
Expected: PASS

- [ ] **Step 4: Commit**

```bash
git add ureport2-core/src/main/java/com/bstek/ureport/Utils.java ureport2-core/src/main/java/com/bstek/ureport/cache/CacheUtils.java
git commit -m "refactor(core): convert Utils and CacheUtils to UReportEngine registries"
```

---

## Task 8: Convert ExpressionUtils and FormParserUtils

**Files:**
- Modify: `ureport2-core/src/main/java/com/bstek/ureport/expression/ExpressionUtils.java`
- Modify: `ureport2-core/src/main/java/com/bstek/ureport/parser/impl/searchform/FormParserUtils.java`

- [ ] **Step 1: Edit `ExpressionUtils.java`**

Remove these three imports (lines 26-28):
```java
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
```
Add import:
```java
import com.bstek.ureport.UReportEngine;
```
Change class declaration (line 68) from `public class ExpressionUtils implements ApplicationContextAware{` to `public class ExpressionUtils {`
Add a constructor that populates `functions` from the registry, replacing the old `setApplicationContext`:
```java
	public ExpressionUtils() {
		for (Function fun : UReportEngine.getInstance().getFunctionRegistry().all()) {
			functions.put(fun.name(), fun);
		}
	}
```
Delete the old `setApplicationContext` method (lines 153-159).

- [ ] **Step 2: Edit `FormParserUtils.java`**

Remove imports (lines 23-25):
```java
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
```
Add imports:
```java
import com.bstek.ureport.UReportEngine;
import java.util.List;
```
Change class declaration (line 33) from `public class FormParserUtils implements ApplicationContextAware{` to `public class FormParserUtils {`
Change the `parsers` field (line 35) from `private static Collection<FormParser> parsers=null;` to `private static List<FormParser> parsers=null;`
Replace `setApplicationContext` (lines 58-61) with a constructor:
```java
	public FormParserUtils() {
		FormParserUtils.parsers = UReportEngine.getInstance().getFormParserRegistry().all();
	}
```

- [ ] **Step 3: Verify it compiles**

Run: `mvn -q -pl ureport2-core compile`
Expected: PASS

- [ ] **Step 4: Commit**

```bash
git add ureport2-core/src/main/java/com/bstek/ureport/expression/ExpressionUtils.java ureport2-core/src/main/java/com/bstek/ureport/parser/impl/searchform/FormParserUtils.java
git commit -m "refactor(core): convert ExpressionUtils and FormParserUtils to registries"
```

---

## Task 9: Convert FontBuilder (lazy font loading + UReportResourceUtils)

**Files:**
- Modify: `ureport2-core/src/main/java/com/bstek/ureport/export/pdf/font/FontBuilder.java`

- [ ] **Step 1: Rewrite `FontBuilder.java`**

Drop Spring, load fonts lazily from `FontBootstrapContext.all()` on first use, and use `UReportResourceUtils` for classpath streams.

```java
package com.bstek.ureport.export.pdf.font;

import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bstek.ureport.utils.UIOUtils;
import com.bstek.ureport.utils.UResourceUtils;
import com.bstek.ureport.utils.UStringUtils;
import com.bstek.ureport.utils.UReportResourceUtils;
import com.bstek.ureport.exception.ReportComputeException;
import com.bstek.ureport.exception.ReportException;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.BaseFont;

public class FontBuilder {
	private static final Map<String, BaseFont> fontMap = new HashMap<String, BaseFont>();
	public static final Map<String, String> fontPathMap = new HashMap<String, String>();
	private static List<String> systemFontNameList = new ArrayList<String>();
	private static boolean fontsLoaded = false;

	public FontBuilder() {
		loadFontsIfNeeded();
	}

	public static Font getFont(String fontName, int fontSize, boolean fontBold, boolean fontItalic, boolean underLine) {
		loadFontsIfNeeded();
		BaseFont baseFont = fontMap.get(fontName);
		Font font = null;
		if (baseFont != null) { font = new Font(baseFont); }
		else { font = FontFactory.getFont(fontName); }
		font.setSize(fontSize);
		int fontStyle = Font.NORMAL;
		if (fontBold && fontItalic && underLine) { fontStyle = Font.BOLD | Font.ITALIC | Font.UNDERLINE; }
		else if (fontBold) {
			if (fontItalic) { fontStyle = Font.BOLD | Font.ITALIC; }
			else if (underLine) { fontStyle = Font.BOLD | Font.UNDERLINE; }
			else { fontStyle = Font.BOLD; }
		} else if (fontItalic) {
			if (underLine) { fontStyle = Font.ITALIC | Font.UNDERLINE; }
			else if (fontBold) { fontStyle = Font.ITALIC | Font.BOLD; }
			else { fontStyle = Font.ITALIC; }
		} else if (underLine) { fontStyle = Font.UNDERLINE; }
		font.setStyle(fontStyle);
		return font;
	}

	public static java.awt.Font getAwtFont(String fontName, int fontStyle, float size) {
		loadFontsIfNeeded();
		if (systemFontNameList.contains(fontName)) {
			return new java.awt.Font(fontName, fontStyle, new Float(size).intValue());
		}
		String fontPath = fontPathMap.get(fontName);
		if (fontPath == null) {
			fontName = "宋体";
			fontPath = fontPathMap.get(fontName);
			if (fontPath == null) { return null; }
		}
		InputStream inputStream = null;
		try {
			inputStream = UReportResourceUtils.getResourceAsStream(fontPath, null);
			java.awt.Font font = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, inputStream);
			return font.deriveFont(fontStyle, size);
		} catch (Exception e) {
			throw new ReportException(e);
		} finally {
			UIOUtils.closeQuietly(inputStream);
		}
	}

	private static synchronized void loadFontsIfNeeded() {
		if (fontsLoaded) { return; }
		GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] fontNames = environment.getAvailableFontFamilyNames();
		for (String name : fontNames) { systemFontNameList.add(name); }
		for (FontRegister fontReg : FontBootstrapContext.all()) {
			String fontName = fontReg.getFontName();
			String fontPath = fontReg.getFontPath();
			if (UStringUtils.isEmpty(fontPath) || UStringUtils.isEmpty(fontName)) { continue; }
			try {
				BaseFont baseFont = getIdentityFont(fontName, fontPath);
				if (baseFont == null) { throw new ReportComputeException("Font " + fontPath + " does not exist"); }
				fontMap.put(fontName, baseFont);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ReportComputeException(e);
			}
		}
		fontsLoaded = true;
	}

	private static BaseFont getIdentityFont(String fontFamily, String fontPath) throws DocumentException, IOException {
		if (!fontPath.startsWith(UReportResourceUtils.CLASSPATH_URL_PREFIX)) {
			fontPath = UReportResourceUtils.CLASSPATH_URL_PREFIX + fontPath;
		}
		String fontName = fontPath;
		int lastSlashPos = fontPath.lastIndexOf("/");
		if (lastSlashPos != -1) { fontName = fontPath.substring(lastSlashPos + 1, fontPath.length()); }
		if (fontName.toLowerCase().endsWith(".ttc")) { fontName = fontName + ",0"; }
		InputStream inputStream = null;
		try {
			fontPathMap.put(fontFamily, fontPath);
			inputStream = UReportResourceUtils.getResourceAsStream(fontPath, null);
			byte[] bytes = UIOUtils.toByteArray(inputStream);
			BaseFont baseFont = BaseFont.createFont(fontName, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, bytes, null);
			baseFont.setSubset(true);
			return baseFont;
		} finally {
			if (inputStream != null) inputStream.close();
		}
	}
}
```

Note: the `UResourceUtils` import line above is a typo — remove `import com.bstek.ureport.utils.UResourceUtils;` (it does not exist). Keep only `UReportResourceUtils`. Verify before compiling.

- [ ] **Step 2: Verify it compiles**

Run: `mvn -q -pl ureport2-core compile`
Expected: PASS

- [ ] **Step 3: Commit**

```bash
git add ureport2-core/src/main/java/com/bstek/ureport/export/pdf/font/FontBuilder.java
git commit -m "refactor(core): convert FontBuilder to lazy loading via FontBootstrapContext"
```

---

## Task 10: Convert ReportBuilder and ReportRender

**Files:**
- Modify: `ureport2-core/src/main/java/com/bstek/ureport/build/ReportBuilder.java`
- Modify: `ureport2-core/src/main/java/com/bstek/ureport/export/ReportRender.java`

- [ ] **Step 1: Edit `ReportBuilder.java`**

Remove imports (lines 26-28):
```java
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
```
Remove the `ApplicationContext` field (line 63): `private ApplicationContext applicationContext;`
Change class declaration (line 61) from `public class ReportBuilder extends BasePagination implements ApplicationContextAware{` to `public class ReportBuilder extends BasePagination {`
In `buildReport` (lines 75-76) change:
```java
		Map<String,Dataset> datasetMap=buildDatasets(reportDefinition, parameters, applicationContext);
		Context context = new Context(this,report,datasetMap,applicationContext,parameters,hideRowColumnBuilder);
```
to:
```java
		Map<String,Dataset> datasetMap=buildDatasets(reportDefinition, parameters);
		Context context = new Context(this,report,datasetMap,parameters,hideRowColumnBuilder);
```
Change `buildDatasets` signature (line 120) to remove the `applicationContext` parameter:
```java
	private Map<String,Dataset> buildDatasets(ReportDefinition reportDefinition,Map<String,Object> parameters){
```
Inside `buildDatasets`, change the `SpringBeanDatasourceDefinition` branch (lines 148-150):
```java
			}else if(dsDef instanceof SpringBeanDatasourceDefinition){
				SpringBeanDatasourceDefinition ds=(SpringBeanDatasourceDefinition)dsDef;
				List<Dataset> ls=ds.getDatasets(applicationContext, parameters);
```
to:
```java
			}else if(dsDef instanceof SpringBeanDatasourceDefinition){
				SpringBeanDatasourceDefinition ds=(SpringBeanDatasourceDefinition)dsDef;
				List<Dataset> ls=ds.getDatasets(parameters);
```
Replace `setApplicationContext` (lines 459-467) with a constructor-based init. Add a no-arg constructor that keeps the existing cell-builder setup and loads `DatasourceProvider` plugins from the registry + prints the splash:
```java
	public ReportBuilder() {
		cellBuildersMap.put(Expand.Right,new RightExpandBuilder());
		cellBuildersMap.put(Expand.Down,new DownExpandBuilder());
		cellBuildersMap.put(Expand.None,noneExpandBuilder);
	}
	public void init() {
		for (DatasourceProvider dp : UReportEngine.getInstance().getDatasourceProviderRegistry().all()) {
			datasourceProviderMap.put(dp.getName(), dp);
		}
		new Splash().doPrint();
	}
```
Add import: `import com.bstek.ureport.UReportEngine;`
The existing `public ReportBuilder()` constructor (lines 68-72) already sets up `cellBuildersMap` — replace it with the version above (which adds the separate `init()` method). `CoreBootstrap` must call `reportBuilder.init()` after construction: update `CoreBootstrap.java` Step 1 — after `engine.setReportBuilder(reportBuilder);` add `reportBuilder.init();`.

- [ ] **Step 2: Edit `ReportRender.java`**

Remove imports (lines 24-26):
```java
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
```
Add import: `import com.bstek.ureport.UReportEngine;`
Change class declaration (line 45) from `public class ReportRender implements ApplicationContextAware{` to `public class ReportRender {`
Change the `reportProviders` field (line 48) from `private Collection<ReportProvider> reportProviders;` to:
```java
	private List<ReportProvider> reportProviders;
```
Add an `init()` method and remove `setApplicationContext` (lines 140-143):
```java
	public void init() {
		reportProviders = UReportEngine.getInstance().getReportProviderRegistry().all();
	}
```
Add import: `import java.util.List;` (already present at line 21 — confirm). Update `CoreBootstrap` Step 1: after `engine.setReportRender(reportRender);` add `reportRender.init();`. Note ordering: `reportRender.init()` reads the report provider registry, which already contains `FileReportProvider` and `ClasspathReportProvider` registered earlier in `CoreBootstrap.bootstrap`. Confirm the register calls for those providers happen before `reportRender.init()` in `CoreBootstrap`.

- [ ] **Step 3: Update CoreBootstrap to call init() methods**

In `CoreBootstrap.java`, after constructing `reportBuilder`, `reportRender`, `exportManager` and setting them on the engine, add the init calls in the right order. Insert before `new FontBuilder();`:
```java
		reportBuilder.init();
		reportRender.init();
```
Ensure the provider-registration lines (fileReportProvider/classpathReportProvider) run before `reportRender.init()`.

- [ ] **Step 4: Verify it compiles**

Run: `mvn -q -pl ureport2-core compile`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add ureport2-core/src/main/java/com/bstek/ureport/build/ReportBuilder.java ureport2-core/src/main/java/com/bstek/ureport/export/ReportRender.java ureport2-core/src/main/java/com/bstek/ureport/bootstrap/CoreBootstrap.java
git commit -m "refactor(core): convert ReportBuilder and ReportRender to registries"
```

---

## Task 11: Remove applicationContext from Context

**Files:**
- Modify: `ureport2-core/src/main/java/com/bstek/ureport/build/Context.java`
- Modify: `ureport2-core/src/main/java/com/bstek/ureport/definition/dataset/SqlDatasetDefinition.java`
- Modify: `ureport2-console/src/main/java/com/bstek/ureport/console/designer/DatasourceServletAction.java`

- [ ] **Step 1: Edit `Context.java`**

Remove import (line 25): `import org.springframework.context.ApplicationContext;`
Remove field (line 54): `private ApplicationContext applicationContext;`
Change the 6-arg constructor (line 63) signature and body — remove the `applicationContext` parameter:
```java
	public Context(ReportBuilder reportBuilder,Report report,Map<String,Dataset> datasetMap,Map<String,Object> parameters,HideRowColumnBuilder hideRowColumnBuilder) {
		this.reportBuilder=reportBuilder;
		this.report = report;
		report.setContext(this);
		this.datasetMap=datasetMap;
		this.parameters=parameters;
		this.hideRowColumnBuilder=hideRowColumnBuilder;
		Map<String,List<Cell>> cellsMap=report.getCellsMap();
		for(String key:cellsMap.keySet()){
			if(key.equals(report.getRootCell().getName())){
				continue;
			}
			List<Cell> list=new ArrayList<Cell>();
			list.addAll(cellsMap.get(key));
			unprocessedCellsMap.put(key, list);
		}
		this.rootCell=new Cell();
		this.rootCell.setName("ROOT");
	}
```
Change the 2-arg constructor (lines 84-87) to drop `applicationContext`:
```java
	public Context(Map<String,Object> parameters){
		this.parameters=parameters;
	}
```
Remove the `getApplicationContext()` method (lines 130-132).

- [ ] **Step 2: Update `SqlDatasetDefinition.java` call site (line 52)**

Change `Context context=new Context(null,parameterMap);` to `Context context=new Context(parameterMap);`
(This file's Spring JDBC migration happens in Task 14; only fix the constructor call here.)

- [ ] **Step 3: Update `DatasourceServletAction.java` call site (line 274)**

Change `Context context=new Context(applicationContext, parameters);` to `Context context=new Context(parameters);`
(The `applicationContext` field is removed in Task 15 when `RenderPageServletAction` is converted; this call-site fix is applied here to keep compile green. The field reference `applicationContext` on line 96 still exists until Task 15.)

- [ ] **Step 4: Verify it compiles**

Run: `mvn -q -pl ureport2-console -am compile`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add ureport2-core/src/main/java/com/bstek/ureport/build/Context.java ureport2-core/src/main/java/com/bstek/ureport/definition/dataset/SqlDatasetDefinition.java ureport2-console/src/main/java/com/bstek/ureport/console/designer/DatasourceServletAction.java
git commit -m "refactor(core): remove ApplicationContext from Context"
```

---

## Task 12: Convert SpringBeanDatasourceDefinition to use BeanDatasourceRegistry

**Files:**
- Modify: `ureport2-core/src/main/java/com/bstek/ureport/definition/datasource/SpringBeanDatasourceDefinition.java`

- [ ] **Step 1: Rewrite `SpringBeanDatasourceDefinition.java`**

```java
package com.bstek.ureport.definition.datasource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bstek.ureport.UReportEngine;
import com.bstek.ureport.build.Dataset;
import com.bstek.ureport.definition.dataset.BeanDatasetDefinition;
import com.bstek.ureport.definition.dataset.DatasetDefinition;

public class SpringBeanDatasourceDefinition implements DatasourceDefinition {
	private String beanId;
	private String name;
	private List<DatasetDefinition> datasets;

	public List<Dataset> getDatasets(Map<String, Object> parameters) {
		Object targetBean = resolveBean();
		List<Dataset> list = new ArrayList<Dataset>();
		for (DatasetDefinition dsDef : datasets) {
			BeanDatasetDefinition beanDef = (BeanDatasetDefinition) dsDef;
			Dataset ds = beanDef.buildDataset(name, targetBean, parameters);
			list.add(ds);
		}
		return list;
	}

	private Object resolveBean() {
		for (BeanDatasourceProvider provider : UReportEngine.getInstance().getBeanDatasourceRegistry().all()) {
			if (provider.supports(beanId)) {
				return provider.getBean(beanId);
			}
		}
		throw new IllegalStateException("No BeanDatasourceProvider supports bean id [" + beanId + "]. Register one (e.g. ureport2-spring-boot-starter for Spring).");
	}

	@Override
	public DatasourceType getType() { return DatasourceType.spring; }

	@Override
	public List<DatasetDefinition> getDatasets() { return datasets; }
	public void setDatasets(List<DatasetDefinition> datasets) { this.datasets = datasets; }
	@Override
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public void setBeanId(String beanId) { this.beanId = beanId; }
	public String getBeanId() { return beanId; }
}
```

- [ ] **Step 2: Verify it compiles**

Run: `mvn -q -pl ureport2-core compile`
Expected: PASS

- [ ] **Step 3: Commit**

```bash
git add ureport2-core/src/main/java/com/bstek/ureport/definition/datasource/SpringBeanDatasourceDefinition.java
git commit -m "refactor(core): SpringBeanDatasourceDefinition uses BeanDatasourceProvider SPI"
```

---

## Task 13: Convert resource providers (Classpath/File/DefaultImage/StaticImage)

**Files:**
- Modify: `ureport2-core/src/main/java/com/bstek/ureport/provider/report/classpath/ClasspathReportProvider.java`
- Modify: `ureport2-core/src/main/java/com/bstek/ureport/provider/report/file/FileReportProvider.java`
- Modify: `ureport2-core/src/main/java/com/bstek/ureport/provider/image/DefaultImageProvider.java`
- Modify: `ureport2-core/src/main/java/com/bstek/ureport/image/StaticImageProcessor.java`

- [ ] **Step 1: Rewrite `ClasspathReportProvider.java`**

```java
package com.bstek.ureport.provider.report.classpath;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.bstek.ureport.UReportEngine;
import com.bstek.ureport.exception.ReportException;
import com.bstek.ureport.provider.report.ReportFile;
import com.bstek.ureport.provider.report.ReportProvider;
import com.bstek.ureport.utils.UReportResourceUtils;

public class ClasspathReportProvider implements ReportProvider {
	@Override
	public InputStream loadReport(String file) {
		InputStream in = UReportResourceUtils.getResourceAsStream(file, UReportEngine.getInstance().getServletContext());
		if (in != null) {
			return in;
		}
		String newFileName = null;
		if (file.startsWith("classpath:")) {
			newFileName = "classpath*:" + file.substring(10, file.length());
		} else if (file.startsWith("classpath*:")) {
			newFileName = "classpath:" + file.substring(11, file.length());
		}
		if (newFileName != null) {
			InputStream alt = UReportResourceUtils.getResourceAsStream(newFileName, UReportEngine.getInstance().getServletContext());
			if (alt != null) { return alt; }
		}
		throw new ReportException(new IOException("Report resource not found: " + file));
	}

	@Override
	public String getPrefix() { return "classpath"; }
	@Override
	public void deleteReport(String file) {}
	@Override
	public void saveReport(String file, String content) {}
	@Override
	public List<ReportFile> getReportFiles() { return null; }
	@Override
	public boolean disabled() { return false; }
	@Override
	public String getName() { return null; }
}
```

- [ ] **Step 2: Rewrite `FileReportProvider.java`**

Add an `init(UReportEngine)` method that replaces `setApplicationContext`. Drop Spring imports and `ApplicationContextAware`.

```java
package com.bstek.ureport.provider.report.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.bstek.ureport.UReportEngine;
import com.bstek.ureport.utils.UIOUtils;
import com.bstek.ureport.exception.ReportException;
import com.bstek.ureport.provider.report.ReportFile;
import com.bstek.ureport.provider.report.ReportProvider;

public class FileReportProvider implements ReportProvider {
	private String prefix = "file:";
	private String fileStoreDir;
	private boolean disabled;

	@Override
	public InputStream loadReport(String file) {
		if (file.startsWith(prefix)) { file = file.substring(prefix.length(), file.length()); }
		String fullPath = fileStoreDir + "/" + file;
		try { return new FileInputStream(fullPath); }
		catch (FileNotFoundException e) { throw new ReportException(e); }
	}

	@Override
	public void deleteReport(String file) {
		if (file.startsWith(prefix)) { file = file.substring(prefix.length(), file.length()); }
		String fullPath = fileStoreDir + "/" + file;
		File f = new File(fullPath);
		if (f.exists()) { f.delete(); }
	}

	@Override
	public List<ReportFile> getReportFiles() {
		File file = new File(fileStoreDir);
		List<ReportFile> list = new ArrayList<ReportFile>();
		for (File f : file.listFiles()) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(f.lastModified());
			list.add(new ReportFile(f.getName(), calendar.getTime()));
		}
		Collections.sort(list, new Comparator<ReportFile>() {
			@Override
			public int compare(ReportFile f1, ReportFile f2) {
				return f2.getUpdateDate().compareTo(f1.getUpdateDate());
			}
		});
		return list;
	}

	@Override
	public String getName() { return "服务器文件系统"; }

	@Override
	public void saveReport(String file, String content) {
		if (file.startsWith(prefix)) { file = file.substring(prefix.length(), file.length()); }
		String fullPath = fileStoreDir + "/" + file;
		FileOutputStream outStream = null;
		try {
			outStream = new FileOutputStream(new File(fullPath));
			UIOUtils.write(content, outStream, "utf-8");
		} catch (Exception ex) {
			throw new ReportException(ex);
		} finally {
			if (outStream != null) {
				try { outStream.close(); } catch (IOException e) { e.printStackTrace(); }
			}
		}
	}

	@Override
	public boolean disabled() { return disabled; }

	public void setDisabled(boolean disabled) { this.disabled = disabled; }
	public void setFileStoreDir(String fileStoreDir) { this.fileStoreDir = fileStoreDir; }

	public void init(UReportEngine engine) {
		File file = new File(fileStoreDir);
		if (file.exists()) { return; }
		if (engine.getServletContext() != null) {
			String basePath = engine.getServletContext().getRealPath("/");
			fileStoreDir = basePath + fileStoreDir;
			file = new File(fileStoreDir);
			if (!file.exists()) { file.mkdirs(); }
		}
	}

	@Override
	public String getPrefix() { return prefix; }
}
```

- [ ] **Step 3: Rewrite `DefaultImageProvider.java`**

```java
package com.bstek.ureport.provider.image;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.bstek.ureport.UReportEngine;
import com.bstek.ureport.exception.ReportComputeException;
import com.bstek.ureport.utils.UReportResourceUtils;

public class DefaultImageProvider implements ImageProvider {
	private String baseWebPath;

	@Override
	public InputStream getImage(String path) {
		try {
			if (path.startsWith(UReportResourceUtils.CLASSPATH_URL_PREFIX) || path.startsWith("/WEB-INF")) {
				InputStream in = UReportResourceUtils.getResourceAsStream(path, UReportEngine.getInstance().getServletContext());
				if (in == null) { throw new ReportComputeException("Image resource not found: " + path); }
				return in;
			} else {
				path = baseWebPath + path;
				return new FileInputStream(path);
			}
		} catch (IOException e) {
			throw new ReportComputeException(e);
		}
	}

	@Override
	public boolean support(String path) {
		if (path.startsWith(UReportResourceUtils.CLASSPATH_URL_PREFIX)) { return true; }
		if (baseWebPath != null && (path.startsWith("/") || path.startsWith("/WEB-INF"))) { return true; }
		return false;
	}

	public void init(UReportEngine engine) {
		if (engine.getServletContext() != null) {
			baseWebPath = engine.getServletContext().getRealPath("/");
		}
	}
}
```

- [ ] **Step 4: Rewrite `StaticImageProcessor.java`**

Replace lines 23 (Spring import) and lines 52-56 (the fallback using `applicationContext.getResource`):

Remove import (line 23): `import org.springframework.context.ApplicationContext;`
Replace lines 48-60:
```java
		try{
			InputStream inputStream=targetImageProvider.getImage(path);
			return inputStream;
		}catch(Exception ex){
			log.warning("Image ["+path+"] not exist,use default picture.");
			String imageNotExistPath="classpath:com/bstek/ureport/image/image-not-exist.jpg";
			InputStream in = com.bstek.ureport.utils.UReportResourceUtils.getResourceAsStream(imageNotExistPath, com.bstek.ureport.UReportEngine.getInstance().getServletContext());
			if (in == null) {
				throw new ReportComputeException("Default image not found in classpath: com/bstek/ureport/image/image-not-exist.jpg");
			}
			return in;
		}
```

- [ ] **Step 5: Re-add the init() calls in CoreBootstrap**

In `CoreBootstrap.java`, re-add the two lines at the end of `bootstrap(...)` that were deferred in Task 4 Step 4:
```java
		fileReportProvider.init(engine);
		((DefaultImageProvider) engine.getImageProviderRegistry().all().get(0)).init(engine);
```
Confirm `DefaultImageProvider` is the first image provider registered (it is — Step 1 of Task 4 registers it first).

- [ ] **Step 6: Verify it compiles**

Run: `mvn -q -pl ureport2-core compile`
Expected: PASS

- [ ] **Step 7: Commit**

```bash
git add ureport2-core/src/main/java/com/bstek/ureport/provider/report/classpath/ClasspathReportProvider.java ureport2-core/src/main/java/com/bstek/ureport/provider/report/file/FileReportProvider.java ureport2-core/src/main/java/com/bstek/ureport/provider/image/DefaultImageProvider.java ureport2-core/src/main/java/com/bstek/ureport/image/StaticImageProcessor.java ureport2-core/src/main/java/com/bstek/ureport/bootstrap/CoreBootstrap.java
git commit -m "refactor(core): convert resource providers to UReportResourceUtils + UReportEngine"
```

---

## Task 14: Convert SqlDatasetDefinition and ProcedureUtils (JDBC)

**Files:**
- Modify: `ureport2-core/src/main/java/com/bstek/ureport/definition/dataset/SqlDatasetDefinition.java`
- Modify: `ureport2-core/src/main/java/com/bstek/ureport/utils/ProcedureUtils.java`

- [ ] **Step 1: Edit `SqlDatasetDefinition.java`**

Remove imports (lines 25-26):
```java
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
```
Add import: `import com.bstek.ureport.utils.UReportJdbcUtils;`
Replace lines 72-74:
```java
		SingleConnectionDataSource datasource=new SingleConnectionDataSource(conn,false);
		NamedParameterJdbcTemplate jdbcTemplate=new NamedParameterJdbcTemplate(datasource);
		List<Map<String,Object>> list= jdbcTemplate.queryForList(sqlForUse, pmap);
		return new Dataset(name,list);
```
with:
```java
		List<Map<String,Object>> list = UReportJdbcUtils.queryForList(conn, sqlForUse, pmap);
		return new Dataset(name,list);
```

- [ ] **Step 2: Edit `ProcedureUtils.java`**

Remove import (line 30): `import org.springframework.jdbc.support.JdbcUtils;`
Add import: `import com.bstek.ureport.utils.UReportJdbcUtils;`
Replace the three `JdbcUtils.close*` calls in `procedureColumnsQuery` (lines 68-70):
```java
			UReportJdbcUtils.closeQuietly(rs);
			UReportJdbcUtils.closeQuietly(cs);
			UReportJdbcUtils.closeQuietly(conn);
```
Replace the three `JdbcUtils.close*` calls in `procedureQuery` (lines 102-104):
```java
			UReportJdbcUtils.closeQuietly(rs);
			UReportJdbcUtils.closeQuietly(cs);
			UReportJdbcUtils.closeQuietly(conn);
```

- [ ] **Step 3: Verify it compiles**

Run: `mvn -q -pl ureport2-core compile`
Expected: PASS

- [ ] **Step 4: Commit**

```bash
git add ureport2-core/src/main/java/com/bstek/ureport/definition/dataset/SqlDatasetDefinition.java ureport2-core/src/main/java/com/bstek/ureport/utils/ProcedureUtils.java
git commit -m "refactor(core): migrate SqlDatasetDefinition and ProcedureUtils off Spring JDBC"
```

---

## Task 15: Convert DatasourceServletAction (JDBC + BeanDatasourceProvider)

**Files:**
- Modify: `ureport2-console/src/main/java/com/bstek/ureport/console/designer/DatasourceServletAction.java`

- [ ] **Step 1: Edit imports**

Remove all Spring JDBC imports (lines 46-57):
```java
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.JdbcUtils;
```
Add imports:
```java
import com.bstek.ureport.UReportEngine;
import com.bstek.ureport.utils.UReportJdbcUtils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
```

- [ ] **Step 2: Replace `loadMethods` (lines 94-120) — use BeanDatasourceProvider**

```java
	public void loadMethods(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String beanId = req.getParameter("beanId");
		Object obj = null;
		for (com.bstek.ureport.definition.datasource.BeanDatasourceProvider p : UReportEngine.getInstance().getBeanDatasourceRegistry().all()) {
			if (p.supports(beanId)) { obj = p.getBean(beanId); break; }
		}
		if (obj == null) {
			throw new ReportDesignException("Bean [" + beanId + "] not found via any BeanDatasourceProvider.");
		}
		Class<?> clazz = obj.getClass();
		Method[] methods = clazz.getMethods();
		List<String> result = new ArrayList<String>();
		for (Method method : methods) {
			Class<?>[] types = method.getParameterTypes();
			if (types.length != 3) { continue; }
			if (!String.class.isAssignableFrom(types[0])) { continue; }
			if (!String.class.isAssignableFrom(types[1])) { continue; }
			if (!Map.class.isAssignableFrom(types[2])) { continue; }
			result.add(method.getName());
		}
		writeObjectToJson(resp, result);
	}
```

- [ ] **Step 3: Replace `buildDatabaseTables` close calls (lines 164-165)**

```java
			UReportJdbcUtils.closeQuietly(rs);
			UReportJdbcUtils.closeQuietly(conn);
```

- [ ] **Step 4: Replace `buildFields` (lines 169-210) — plain PreparedStatement**

```java
	public void buildFields(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String sql = req.getParameter("sql");
		String parameters = req.getParameter("parameters");
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		final List<Field> fields = new ArrayList<Field>();
		try {
			conn = buildConnection(req);
			Map<String, Object> map = buildParameters(parameters);
			sql = parseSql(sql, map);
			if (ProcedureUtils.isProcedure(sql)) {
				List<Field> fieldsList = ProcedureUtils.procedureColumnsQuery(sql, map, conn);
				fields.addAll(fieldsList);
			} else {
				com.bstek.ureport.utils.ParsedSql parsed = UReportJdbcUtils.parseNamedSql(sql);
				Object[] values = UReportJdbcUtils.buildValueArray(parsed, map);
				ps = conn.prepareStatement(parsed.getSql());
				for (int k = 0; k < values.length; k++) { ps.setObject(k + 1, values[k]); }
				rs = ps.executeQuery();
				ResultSetMetaData metadata = rs.getMetaData();
				int columnCount = metadata.getColumnCount();
				for (int i = 0; i < columnCount; i++) {
					String columnName = metadata.getColumnLabel(i + 1);
					fields.add(new Field(columnName));
				}
			}
			writeObjectToJson(resp, fields);
		} catch (Exception ex) {
			throw new ReportDesignException(ex);
		} finally {
			UReportJdbcUtils.closeQuietly(rs);
			UReportJdbcUtils.closeQuietly(ps);
			UReportJdbcUtils.closeQuietly(conn);
		}
	}
```

- [ ] **Step 5: Delete `getPreparedStatementCreator` (lines 212-219)**

Remove the entire method.

- [ ] **Step 6: Replace `previewData` (lines 221-270) — use UReportJdbcUtils.queryForList**

Inside `previewData`, replace lines 233-235:
```java
				DataSource dataSource=new SingleConnectionDataSource(conn,false);
				NamedParameterJdbcTemplate jdbc=new NamedParameterJdbcTemplate(dataSource);
				list=jdbc.queryForList(sql, map);
```
with:
```java
				list = UReportJdbcUtils.queryForList(conn, sql, map);
```
Replace the `finally` block's manual `conn.close()` (lines 262-268) with:
```java
		} finally {
			UReportJdbcUtils.closeQuietly(conn);
		}
```
Remove now-unused imports if the compiler warns: `javax.sql.DataSource` (line 39) can be removed.

- [ ] **Step 7: Verify it compiles**

Run: `mvn -q -pl ureport2-console -am compile`
Expected: PASS (the `applicationContext` field on `RenderPageServletAction` is still present but no longer referenced by this file after Step 2; the `Context` constructor call was already fixed in Task 11 Step 3)

- [ ] **Step 8: Commit**

```bash
git add ureport2-console/src/main/java/com/bstek/ureport/console/designer/DatasourceServletAction.java
git commit -m "refactor(console): migrate DatasourceServletAction off Spring JDBC and getBean"
```

---

## Task 16: Convert RenderPageServletAction, DesignerServletAction, ResourceLoaderServletAction

**Files:**
- Modify: `ureport2-console/src/main/java/com/bstek/ureport/console/RenderPageServletAction.java`
- Modify: `ureport2-console/src/main/java/com/bstek/ureport/console/designer/DesignerServletAction.java`
- Modify: `ureport2-console/src/main/java/com/bstek/ureport/console/res/ResourceLoaderServletAction.java`

- [ ] **Step 1: Rewrite `RenderPageServletAction.java`**

```java
package com.bstek.ureport.console;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogChute;

public abstract class RenderPageServletAction extends WriteJsonServletAction {
	protected VelocityEngine ve;

	public void init() {
		ve = new VelocityEngine();
		ve.setProperty(Velocity.RESOURCE_LOADER, "class");
		ve.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		ve.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, new NullLogChute());
		ve.init();
	}
}
```

- [ ] **Step 2: Edit `DesignerServletAction.java`**

Remove imports (lines 36-37):
```java
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
```
Add import: `import com.bstek.ureport.UReportEngine;`
Replace the `setApplicationContext` method (lines 200-210) with an `init()` override:
```java
	@Override
	public void init() {
		super.init();
		for (ReportProvider provider : UReportEngine.getInstance().getReportProviderRegistry().all()) {
			if (provider.disabled() || provider.getName() == null) { continue; }
			reportProviders.add(provider);
		}
	}
```
Change the `reportProviders` field (line 60) from `private List<ReportProvider> reportProviders=new ArrayList<ReportProvider>();` — keep as-is (it's populated by `init()` now).

- [ ] **Step 3: Rewrite `ResourceLoaderServletAction.java`**

```java
package com.bstek.ureport.console.res;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.bstek.ureport.UReportEngine;
import com.bstek.ureport.utils.UIOUtils;
import com.bstek.ureport.utils.UReportResourceUtils;
import com.bstek.ureport.console.ServletAction;
import com.bstek.ureport.console.UReportServlet;

public class ResourceLoaderServletAction implements ServletAction {
	public static final String URL = "/res";

	@Override
	public void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getContextPath() + UReportServlet.URL + URL;
		String uri = req.getRequestURI();
		String resPath = uri.substring(path.length() + 1);
		String p = "classpath:" + resPath;
		if (p.endsWith(".js")) { resp.setContentType("text/javascript"); }
		else if (p.endsWith(".css")) { resp.setContentType("text/css"); }
		else if (p.endsWith(".png")) { resp.setContentType("image/png"); }
		else if (p.endsWith(".jpg")) { resp.setContentType("image/jpeg"); }
		else if (p.endsWith(".svg")) { resp.setContentType("image/svg+xml"); }
		else { resp.setContentType("application/octet-stream"); }
		InputStream input = UReportResourceUtils.getResourceAsStream(p, UReportEngine.getInstance().getServletContext());
		if (input == null) {
			throw new ServletException("Resource not found: " + p);
		}
		OutputStream output = resp.getOutputStream();
		try { UIOUtils.copy(input, output); }
		finally {
			UIOUtils.closeQuietly(input);
			UIOUtils.closeQuietly(output);
		}
	}

	@Override
	public String url() { return URL; }
}
```

- [ ] **Step 4: Re-enable the initAll() body in ServletActionRegistry**

In `ureport2-console/src/main/java/com/bstek/ureport/console/ServletActionRegistry.java`, if `initAll()` was made a no-op in Task 6 Step 4, restore it to call `init()` on each `RenderPageServletAction`:
```java
	public void initAll() {
		for (ServletAction a : items) {
			if (a instanceof RenderPageServletAction) {
				((RenderPageServletAction) a).init();
			}
		}
	}
```

- [ ] **Step 5: Verify it compiles**

Run: `mvn -q -pl ureport2-console -am compile`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add ureport2-console/src/main/java/com/bstek/ureport/console/RenderPageServletAction.java ureport2-console/src/main/java/com/bstek/ureport/console/designer/DesignerServletAction.java ureport2-console/src/main/java/com/bstek/ureport/console/res/ResourceLoaderServletAction.java ureport2-console/src/main/java/com/bstek/ureport/console/ServletActionRegistry.java
git commit -m "refactor(console): convert servlet actions off Spring ApplicationContextAware"
```

---

## Task 17: Convert UReportServlet (bootstrap via UReportEngine)

**Files:**
- Modify: `ureport2-console/src/main/java/com/bstek/ureport/console/UReportServlet.java`

- [ ] **Step 1: Edit imports**

Remove (lines 32-33):
```java
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
```
Add:
```java
import java.util.Properties;
import com.bstek.ureport.UReportEngine;
```

- [ ] **Step 2: Rewrite `init` and remove `getWebApplicationContext` (lines 45-61)**

```java
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		Properties props = new Properties();
		String fileStoreDir = config.getInitParameter("ureport.fileStoreDir");
		String debug = config.getInitParameter("ureport.debug");
		String disableFileProvider = config.getInitParameter("ureport.disableFileProvider");
		String disableCache = config.getInitParameter("ureport.disableHttpSessionReportCache");
		if (fileStoreDir != null) { props.setProperty("ureport.fileStoreDir", fileStoreDir); }
		if (debug != null) { props.setProperty("ureport.debug", debug); }
		if (disableFileProvider != null) { props.setProperty("ureport.disableFileProvider", disableFileProvider); }
		if (disableCache != null) { props.setProperty("ureport.disableHttpSessionReportCache", disableCache); }
		UReportEngine.initialize(props, config.getServletContext());
		for (ServletAction handler : ServletActionRegistry.getInstance().all()) {
			String url = handler.url();
			if (actionMap.containsKey(url)) {
				throw new RuntimeException("Handler [" + url + "] already exist.");
			}
			actionMap.put(url, handler);
		}
	}
```
Delete the `getWebApplicationContext` method (lines 59-61).

- [ ] **Step 3: Verify it compiles**

Run: `mvn -q -pl ureport2-console -am compile`
Expected: PASS

- [ ] **Step 4: Commit**

```bash
git add ureport2-console/src/main/java/com/bstek/ureport/console/UReportServlet.java
git commit -m "refactor(console): UReportServlet bootstraps via UReportEngine"
```

---

## Task 18: Delete Spring XML + PropertyPlaceholder, remove Spring deps, bump version

**Files:**
- Delete: `ureport2-core/src/main/java/com/bstek/ureport/UReportPropertyPlaceholderConfigurer.java`
- Delete: `ureport2-core/src/main/resources/ureport-core-context.xml`
- Delete: `ureport2-console/src/main/resources/ureport-console-context.xml`
- Delete: `ureport2-font/src/main/resources/ureport-font-context.xml`
- Modify: `ureport2-core/pom.xml`
- Modify: `ureport2-parent/pom.xml`

- [ ] **Step 1: Delete the four files**

```bash
git rm ureport2-core/src/main/java/com/bstek/ureport/UReportPropertyPlaceholderConfigurer.java
git rm ureport2-core/src/main/resources/ureport-core-context.xml
git rm ureport2-console/src/main/resources/ureport-console-context.xml
git rm ureport2-font/src/main/resources/ureport-font-context.xml
```

- [ ] **Step 2: Remove Spring dependencies from `ureport2-core/pom.xml`**

Delete these four `<dependency>` blocks (lines 22-41):
```xml
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-web</artifactId>
			<version>6.1.21</version>
		</dependency>
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-jdbc</artifactId>
			<version>6.1.21</version>
		</dependency>
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-beans</artifactId>
			<version>6.1.21</version>
		</dependency>
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-context</artifactId>
			<version>6.1.21</version>
		</dependency>
```

- [ ] **Step 3: Bump revision and update description in `ureport2-parent/pom.xml`**

Change line 32: `<revision>3.0.0</revision>` to `<revision>4.0.0</revision>`.
Change line 11: `<description>基于Spring架构的高性能纯Java报表引擎</description>` to `<description>Framework-agnostic high-performance pure Java report engine</description>`.
Add the new starter module to `<modules>` (after line 45, the ureport2-font module line):
```xml
		<module>../ureport2-spring-boot-starter</module>
```

- [ ] **Step 4: Verify the whole project compiles with Spring gone**

Run: `mvn -q clean install -DskipTests`
Expected: PASS for ureport2-core, ureport2-font, ureport2-console. (ureport2-spring-boot-starter does not exist yet — the build will fail trying to build that module. Temporarily remove the `<module>../ureport2-spring-boot-starter</module>` line added in Step 3 until Task 19, OR create the starter in Task 19 first. Recommended: leave the module line out for now, re-add it at the end of Task 19.)

If you added the module line in Step 3, remove it now and re-add after Task 19. Run `mvn -q -pl ureport2-core,ureport2-font,ureport2-console -amd clean install -DskipTests` instead.

Expected: PASS — BUILD SUCCESS

- [ ] **Step 5: Grep to confirm zero Spring references remain in core/console/font Java**

Run: `grep -r "org.springframework" ureport2-core/src ureport2-console/src ureport2-font/src --include=*.java`
Expected: no matches (empty output)

- [ ] **Step 6: Commit**

```bash
git add -A
git commit -m "chore: remove Spring deps and XML configs, bump to 4.0.0"
```

---

## Task 19: Create the ureport2-spring-boot-starter module

**Files:**
- Create: `ureport2-spring-boot-starter/pom.xml`
- Create: `ureport2-spring-boot-starter/src/main/java/com/bstek/ureport/spring/boot/autoconfigure/UReportProperties.java`
- Create: `ureport2-spring-boot-starter/src/main/java/com/bstek/ureport/spring/boot/autoconfigure/UReportAutoConfiguration.java`
- Create: `ureport2-spring-boot-starter/src/main/java/com/bstek/ureport/spring/boot/bean/SpringBeanDatasourceProvider.java`
- Create: `ureport2-spring-boot-starter/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

- [ ] **Step 1: Create the starter pom.xml**

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<artifactId>ureport2-spring-boot-starter</artifactId>
	<name>ureport2-spring-boot-starter</name>
	<parent>
		<groupId>io.github.jiangood</groupId>
		<artifactId>ureport2-parent</artifactId>
		<version>${revision}</version>
		<relativePath>../ureport2-parent/pom.xml</relativePath>
	</parent>
	<dependencies>
		<dependency>
			<groupId>io.github.jiangood</groupId>
			<artifactId>ureport2-console</artifactId>
			<version>${revision}</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-autoconfigure</artifactId>
			<version>3.3.0</version>
		</dependency>
		<dependency>
			<groupId>jakarta.servlet</groupId>
			<artifactId>jakarta.servlet-api</artifactId>
			<version>6.0.0</version>
			<scope>provided</scope>
		</dependency>
	</dependencies>
</project>
```

- [ ] **Step 2: Create `UReportProperties.java`**

```java
package com.bstek.ureport.spring.boot.autoconfigure;

import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ureport")
public class UReportProperties {
	private String fileStoreDir = "/WEB-INF/ureportfiles";
	private boolean debug = true;
	private boolean disableFileProvider = false;
	private boolean disableHttpSessionReportCache = false;

	public String getFileStoreDir() { return fileStoreDir; }
	public void setFileStoreDir(String fileStoreDir) { this.fileStoreDir = fileStoreDir; }
	public boolean isDebug() { return debug; }
	public void setDebug(boolean debug) { this.debug = debug; }
	public boolean isDisableFileProvider() { return disableFileProvider; }
	public void setDisableFileProvider(boolean disableFileProvider) { this.disableFileProvider = disableFileProvider; }
	public boolean isDisableHttpSessionReportCache() { return disableHttpSessionReportCache; }
	public void setDisableHttpSessionReportCache(boolean disableHttpSessionReportCache) { this.disableHttpSessionReportCache = disableHttpSessionReportCache; }

	public Properties toProperties() {
		Properties props = new Properties();
		props.setProperty("ureport.fileStoreDir", fileStoreDir);
		props.setProperty("ureport.debug", Boolean.toString(debug));
		props.setProperty("ureport.disableFileProvider", Boolean.toString(disableFileProvider));
		props.setProperty("ureport.disableHttpSessionReportCache", Boolean.toString(disableHttpSessionReportCache));
		return props;
	}
}
```

- [ ] **Step 3: Create `SpringBeanDatasourceProvider.java`**

```java
package com.bstek.ureport.spring.boot.bean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;

import com.bstek.ureport.definition.datasource.BeanDatasourceProvider;
import com.bstek.ureport.definition.datasource.MethodInfo;

public class SpringBeanDatasourceProvider implements BeanDatasourceProvider {
	private final ApplicationContext applicationContext;

	public SpringBeanDatasourceProvider(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public String frameworkName() { return "spring"; }

	@Override
	public boolean supports(String beanId) {
		return applicationContext.containsBean(beanId);
	}

	@Override
	public Object getBean(String beanId) {
		return applicationContext.getBean(beanId);
	}

	@Override
	public List<MethodInfo> getMethods(String beanId) {
		Object obj = getBean(beanId);
		List<MethodInfo> result = new ArrayList<MethodInfo>();
		for (Method method : obj.getClass().getMethods()) {
			Class<?>[] types = method.getParameterTypes();
			if (types.length != 3) { continue; }
			if (!String.class.isAssignableFrom(types[0])) { continue; }
			if (!String.class.isAssignableFrom(types[1])) { continue; }
			if (!Map.class.isAssignableFrom(types[2])) { continue; }
			List<Class<?>> paramTypes = new ArrayList<Class<?>>();
			for (Class<?> t : types) { paramTypes.add(t); }
			result.add(new MethodInfo(method.getName(), method.getReturnType(), paramTypes));
		}
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> invoke(String beanId, String methodName, String datasetName, Map<String, Object> params) {
		Object obj = getBean(beanId);
		try {
			Method method = obj.getClass().getMethod(methodName, String.class, String.class, Map.class);
			return (List<Map<String, Object>>) method.invoke(obj, datasetName, datasetName, params);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
```

- [ ] **Step 4: Create `UReportAutoConfiguration.java`**

```java
package com.bstek.ureport.spring.boot.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.bstek.ureport.UReportEngine;
import com.bstek.ureport.console.UReportServlet;
import com.bstek.ureport.spring.boot.bean.SpringBeanDatasourceProvider;

import jakarta.servlet.ServletContext;

@AutoConfiguration
@EnableConfigurationProperties(UReportProperties.class)
@ConditionalOnWebApplication
public class UReportAutoConfiguration {

	@Bean
	public UReportEngine ureportEngine(UReportProperties props, ServletContext servletContext, ApplicationContext ctx) {
		UReportEngine engine = UReportEngine.initialize(props.toProperties(), servletContext);
		engine.getBeanDatasourceRegistry().register(new SpringBeanDatasourceProvider(ctx));
		return engine;
	}

	@Bean
	public ServletRegistrationBean<UReportServlet> ureportServlet() {
		return new ServletRegistrationBean<UReportServlet>(new UReportServlet(), "/ureport/*");
	}
}
```

- [ ] **Step 5: Create the AutoConfiguration imports file**

Create `ureport2-spring-boot-starter/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` with single line:

```
com.bstek.ureport.spring.boot.autoconfigure.UReportAutoConfiguration
```

- [ ] **Step 6: Re-add the starter to the parent modules list**

In `ureport2-parent/pom.xml`, add inside `<modules>` (after the ureport2-font module line):
```xml
		<module>../ureport2-spring-boot-starter</module>
```

- [ ] **Step 7: Build the starter**

Run: `mvn -q -pl ureport2-spring-boot-starter -am clean install -DskipTests`
Expected: PASS — BUILD SUCCESS

- [ ] **Step 8: Commit**

```bash
git add ureport2-spring-boot-starter ureport2-parent/pom.xml
git commit -m "feat: add ureport2-spring-boot-starter with Spring Boot 3 auto-config"
```

---

## Task 20: Update documentation

**Files:**
- Modify: `README.md`
- Modify: `README-zh_CN.md`
- Modify: `docs/STORAGE-DATASOURCE.md`

- [ ] **Step 1: Update `README.md`**

Change line 9 from `UReport2 是一款高性能的架构在 Spring 之上纯 Java 报表引擎...` to `UReport2 是一款高性能的框架无关（framework-agnostic）纯 Java 报表引擎...`.
Replace the "安装与配置" section (lines 25-74) with the Spring Boot integration instructions:

```markdown
## 安装与配置

### Spring Boot 3.x 项目（推荐）

在 `pom.xml` 中添加依赖：

```
<dependency>
    <groupId>io.github.jiangood</groupId>
    <artifactId>ureport2-spring-boot-starter</artifactId>
    <version>4.0.0</version>
</dependency>
```

启动项目后访问 `http://localhost:8080/ureport/designer` 即可看到报表设计器界面。无需任何 `web.xml` 或监听器配置。

可选配置项（`application.yml`）：

```yaml
ureport:
  file-store-dir: /WEB-INF/ureportfiles
  debug: true
  disable-file-provider: false
  disable-http-session-report-cache: false
```

### 非 Spring 项目（Solon / JFinal / 原生 Servlet 等）

在 `pom.xml` 中添加依赖：

```
<dependency>
    <groupId>io.github.jiangood</groupId>
    <artifactId>ureport2-console</artifactId>
    <version>4.0.0</version>
</dependency>
```

在 `web.xml` 中注册 Servlet（或使用等价的 `@WebServlet` / `ServletContainerInitializer`）：

```
<servlet>
    <servlet-name>ureportServlet</servlet-name>
    <servlet-class>com.bstek.ureport.console.UReportServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>ureportServlet</servlet-name>
    <url-pattern>/ureport/*</url-pattern>
</servlet-mapping>
```

`UReportServlet` 在初始化时自动完成 `UReportEngine` 的引导，无需 `ContextLoaderListener` 或 Spring 配置文件。

### 自定义扩展（SPI）

UReport2 通过 Java SPI 发现用户扩展。实现以下任意接口并在 `META-INF/services/` 下注册即可：

- `com.bstek.ureport.provider.report.ReportProvider` — 报表存储
- `com.bstek.ureport.definition.datasource.BuildinDatasource` — 内置数据源
- `com.bstek.ureport.definition.datasource.DatasourceProvider` — 数据源提供者
- `com.bstek.ureport.provider.image.ImageProvider` — 图片提供者
- `com.bstek.ureport.expression.function.Function` — 表达式函数
- `com.bstek.ureport.export.pdf.font.FontRegister` — 字体注册
- `com.bstek.ureport.definition.datasource.BeanDatasourceProvider` — Bean 数据源（用于 Spring Bean 数据源特性，Spring 用户使用 `ureport2-spring-boot-starter` 自动获得）

例如，自定义一个报表存储：

```
META-INF/services/com.bstek.ureport.provider.report.ReportProvider
```
内容为实现类的全限定名。
```

- [ ] **Step 2: Update `README-zh_CN.md`**

Apply the same content changes as Step 1 (the file mirrors `README.md`).

- [ ] **Step 3: Update `docs/STORAGE-DATASOURCE.md`**

Replace the Spring-specific instructions in the "Spring Bean" data source section (around lines 253-263) and the `ApplicationContextAware` code example (lines 116-129, 219-225) with `BeanDatasourceProvider` SPI instructions and a reference to `ureport2-spring-boot-starter`. Replace the `FileReportProvider` example (lines 96-129) so it implements `ReportProvider` and registers via `META-INF/services/com.bstek.ureport.provider.report.ReportProvider` instead of `ApplicationContextAware` + Spring XML.

Specifically:
- Lines 116-119: remove the `org.springframework.*` imports.
- Line 129: change `public class FileReportProvider implements ReportProvider,ApplicationContextAware{` to `public class FileReportProvider implements ReportProvider{` and add `public void init(UReportEngine engine){...}` instead of `setApplicationContext`.
- Lines 219-225: replace the `setApplicationContext` method body with the `init(UReportEngine)` method using `engine.getServletContext().getRealPath("/")`.
- Lines 253-263: replace the Spring Bean datasource description with: "The Spring Bean datasource feature is now framework-agnostic via the `BeanDatasourceProvider` SPI. Spring Boot users get it automatically by adding `ureport2-spring-boot-starter`. Non-Spring users implement `BeanDatasourceProvider` and register it via `META-INF/services/com.bstek.ureport.definition.datasource.BeanDatasourceProvider`."
- Line 15 / 25: remove references to `PropertyPlaceholderConfigurer` and Spring XML config; point to `ureport.properties` / `application.yml` instead.

- [ ] **Step 4: Commit**

```bash
git add README.md README-zh_CN.md docs/STORAGE-DATASOURCE.md
git commit -m "docs: update README and STORAGE-DATASOURCE for v4.0.0 framework-agnostic design"
```

---

## Task 21: Final verification

- [ ] **Step 1: Full clean build**

Run: `mvn clean install -DskipTests`
Expected: BUILD SUCCESS for all 5 modules (parent, core, font, console, spring-boot-starter).

- [ ] **Step 2: Run the JDBC unit tests**

Run: `mvn -q -pl ureport2-core test -Dtest=UReportJdbcUtilsTest`
Expected: `Tests run: 4, Failures: 0, Errors: 0`

- [ ] **Step 3: Confirm no Spring references in core/console/font**

Run (PowerShell):
```powershell
Get-ChildItem -Path ureport2-core,ureport2-console,ureport2-font -Recurse -Include *.java | Select-String -Pattern "org.springframework" -SimpleMatch
```
Expected: no output (zero matches).

- [ ] **Step 4: Confirm the starter auto-config file is present**

Run: `Test-Path ureport2-spring-boot-starter/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
Expected: True

- [ ] **Step 5: Confirm the three ServiceLoader bootstrap files exist**

```powershell
Test-Path ureport2-core/src/main/resources/META-INF/services/com.bstek.ureport.bootstrap.UReportBootstrap
Test-Path ureport2-font/src/main/resources/META-INF/services/com.bstek.ureport.bootstrap.UReportBootstrap
Test-Path ureport2-console/src/main/resources/META-INF/services/com.bstek.ureport.bootstrap.UReportBootstrap
```
Expected: True for all three.

- [ ] **Step 6: Commit any final fixes (if any)**

If steps 1-5 required fixes, commit them:
```bash
git add -A
git commit -m "chore: final verification fixes for v4.0.0"
```

---

## Self-Review Notes

- **Spec coverage:** Every section of the spec maps to a task. Section 2 (modules) → Task 18/19. Section 3 (engine + registries) → Task 1. Section 4 (XML→programmatic) → Tasks 4-6. Section 5 (BeanDatasourceProvider) → Tasks 1+12+19. Section 6 (JDBC + resource) → Tasks 2+3+13+14+15. Section 7 (web bootstrap) → Tasks 16+17. Section 8 (Spring Boot starter) → Task 19. Section 9 (files touched) — all 21 Java + 3 XML + 4 pom + 1 new module covered. Section 10 (out of scope) respected — no full test suite, only the one focused JDBC test the spec's risk section flagged.
- **Type consistency:** `Registry<T>.all()` returns `List<T>` throughout. `UReportEngine.getInstance()` used uniformly. `UReportJdbcUtils.parseNamedSql`/`buildValueArray`/`queryForList`/`closeQuietly` signatures consistent across Tasks 2, 14, 15. `UReportResourceUtils.getResourceAsStream(path, ServletContext)` + `CLASSPATH_URL_PREFIX` consistent across Tasks 3, 9, 13, 16. `BeanDatasourceProvider` method names (`frameworkName`/`supports`/`getBean`/`getMethods`/`invoke`) consistent across Tasks 1, 12, 15, 19. `UReportBootstrap.bootstrap(UReportEngine)` + `order()` consistent across Tasks 4, 5, 6.
- **Compile-green invariant:** Spring deps are removed only in Task 18, after every `org.springframework` import is gone from `.java` files (Tasks 7-17). Each task verifies with `mvn compile` before committing.
