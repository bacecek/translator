package com.bacecek.translate.data.network;

import com.bacecek.translate.data.entities.DictionaryItem;
import io.reactivex.Observable;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Denis Buzmakov on 20/03/2017.
 * <buzmakov.da@gmail.com>
 */

public interface DictionaryAPI {
	@GET("lookup")
	Observable<List<DictionaryItem>> translate(
			@Query("text") String text,
			@Query("lang") String langs,
			@Query("ui") String ui
	);
}
