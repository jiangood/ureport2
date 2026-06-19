package com.bstek.ureport.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UReportJdbcUtils {

	public static ParsedSql parseNamedSql(String sql) {
		ParsedSql parsed = new ParsedSql();
		StringBuilder sqlToUse = new StringBuilder(sql.length());
		char[] chars = sql.toCharArray();
		int i = 0;
		while (i < chars.length) {
			char c = chars[i];
			if (c == '\'' || c == '"') {
				char quote = c;
				sqlToUse.append(c);
				i++;
				while (i < chars.length) {
					char q = chars[i];
					sqlToUse.append(q);
					if (q == quote) {
						if (i + 1 < chars.length && chars[i + 1] == quote) {
							sqlToUse.append(chars[i + 1]);
							i += 2;
							continue;
						}
						i++;
						break;
					}
					i++;
				}
				continue;
			}
			if (c == ':') {
				int j = i + 1;
				if (j < chars.length && chars[j] == ':') {
					sqlToUse.append(c);
					i++;
					continue;
				}
				StringBuilder name = new StringBuilder();
				while (j < chars.length && Character.isJavaIdentifierPart(chars[j])) {
					name.append(chars[j]);
					j++;
				}
				if (name.length() == 0) {
					sqlToUse.append(c);
					i++;
					continue;
				}
				parsed.addParameter(name.toString());
				sqlToUse.append('?');
				i = j;
				continue;
			}
			sqlToUse.append(c);
			i++;
		}
		parsed.setSql(sqlToUse.toString());
		return parsed;
	}

	public static Object[] buildValueArray(ParsedSql parsedSql, Map<String, Object> params) {
		List<String> names = parsedSql.getParameterNames();
		Object[] values = new Object[names.size()];
		for (int k = 0; k < names.size(); k++) {
			values[k] = params == null ? null : params.get(names.get(k));
		}
		return values;
	}

	public static List<Map<String, Object>> queryForList(Connection conn, String sql, Map<String, Object> params) {
		ParsedSql parsed = parseNamedSql(sql);
		Object[] values = buildValueArray(parsed, params);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(parsed.getSql());
			for (int k = 0; k < values.length; k++) {
				ps.setObject(k + 1, values[k]);
			}
			rs = ps.executeQuery();
			ResultSetMetaData metadata = rs.getMetaData();
			int columnCount = metadata.getColumnCount();
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			while (rs.next()) {
				Map<String, Object> row = new LinkedHashMap<String, Object>();
				for (int c = 1; c <= columnCount; c++) {
					row.put(metadata.getColumnLabel(c), rs.getObject(c));
				}
				result.add(row);
			}
			return result;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			closeQuietly(rs);
			closeQuietly(ps);
		}
	}

	public static void closeQuietly(Connection conn) {
		if (conn != null) {
			try { conn.close(); } catch (SQLException ignored) {}
		}
	}

	public static void closeQuietly(Statement stmt) {
		if (stmt != null) {
			try { stmt.close(); } catch (SQLException ignored) {}
		}
	}

	public static void closeQuietly(ResultSet rs) {
		if (rs != null) {
			try { rs.close(); } catch (SQLException ignored) {}
		}
	}
}
