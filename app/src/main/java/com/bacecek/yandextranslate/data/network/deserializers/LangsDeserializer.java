package com.bacecek.yandextranslate.data.network.deserializers;

import com.bacecek.yandextranslate.data.db.entities.Language;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * Created by Denis Buzmakov on 21/03/2017.
 * <buzmakov.da@gmail.com>
 */

public class LangsDeserializer implements JsonDeserializer<List<Language>> {

	@Override
	public List<Language> deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context)
			throws JsonParseException {
		final ArrayList<Language> list = new ArrayList<Language>();
		final JsonObject jsonObject = json.getAsJsonObject();

		JsonObject langs = jsonObject.getAsJsonObject("langs");
		for (Entry<String, JsonElement> entry : langs.entrySet()) {
			Language lang = new Language(entry.getKey(), entry.getValue().getAsString());
			list.add(lang);
		}

		return list;
	}
}
