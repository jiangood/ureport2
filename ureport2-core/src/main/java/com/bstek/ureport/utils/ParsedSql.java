package com.bstek.ureport.utils;

import java.util.ArrayList;
import java.util.List;

public class ParsedSql {
	private String sql;
	private final List<String> parameterNames = new ArrayList<String>();
	public String getSql() { return sql; }
	public void setSql(String sql) { this.sql = sql; }
	public List<String> getParameterNames() { return parameterNames; }
	public void addParameter(String name) { parameterNames.add(name); }
}
