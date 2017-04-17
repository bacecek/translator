package com.bacecek.translate.data.entity;

/**
 * Created by Denis Buzmakov on 16/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class Setting {
	private int id;
	private String name;
	private boolean value;

	public Setting(int id, String name, boolean value) {
		this.id = id;
		this.name = name;
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}
}
