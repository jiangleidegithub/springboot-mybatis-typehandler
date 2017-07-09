package com.jianglei.mstransfer.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import com.alibaba.fastjson.JSON;

@MappedTypes(com.jianglei.mstransfer.model.OtherInfo.class)
//@MappedJdbcTypes({JdbcType.LONGVARCHAR})
public class JSONTypeHandler<T extends Object> extends BaseTypeHandler<T> {
	private Class<T> clazz;

	public JSONTypeHandler(Class<T> clazz) {
		if (clazz == null)
			throw new IllegalArgumentException("Type argument cannot be null");
		this.clazz = clazz;
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
		ps.setObject(i, (T) JSON.parseObject(parameter.toString(), clazz));
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
		T parse = (T) JSON.parseObject(rs.getString(columnName),clazz);
		return parse;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		T parse = (T) JSON.parseObject(rs.getString(columnIndex),clazz);
		return parse;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		T parse = (T) JSON.parseObject(cs.getString(columnIndex),clazz);
		return parse;
	}

}
