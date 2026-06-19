package com.bstek.ureport.bootstrap;

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
