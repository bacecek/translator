package com.bacecek.yandextranslate.data.db.entities;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Denis Buzmakov on 20/03/2017.
 * <buzmakov.da@gmail.com>
 */

public class Language extends RealmObject {
	@PrimaryKey
	private String code;
	private String name;

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
}
