package com.bacecek.translate.data.network.deserializer;

import com.bacecek.translate.data.entity.DictionaryExample;
import com.bacecek.translate.data.entity.DictionaryItem;
import com.bacecek.translate.data.entity.DictionaryMeanList;
import com.bacecek.translate.data.entity.DictionaryMeanList.DictionaryMean;
import com.bacecek.translate.data.entity.DictionaryPos;
import com.bacecek.translate.data.entity.DictionarySynonymList;
import com.bacecek.translate.data.entity.DictionarySynonymList.DictionarySynonym;
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

/**
 * Класс для парсинга ответа с API словаря
 */
public class DictionaryDeserializer implements JsonDeserializer<List<DictionaryItem>> {

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

					items.add(parseSynonyms(trObj));


					if(trObj.has("mean")) {
						items.add(parseMeans(trObj));
					}

					if(trObj.has("ex")) {
						JsonArray exs = trObj.getAsJsonArray("ex");
						for(JsonElement exElement : exs) {
							JsonObject exObj = exElement.getAsJsonObject();
							items.add(parseExample(exObj));
						}
					}
				}
			}
		}
		return items;
	}

	private DictionarySynonymList parseSynonyms(JsonObject obj) {
		ArrayList<DictionarySynonym> synonyms = new ArrayList<DictionarySynonym>();
		synonyms.add(parseSynonym(obj));

		if(obj.has("syn")) {
			JsonArray array = obj.getAsJsonArray("syn");
			for(JsonElement element : array) {
				JsonObject synObj = element.getAsJsonObject();
				synonyms.add(parseSynonym(synObj));
			}
		}

		return new DictionarySynonymList(synonyms);
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

	private DictionaryMeanList parseMeans(JsonObject obj) {
		ArrayList<DictionaryMean> means = new ArrayList<DictionaryMean>();
		JsonArray array = obj.getAsJsonArray("mean");
		for(JsonElement element : array) {
			JsonObject meanObj = element.getAsJsonObject();
			means.add(parseMean(meanObj));
		}

		return new DictionaryMeanList(means);
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
