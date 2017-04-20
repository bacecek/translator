package com.bacecek.translate.data.network.util;

import java.io.IOException;
import java.lang.annotation.Annotation;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Denis Buzmakov on 13/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class RetrofitException extends RuntimeException {
	public static RetrofitException httpError(Response response, Retrofit retrofit) {
		String message = response.code() + " " + response.message();
		return new RetrofitException(message, Kind.HTTP, null, response, retrofit);
	}

	public static RetrofitException networkError(IOException exception) {
		return new RetrofitException(exception.getMessage(), Kind.NETWORK, exception, null, null);
	}

	public static RetrofitException unexpectedError(Throwable exception) {
		return new RetrofitException(exception.getMessage(), Kind.UNEXPECTED, exception, null, null);
	}

	public enum Kind {
		NETWORK,
		HTTP,
		UNEXPECTED
	}

	private final Kind mKind;
	private Response mResponse;
	private Retrofit mRetrofit;

	RetrofitException(String message, Kind kind, Throwable exception, Response response, Retrofit retrofit) {
		super(message, exception);
		mKind = kind;
		mResponse = response;
		mRetrofit = retrofit;
	}

	public Kind getKind() {
		return mKind;
	}

	public <T> T getErrorBodyAs(Class<T> type) throws IOException {
		if (mResponse == null || mResponse.errorBody() == null) {
			return null;
		}
		Converter<ResponseBody, T> converter = mRetrofit.responseBodyConverter(type, new Annotation[0]);
		return converter.convert(mResponse.errorBody());
	}
}
