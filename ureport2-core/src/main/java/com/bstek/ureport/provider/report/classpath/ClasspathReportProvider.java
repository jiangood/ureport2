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
package com.bstek.ureport.provider.report.classpath;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.bstek.ureport.UReportEngine;
import com.bstek.ureport.exception.ReportException;
import com.bstek.ureport.provider.report.ReportFile;
import com.bstek.ureport.provider.report.ReportProvider;
import com.bstek.ureport.utils.UReportResourceUtils;

/**
 * @author Jacky.gao
 * @since 2016年12月4日
 */
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
			if (alt != null) {
				return alt;
			}
		}
		throw new ReportException(new IOException("Report resource not found: " + file));
	}

	@Override
	public String getPrefix() {
		return "classpath";
	}

	@Override
	public void deleteReport(String file) {
	}

	@Override
	public void saveReport(String file, String content) {
	}
	@Override
	public List<ReportFile> getReportFiles() {
		return null;
	}

	@Override
	public boolean disabled() {
		return false;
	}

	@Override
	public String getName() {
		return null;
	}
}
