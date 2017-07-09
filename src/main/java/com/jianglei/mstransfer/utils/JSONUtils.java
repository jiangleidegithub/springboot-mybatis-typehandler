package com.jianglei.mstransfer.utils;

import com.alibaba.fastjson.JSON;

public class JSONUtils<T extends Object> {

	@SuppressWarnings("unchecked")
	public T parseJSON(String json) {
		Object o = JSON.parse(json);
		return (T) o;
	}
}
