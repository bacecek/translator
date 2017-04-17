package com.bacecek.translate.di.module;

import com.bacecek.translate.BuildConfig;
import com.bacecek.translate.data.entity.DictionaryItem;
import com.bacecek.translate.data.entity.Language;
import com.bacecek.translate.data.entity.Translation;
import com.bacecek.translate.data.network.RxErrorHandlingCallAdapterFactory;
import com.bacecek.translate.data.network.deserializer.DictionaryDeserializer;
import com.bacecek.translate.data.network.deserializer.LangsDeserializer;
import com.bacecek.translate.data.network.deserializer.TranslateDeserializer;
import com.bacecek.translate.util.Consts.DI;
import com.bacecek.translate.data.network.CustomInterceptor;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dagger.Module;
import dagger.Provides;
import java.util.List;
import javax.inject.Named;
import javax.inject.Singleton;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Denis Buzmakov on 10/04/2017.
 * <buzmakov.da@gmail.com>
 */

@Module
public class NetworkModule {

	@Provides
	@Singleton
	LangsDeserializer provideLangsDeserializer() {
		return new LangsDeserializer();
	}

	@Provides
	@Singleton
	TranslateDeserializer provideTranslateDeserializer() {
		return new TranslateDeserializer();
	}

	@Provides
	@Singleton
	DictionaryDeserializer provideDictionaryDeserializer() {
		return new DictionaryDeserializer();
	}

	@Provides
	@Singleton
	Gson provideGson(LangsDeserializer langsDeserializer,
			TranslateDeserializer translateDeserializer,
			DictionaryDeserializer dictionaryDeserializer) {
		return new GsonBuilder()
				.registerTypeAdapter(new TypeToken<List<Language>>(){}.getType(), langsDeserializer)
				.registerTypeAdapter(new TypeToken<Translation>(){}.getType(), translateDeserializer)
				.registerTypeAdapter(new TypeToken<List<DictionaryItem>>(){}.getType(), dictionaryDeserializer)
				.create();
	}

	@Provides
	@Singleton
	@Named(DI.DI_TRANSLATOR_INTERCEPTOR)
	CustomInterceptor provideTranslatorInterceptor() {
		return new CustomInterceptor(BuildConfig.YANDEX_TRANSLATE_API_KEY);
	}

	@Provides
	@Singleton
	@Named(DI.DI_DICTIONARY_INTERCEPTOR)
	CustomInterceptor provideDictoonaryInterceptor() {
		return new CustomInterceptor(BuildConfig.YANDEX_DICTIONARY_API_KEY);
	}

	@Provides
	@Singleton
	@Named(DI.DI_TRANSLATOR_OKHTTP)
	OkHttpClient provideTranslatorOkHttpClient(@Named(DI.DI_TRANSLATOR_INTERCEPTOR) CustomInterceptor interceptor) {
		return new OkHttpClient.Builder()
				.addInterceptor(interceptor)
				.addNetworkInterceptor(new StethoInterceptor())
				.build();
	}

	@Provides
	@Singleton
	@Named(DI.DI_DICTIONARY_OKHTTP)
	OkHttpClient provideDictionaryOkHttpClient(@Named(DI.DI_DICTIONARY_INTERCEPTOR) CustomInterceptor interceptor) {
		return new OkHttpClient.Builder()
				.addInterceptor(interceptor)
				.addNetworkInterceptor(new StethoInterceptor())
				.build();
	}

	@Provides
	@Singleton
	GsonConverterFactory provideGsonConverterFactory(Gson gson) {
		return GsonConverterFactory.create(gson);
	}

	@Provides
	@Singleton
	@Named(DI.DI_TRANSLATOR_RETROFIT)
	Retrofit provideTranslatorRetrofit(@Named(DI.DI_TRANSLATOR_OKHTTP) OkHttpClient client, GsonConverterFactory factory) {
		return new Builder()
				.baseUrl(BuildConfig.YANDEX_TRANSLATE_API_URL)
				.addConverterFactory(factory)
				.addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
				.client(client)
				.build();
	}

	@Provides
	@Singleton
	@Named(DI.DI_DICTIONARY_RETROFIT)
	Retrofit provideDictionaryRetrofit(@Named(DI.DI_DICTIONARY_OKHTTP) OkHttpClient client, GsonConverterFactory factory) {
		return new Builder()
				.baseUrl(BuildConfig.YANDEX_DICTIONARY_API_URL)
				.addConverterFactory(factory)
				.addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
				.client(client)
				.build();
	}
}
