package com.jianglei.mstransfer.model;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.JSONArray;

/**
 * @author
 */
public class User implements Serializable {
	private static final long serialVersionUID = 9114009126585527347L;

	private Integer id;

	private String name;

	private OtherInfo json;

	private JSONArray array;

	private List<OtherInfo2> list;

	public Integer getId() { 
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public OtherInfo getJson() {
		return json;
	}

	public void setJson(OtherInfo json) {
		this.json = json;
	}

	public JSONArray getArray() {
		return array;
	}

	public void setArray(JSONArray array) {
		this.array = array;
	}

	public List<OtherInfo2> getList() {
		return this.array.toJavaList(OtherInfo2.class);
	}

	public void setList(List<OtherInfo2> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", json=" + json + ", array=" + array + ", list=" + list + "]";
	}

}