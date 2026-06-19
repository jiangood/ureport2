/*******************************************************************************
 * Copyright 2017 Bstek
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
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
