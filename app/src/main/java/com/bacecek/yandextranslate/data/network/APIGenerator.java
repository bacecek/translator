package com.bacecek.yandextranslate.data.network;

import com.bacecek.yandextranslate.BuildConfig;
import com.bacecek.yandextranslate.data.db.entities.Language;
import com.bacecek.yandextranslate.data.db.entities.Translation;
import com.bacecek.yandextranslate.data.network.deserializers.DetectLangDeserializer;
import com.bacecek.yandextranslate.data.network.deserializers.LangsDeserializer;
import com.bacecek.yandextranslate.data.network.deserializers.TranslateDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.List;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Denis Buzmakov on 20/03/2017.
 * <buzmakov.da@gmail.com>
 */

public class APIGenerator {

	private static final Gson sGson = new GsonBuilder()
			.registerTypeAdapter(new TypeToken<List<Language>>(){}.getType(), new LangsDeserializer())
			.registerTypeAdapter(new TypeToken<Translation>(){}.getType(), new TranslateDeserializer())
			.registerTypeAdapter(new TypeToken<String>(){}.getType(), new DetectLangDeserializer())
			.create();

	private static final Retrofit.Builder sTranslatorRetrofitBuilder = new Builder().
			baseUrl(BuildConfig.YANDEX_TRANSLATE_API_URL).
			addConverterFactory(GsonConverterFactory.create(sGson));
	private static final Retrofit.Builder sDictionaryRetrofitBuilder = new Builder().
			baseUrl(BuildConfig.YANDEX_DICTIONARY_API_URL).
			addConverterFactory(GsonConverterFactory.create(sGson));

	private static final OkHttpClient.Builder sTranslatorOkHttpBuilder = new OkHttpClient.Builder().addInterceptor(new CustomInterceptor(BuildConfig.YANDEX_TRANSLATE_API_KEY));
	private static final OkHttpClient.Builder sDictionaryOkHttpBuilder = new OkHttpClient.Builder().addInterceptor(new CustomInterceptor(BuildConfig.YANDEX_DICTIONARY_API_KEY));

	public static TranslatorAPI createTranslatorService() {
		OkHttpClient client = sTranslatorOkHttpBuilder.build();
		Retrofit retrofit = sTranslatorRetrofitBuilder.client(client).build();
		return retrofit.create(TranslatorAPI.class);
	}

	public static DictionaryAPI createDictionaryService() {
		OkHttpClient client = sDictionaryOkHttpBuilder.build();
		Retrofit retrofit = sDictionaryRetrofitBuilder.client(client).build();
		return retrofit.create(DictionaryAPI.class);
	}

	private static class CustomInterceptor implements Interceptor {
		private String mKey;

		CustomInterceptor(String key) {
			mKey = key;
		}

		@Override
		public Response intercept(Chain chain) throws IOException {
			Request original = chain.request();
			HttpUrl originalHttpUrl = original.url();

			HttpUrl url = originalHttpUrl.newBuilder()
					.addQueryParameter("key", mKey)
					.build();
			Request.Builder requestBuilder = original.newBuilder()
					.url(url);

			Request request = requestBuilder.build();
			return chain.proceed(request);
		}
	}
}
