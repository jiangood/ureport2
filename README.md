<p align="center">
  <a href="https://github.com/jiangood/ureport2"><img alt="Java" src="https://img.shields.io/badge/Java-8+-blue.svg"></a>
  <a href="https://github.com/jiangood/ureport2"><img alt="License" src="https://img.shields.io/github/license/jiangood/ureport2"></a>
  <a href="https://search.maven.org/artifact/io.github.jiangood/ureport2-parent"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/io.github.jiangood/ureport2-parent"></a>
</p>

# 简介

UReport2 是一款高性能的框架无关（framework-agnostic）纯 Java 报表引擎，通过迭代单元格可以实现任意复杂的中国式报表。

在 UReport2 中，提供了全新的基于网页的报表设计器，可以在 Chrome、Firefox、Edge 等各种主流浏览器运行（IE 浏览器除外），打开浏览器即可完成各种复杂报表的设计制作。

UReport2 是第一款基于 Apache-2.0 协议开源的中式报表引擎。

## 文档与视频教程

- BSDN WIKI：[http://wiki.bsdn.org/display/UR/ureport2+Home](http://wiki.bsdn.org/display/UR/ureport2+Home)
- w3cschool：[https://www.w3cschool.cn/ureport](https://www.w3cschool.cn/ureport)

# QQ 群

- 一群：423339793（已满）
- 二群：636590564

# 安装与配置

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

注意：`url-pattern` 必须保持为 `/ureport/*`，否则系统无法正常运行。

`UReportServlet` 在初始化时自动完成 `UReportEngine` 的引导，无需 `ContextLoaderListener` 或 Spring 配置文件。

完成以上配置后，启动项目，在浏览器中访问 `http://localhost:8080/[contextPath]/ureport/designer` 即可看到 UReport2 的报表设计器界面。

![](docs/images/dd.png)
![](docs/images/designer.png)

### 自定义扩展（SPI）

UReport2 通过 Java SPI 发现用户扩展。实现以下任意接口并在 `META-INF/services/` 下注册即可：

- `com.bstek.ureport.provider.report.ReportProvider` — 报表存储
- `com.bstek.ureport.definition.datasource.BuildinDatasource` — 内置数据源
- `com.bstek.ureport.definition.datasource.DatasourceProvider` — 数据源提供者
- `com.bstek.ureport.provider.image.ImageProvider` — 图片提供者
- `com.bstek.ureport.expression.function.Function` — 表达式函数
- `com.bstek.ureport.export.pdf.font.FontRegister` — 字体注册
- `com.bstek.ureport.definition.datasource.BeanDatasourceProvider` — Bean 数据源（用于 Spring Bean 数据源特性，Spring 用户使用 `ureport2-spring-boot-starter` 自动获得）

例如，自定义一个报表存储，在 `META-INF/services/com.bstek.ureport.provider.report.ReportProvider` 文件中写入实现类的全限定名即可。

# 相关文档

- [报表存储与数据源配置](docs/STORAGE-DATASOURCE.md)
- [报表计算模型介绍](docs/REPORT-MODEL.md)
- [表达式](docs/EXPRESSION.md)
- 函数
- 条件属性
- 参数
- 与业务结合
- 行类型
- 添加空白行与分页
- 加载图片
- 套打
