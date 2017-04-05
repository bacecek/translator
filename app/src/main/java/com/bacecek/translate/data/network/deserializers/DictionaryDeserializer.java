package com.bacecek.translate.data.network.deserializers;

import com.bacecek.translate.data.db.entities.DictionaryExample;
import com.bacecek.translate.data.db.entities.DictionaryItem;
import com.bacecek.translate.data.db.entities.DictionaryTranslation;
import com.bacecek.translate.data.db.entities.DictionaryWord;
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
 * Created by Denis Buzmakov on 05/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class DictionaryDeserializer implements JsonDeserializer<List<DictionaryItem>> {
	//TODO:привести в нормальный читабельный вид

	@Override
	public ArrayList<DictionaryItem> deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		ArrayList<DictionaryItem> items = new ArrayList<DictionaryItem>();

		JsonObject jsonObject = json.getAsJsonObject();
		JsonArray def = jsonObject.getAsJsonArray("def");

		for(JsonElement element : def) {
			JsonObject obj = element.getAsJsonObject();
			DictionaryItem item = new DictionaryItem();
			if(obj.has("text")) {
				item.setText(obj.get("text").getAsString());
			}
			if(obj.has("pos")) {
				item.setPos(obj.get("pos").getAsString());
			}
			if(obj.has("gen")) {
				item.setGen(obj.get("gen").getAsString());
			}
			if(obj.has("ts")) {
				item.setTs(obj.get("ts").getAsString());
			}
			if(obj.has("anm")) {
				item.setAnm(obj.get("anm").getAsString());
			}
			if(obj.has("tr")) {
				ArrayList<DictionaryTranslation> translations = new ArrayList<DictionaryTranslation>();
				JsonArray tr = obj.getAsJsonArray("tr");
				for (JsonElement trElement : tr) {
					JsonObject trObj = trElement.getAsJsonObject();
					DictionaryTranslation translation = new DictionaryTranslation();
					if (trObj.has("text")) {
						translation.setText(trObj.get("text").getAsString());
					}
					if (trObj.has("pos")) {
						translation.setPos(trObj.get("pos").getAsString());
					}
					if (trObj.has("gen")) {
						translation.setGen(trObj.get("gen").getAsString());
					}

					if (trObj.has("syn")) {
						JsonArray array = trObj.getAsJsonArray("syn");
						ArrayList<DictionaryWord> synonyms = new ArrayList<DictionaryWord>();
						for (JsonElement synElement : array) {
							JsonObject synObj = synElement.getAsJsonObject();
							DictionaryWord syn = parseDictionaryWord(synObj);
							synonyms.add(syn);
						}
						translation.setSynonyms(synonyms);
					}

					if (trObj.has("mean")) {
						JsonArray array = trObj.getAsJsonArray("mean");
						ArrayList<DictionaryWord> means = new ArrayList<DictionaryWord>();
						for (JsonElement meanElement : array) {
							JsonObject meanObj = meanElement.getAsJsonObject();
							DictionaryWord mean = parseDictionaryWord(meanObj);
							means.add(mean);
						}
						translation.setMeans(means);
					}

					if (trObj.has("ex")) {
						JsonArray array = trObj.getAsJsonArray("ex");
						ArrayList<DictionaryExample> exs = new ArrayList<DictionaryExample>();
						for (JsonElement exElement : array) {
							JsonObject exObj = exElement.getAsJsonObject();
							DictionaryExample ex = new DictionaryExample();

							if (exObj.has("text")) {
								ex.setText(exObj.get("text").getAsString());
							}
							if (exObj.has("pos")) {
								ex.setPos(exObj.get("pos").getAsString());
							}
							if (exObj.has("gen")) {
								ex.setGen(exObj.get("gen").getAsString());
							}
							if (exObj.has("tr")) {
								JsonArray trArray = exObj.getAsJsonArray("tr");
								ArrayList<String> trs = new ArrayList<String>();
								for (JsonElement trExElement : trArray) {
									JsonObject trExObj = trExElement.getAsJsonObject();
									if (trExObj.has("text")) {
										trs.add(trExObj.get("text").getAsString());
									}
								}
								ex.setExamples(trs);
							}
							exs.add(ex);
						}
						translation.setExamples(exs);
					}
					translations.add(translation);
				}
				item.setTranslations(translations);
			}
			items.add(item);
		}

		return items;
	}

	private DictionaryWord parseDictionaryWord(JsonObject obj) {
		DictionaryWord word = new DictionaryWord();
		if(obj.has("text")) {
			word.setText(obj.get("text").getAsString());
		}
		if(obj.has("pos")) {
			word.setPos(obj.get("pos").getAsString());
		}
		if(obj.has("gen")) {
			word.setGen(obj.get("gen").getAsString());
		}
		return word;
	}
}
