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
package com.bstek.ureport.console;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.bstek.ureport.utils.UStringUtils;
import com.bstek.ureport.UReportEngine;

/**
 * @author Jacky.gao
 * @since 2017年1月25日
 */
@MultipartConfig
public class UReportServlet extends HttpServlet {
	private static final long serialVersionUID = 533049461276487971L;
	public static final String URL = "/ureport";
	private Map<String, ServletAction> actionMap = new HashMap<String, ServletAction>();

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

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getContextPath() + URL;
		String uri = req.getRequestURI();
		String targetUrl = uri.substring(path.length());
		if (targetUrl.length() < 1) {
			outContent(resp, "Welcome to use ureport,please specify target url.");
			return;
		}
		int slashPos = targetUrl.indexOf("/", 1);
		if (slashPos > -1) {
			targetUrl = targetUrl.substring(0, slashPos);
		}
		ServletAction targetHandler = actionMap.get(targetUrl);
		if (targetHandler == null) {
			outContent(resp, "Handler [" + targetUrl + "] not exist.");
			return;
		}
		RequestHolder.setRequest(req);
		try{
			targetHandler.execute(req, resp);
		}catch(Exception ex){
			resp.setCharacterEncoding("UTF-8");
			PrintWriter pw=resp.getWriter();
			Throwable e=buildRootException(ex);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			String errorMsg = e.getMessage();
			if(UStringUtils.isBlank(errorMsg)){
				errorMsg=e.getClass().getName();
			}
			pw.write(errorMsg);
			pw.close();				
			throw new ServletException(ex);	
		}finally{
			RequestHolder.clean();
		}
	}
	private Throwable buildRootException(Throwable throwable){
		if(throwable.getCause()==null){
			return throwable;
		}
		return buildRootException(throwable.getCause());
	}

	private void outContent(HttpServletResponse resp, String msg) throws IOException {
		resp.setContentType("text/html");
		PrintWriter pw = resp.getWriter();
		pw.write("<html>");
		pw.write("<header><title>UReport Console</title></header>");
		pw.write("<body>");
		pw.write(msg);
		pw.write("</body>");
		pw.write("</html>");
		pw.flush();
		pw.close();
	}
}
