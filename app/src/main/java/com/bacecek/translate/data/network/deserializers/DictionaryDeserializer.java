package com.bacecek.translate.data.network.deserializers;

import com.bacecek.translate.data.entities.DictionaryExample;
import com.bacecek.translate.data.entities.DictionaryItem;
import com.bacecek.translate.data.entities.DictionaryMean;
import com.bacecek.translate.data.entities.DictionaryPos;
import com.bacecek.translate.data.entities.DictionarySynonym;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Denis Buzmakov on 06/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class DictionaryDeserializer implements JsonDeserializer<List<DictionaryItem>> {
	private final static String JSON_SYNONYM = "syn";
	private final static String JSON_MEAN = "mean";
	private final static String JSON_EXAMPLE = "ex";

	@Override
	public List<DictionaryItem> deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		ArrayList<DictionaryItem> items = new ArrayList<DictionaryItem>();

		JsonObject jsonObject = json.getAsJsonObject();
		JsonArray def = jsonObject.getAsJsonArray("def");

		for(JsonElement element : def) {
			JsonObject obj = element.getAsJsonObject();

			if(obj.has("pos")) {
				String text = obj.get("pos").getAsString();
				DictionaryPos pos = new DictionaryPos(text);
				items.add(pos);
			}

			if(obj.has("tr")) {
				JsonArray tr = obj.getAsJsonArray("tr");
				for(JsonElement trElement : tr) {
					JsonObject trObj = trElement.getAsJsonObject();
					items.add(parseSynonym(trObj));

					parseArray(trObj, JSON_SYNONYM, items);
					parseArray(trObj, JSON_MEAN, items);
					parseArray(trObj, JSON_EXAMPLE, items);
				}
			}
		}
		return items;
	}

	private void parseArray(JsonObject obj, String field, ArrayList<DictionaryItem> items) {
		if (obj.has(field)) {
			JsonArray array = obj.getAsJsonArray(field);
			for(JsonElement element : array) {
				JsonObject arrayObj = element.getAsJsonObject();
				switch (field) {
					case JSON_EXAMPLE:
						items.add(parseExample(arrayObj));
						break;
					case JSON_MEAN:
						items.add(parseMean(arrayObj));
						break;
					case JSON_SYNONYM:
						items.add(parseSynonym(arrayObj));
						break;
				}
			}
		}
	}

	private DictionarySynonym parseSynonym(JsonObject obj) {
		String text = null, pos = null, gen = null;
		if (obj.has("text")) {
			text = obj.get("text").getAsString();
		}
		if (obj.has("pos")) {
			pos = obj.get("pos").getAsString();
		}
		if (obj.has("gen")) {
			gen = obj.get("gen").getAsString();
		}
		return new DictionarySynonym(text, pos, gen);
	}

	private DictionaryMean parseMean(JsonObject obj) {
		String text = null;
		if (obj.has("text")) {
			text = obj.get("text").getAsString();
		}
		return new DictionaryMean(text);
	}

	private DictionaryExample parseExample(JsonObject obj) {
		String text = null;
		String[] translations = null;

		if (obj.has("text")) {
			text = obj.get("text").getAsString();
		}
		if (obj.has("tr")) {
			JsonArray array = obj.getAsJsonArray("tr");
			translations = new String[array.size()];
			for(int i = 0; i < array.size(); i++) {
				JsonObject exObj = array.get(i).getAsJsonObject();
				String translation = null;
				if (exObj.has("text")) {
					translation = exObj.get("text").getAsString();
				}
				translations[i] = translation;
			}
		}
		return new DictionaryExample(text, translations);
	}
}
