package com.bstek.ureport.definition.datasource;

import java.util.List;

public class MethodInfo {
	private String name;
	private Class<?> returnType;
	private List<Class<?>> parameterTypes;
	public MethodInfo(String name, Class<?> returnType, List<Class<?>> parameterTypes) {
		this.name = name;
		this.returnType = returnType;
		this.parameterTypes = parameterTypes;
	}
	public String getName() { return name; }
	public Class<?> getReturnType() { return returnType; }
	public List<Class<?>> getParameterTypes() { return parameterTypes; }
}
