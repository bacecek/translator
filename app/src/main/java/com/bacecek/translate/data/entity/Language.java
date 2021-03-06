package com.bacecek.translate.data.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Denis Buzmakov on 20/03/2017.
 * <buzmakov.da@gmail.com>
 */

public class Language extends RealmObject {
	@PrimaryKey
	private String code; //primary key, т.к. других таких языков с таким же кодом не может быть
	private String name;
	private long lastUsedTimeStamp;

	public Language() {
	}

	public Language(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getLastUsedTimeStamp() {
		return lastUsedTimeStamp;
	}

	public void setLastUsedTimeStamp(long lastUsedTimeStamp) {
		this.lastUsedTimeStamp = lastUsedTimeStamp;
	}
}
