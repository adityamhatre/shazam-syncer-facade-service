package com.adityamhatre.shazamfacade.service;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Component
public class ShazamProvider {
	@Bean
	public Shazam getShazam() {
		return new Retrofit.Builder()
				.baseUrl("https://www.shazam.com/")
				.addConverterFactory(JacksonConverterFactory.create())
				.build()
				.create(Shazam.class);
	}
}
