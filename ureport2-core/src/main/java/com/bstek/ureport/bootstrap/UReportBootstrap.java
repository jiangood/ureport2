package com.bstek.ureport.bootstrap;

import com.bstek.ureport.UReportEngine;

public interface UReportBootstrap {
	void bootstrap(UReportEngine engine);
	int order();
}
