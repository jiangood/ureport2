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
import com.bstek.ureport.build.ReportBuilder;
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
		instance = engine;
		engine.runBootstraps();
		engine.loadUserPlugins();
		engine.refreshManagers();
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
		registerFormParsers();
		registerAll(BeanDatasourceProvider.class, beanDatasourceRegistry);
	}

	private <T> void registerAll(Class<T> spi, Registry<T> registry) {
		for (T impl : ServiceLoader.load(spi)) {
			registry.register(impl);
		}
	}

	private void refreshManagers() {
		if (reportBuilder != null) {
			reportBuilder.init();
		}
		if (reportRender != null) {
			reportRender.init();
		}
	}

	@SuppressWarnings("unchecked")
	private void registerFormParsers() {
		for (FormParser<?> parser : ServiceLoader.load(FormParser.class)) {
			formParserRegistry.register(parser);
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
