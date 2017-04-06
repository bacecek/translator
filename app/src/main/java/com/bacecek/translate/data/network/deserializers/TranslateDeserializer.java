package com.bacecek.translate.data.network.deserializers;

import com.bacecek.translate.data.entities.Translation;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

/**
 * Created by Denis Buzmakov on 21/03/2017.
 * <buzmakov.da@gmail.com>
 */

public class TranslateDeserializer implements JsonDeserializer<Translation> {

	@Override
	public Translation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		Translation translation = new Translation();
		JsonObject jsonObject = json.getAsJsonObject();

		String[] langs = jsonObject.get("lang").getAsString().split("-");
		translation.setOriginalLang(langs[0]);
		translation.setTargetLang(langs[1]);
		JsonArray textArray = jsonObject.getAsJsonArray("text");
		translation.setTranslatedText(textArray.get(0).getAsString());

		return translation;
	}
}
