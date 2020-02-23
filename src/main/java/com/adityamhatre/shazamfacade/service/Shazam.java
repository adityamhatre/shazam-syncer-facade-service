package com.adityamhatre.shazamfacade.service;

import com.adityamhatre.shazamfacade.dto.ShazamResponse;
import org.springframework.lang.Nullable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Shazam {
	@GET("discovery/v4/en-US/US/web/-/tag/{inid}")
	Call<ShazamResponse> getSongsForUser(@Path("inid") String inid, @Header("cookie") String codever, @Nullable @Query("limit") int limit, @Nullable @Query("token") String token);
}
