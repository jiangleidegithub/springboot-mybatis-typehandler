package com.jianglei.mstransfer.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import com.alibaba.fastjson.JSONArray;

@MappedTypes({com.alibaba.fastjson.JSONArray.class})
public class JSONArrayTypeHandler<T> extends BaseTypeHandler<T> {
	private Class<T> clazz;

	public JSONArrayTypeHandler(Class<T> clazz) {
		if (clazz == null)
			throw new IllegalArgumentException("Type argument cannot be null");
		this.clazz = clazz;
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
//		TODO
		ps.setObject(i, JSONArray.parseArray(parameter.toString()));
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
		T t  = (T) JSONArray.parseArray(rs.getString(columnName));
		return t;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		T parse = (T) JSONArray.parseArray(rs.getString(columnIndex));
		return parse;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		T parse = (T) JSONArray.parseArray(cs.getString(columnIndex));
		return parse;
	}

}
