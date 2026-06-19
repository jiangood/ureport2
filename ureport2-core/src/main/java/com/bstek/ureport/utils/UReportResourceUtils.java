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
