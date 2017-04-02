package com.bacecek.yandextranslate.data.network.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

/**
 * Created by Denis Buzmakov on 22/03/2017.
 * <buzmakov.da@gmail.com>
 */

public class DetectLangDeserializer implements JsonDeserializer<String> {

	@Override
	public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		String lang;
		JsonObject jsonObject = json.getAsJsonObject();

		lang = jsonObject.get("lang").getAsString();

		return lang;
	}
}
