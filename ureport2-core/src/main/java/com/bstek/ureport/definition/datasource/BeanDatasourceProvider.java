package com.bstek.ureport.definition.datasource;

import java.util.List;
import java.util.Map;

public interface BeanDatasourceProvider {
	String frameworkName();
	boolean supports(String beanId);
	Object getBean(String beanId);
	List<MethodInfo> getMethods(String beanId);
	List<Map<String, Object>> invoke(String beanId, String methodName, String datasetName, Map<String, Object> params);
}
