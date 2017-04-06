package com.bacecek.translate.data.network;

import com.bacecek.translate.data.entities.DictionaryItem;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Denis Buzmakov on 20/03/2017.
 * <buzmakov.da@gmail.com>
 */

public interface DictionaryAPI {
	@GET("lookup")
	Call<List<DictionaryItem>> translate(
			@Query("text") String text,
			@Query("lang") String langs
	);
}
