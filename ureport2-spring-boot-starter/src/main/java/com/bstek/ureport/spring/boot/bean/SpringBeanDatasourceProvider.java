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
package com.bstek.ureport.spring.boot.bean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;

import com.bstek.ureport.definition.datasource.BeanDatasourceProvider;
import com.bstek.ureport.definition.datasource.MethodInfo;

public class SpringBeanDatasourceProvider implements BeanDatasourceProvider {
	private final ApplicationContext applicationContext;

	public SpringBeanDatasourceProvider(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public String frameworkName() {
		return "spring";
	}

	@Override
	public boolean supports(String beanId) {
		return applicationContext.containsBean(beanId);
	}

	@Override
	public Object getBean(String beanId) {
		return applicationContext.getBean(beanId);
	}

	@Override
	public List<MethodInfo> getMethods(String beanId) {
		Object obj = getBean(beanId);
		List<MethodInfo> result = new ArrayList<MethodInfo>();
		for (Method method : obj.getClass().getMethods()) {
			Class<?>[] types = method.getParameterTypes();
			if (types.length != 3) {
				continue;
			}
			if (!String.class.isAssignableFrom(types[0])) {
				continue;
			}
			if (!String.class.isAssignableFrom(types[1])) {
				continue;
			}
			if (!Map.class.isAssignableFrom(types[2])) {
				continue;
			}
			List<Class<?>> paramTypes = new ArrayList<Class<?>>();
			for (Class<?> t : types) {
				paramTypes.add(t);
			}
			result.add(new MethodInfo(method.getName(), method.getReturnType(), paramTypes));
		}
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> invoke(String beanId, String methodName, String datasetName, Map<String, Object> params) {
		Object obj = getBean(beanId);
		try {
			Method method = obj.getClass().getMethod(methodName, String.class, String.class, Map.class);
			return (List<Map<String, Object>>) method.invoke(obj, datasetName, datasetName, params);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
