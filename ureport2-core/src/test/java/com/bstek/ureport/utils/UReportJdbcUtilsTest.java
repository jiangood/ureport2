package com.bstek.ureport.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class UReportJdbcUtilsTest {

	@Test
	public void parseNamedSql_replacesColonParamsWithQuestionMarks() {
		ParsedSql parsed = UReportJdbcUtils.parseNamedSql("select * from t where a=:a and b=:b");
		assertEquals("select * from t where a=? and b=?", parsed.getSql());
		assertEquals(2, parsed.getParameterNames().size());
		assertEquals("a", parsed.getParameterNames().get(0));
		assertEquals("b", parsed.getParameterNames().get(1));
	}

	@Test
	public void parseNamedSql_ignoresParamsInsideQuotes() {
		ParsedSql parsed = UReportJdbcUtils.parseNamedSql("select ':a' as x where b=:b");
		assertEquals("select ':a' as x where b=?", parsed.getSql());
		assertEquals(1, parsed.getParameterNames().size());
		assertEquals("b", parsed.getParameterNames().get(0));
	}

	@Test
	public void parseNamedSql_keepsPositionalQuestionMarks() {
		ParsedSql parsed = UReportJdbcUtils.parseNamedSql("select * from t where a=? and b=:b");
		assertEquals("select * from t where a=? and b=?", parsed.getSql());
		assertEquals(1, parsed.getParameterNames().size());
		assertEquals("b", parsed.getParameterNames().get(0));
	}

	@Test
	public void buildValueArray_ordersValuesByParameterNames() {
		ParsedSql parsed = new ParsedSql();
		parsed.setSql("where a=? and b=?");
		parsed.addParameter("a");
		parsed.addParameter("b");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("a", 1);
		params.put("b", "two");
		Object[] values = UReportJdbcUtils.buildValueArray(parsed, params);
		assertArrayEquals(new Object[]{1, "two"}, values);
	}
}
