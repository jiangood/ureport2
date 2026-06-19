<p align="center">
  <a href="https://github.com/jiangood/ureport2"><img alt="Java" src="https://img.shields.io/badge/Java-8+-blue.svg"></a>
  <a href="https://github.com/jiangood/ureport2"><img alt="License" src="https://img.shields.io/github/license/jiangood/ureport2"></a>
  <a href="https://search.maven.org/artifact/io.github.jiangood/ureport2-parent"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/io.github.jiangood/ureport2-parent"></a>
</p>

# 简介

UReport2 是一款高性能的架构在 Spring 之上纯 Java 报表引擎，通过迭代单元格可以实现任意复杂的中国式报表。

在 UReport2 中，提供了全新的基于网页的报表设计器，可以在 Chrome、Firefox、Edge 等各种主流浏览器运行（IE 浏览器除外），打开浏览器即可完成各种复杂报表的设计制作。

UReport2 是第一款基于 Apache-2.0 协议开源的中式报表引擎。

## 文档与视频教程

- BSDN WIKI：[http://wiki.bsdn.org/display/UR/ureport2+Home](http://wiki.bsdn.org/display/UR/ureport2+Home)
- w3cschool：[https://www.w3cschool.cn/ureport](https://www.w3cschool.cn/ureport)

# QQ 群

- 一群：423339793（已满）
- 二群：636590564

# 安装与配置

UReport2 是纯 Java 报表引擎，支持当前所有主流的 J2EE 项目。下面介绍基于 Maven 的 J2EE 项目如何集成 UReport2。

### Maven

在 `pom.xml` 中添加依赖：

```
<dependency>
    <groupId>io.github.jiangood</groupId>
    <artifactId>ureport2-console</artifactId>
    <version>3.0.0</version>
</dependency>
```

然后需要在 `web.xml` 中配置 UReport2 使用的 Servlet：

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

由于 UReport2 基于 Spring 框架构建，最后需要加载 UReport2 的 Spring 配置文件。可以在 `web.xml` 中添加 Spring 提供的监听器，直接加载 UReport2 的 Spring 配置文件：

```
<listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>classpath:ureport-console-context.xml</param-value>
</context-param>
```

如果项目中已经使用了 Spring，则可以在已有的 Spring 配置文件中导入 UReport2 的配置文件：

```
<import resource="classpath:ureport-console-context.xml" />
```

完成以上配置后，启动项目，在浏览器中访问 `http://localhost:8080/[contextPath]/ureport/designer` 即可看到 UReport2 的报表设计器界面。

![](docs/images/dd.png)
![](docs/images/designer.png)

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
