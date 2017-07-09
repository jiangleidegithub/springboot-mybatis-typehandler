package com.jianglei.mstransfer.model;

import java.io.Serializable;

/**
 * @author 
 */
public class OtherInfo  implements Serializable {
	private static final long serialVersionUID = 5141691466216455298L;

	private String height;

    private String array;

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getArray() {
		return array;
	}

	public void setArray(String array) {
		this.array = array;
	}

	@Override
	public String toString() {
		return "OtherInfo [height=" + height + ", array=" + array + "]";
	}

}