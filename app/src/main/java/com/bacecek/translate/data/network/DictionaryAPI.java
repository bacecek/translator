package com.bacecek.translate.data.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Denis Buzmakov on 20/03/2017.
 * <buzmakov.da@gmail.com>
 */

public interface DictionaryAPI {
	@GET("lookup")
	Call<String> translate(
			@Query("text") String text,
			@Query("lang") String lang
	);
}
