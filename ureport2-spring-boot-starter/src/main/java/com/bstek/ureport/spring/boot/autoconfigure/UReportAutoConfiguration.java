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
