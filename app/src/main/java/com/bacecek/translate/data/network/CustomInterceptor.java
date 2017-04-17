package com.bacecek.translate.data.network;

import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Denis Buzmakov on 10/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class CustomInterceptor implements Interceptor {
	private String mKey;

	public CustomInterceptor(String key) {
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
