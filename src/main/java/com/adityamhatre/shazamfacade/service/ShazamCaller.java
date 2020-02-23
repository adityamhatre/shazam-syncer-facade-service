package com.adityamhatre.shazamfacade.service;

import com.adityamhatre.shazamfacade.Kafka.KafkaProducer;
import com.adityamhatre.shazamfacade.dto.ShazamResponse;
import com.adityamhatre.shazamfacade.dto.Tag;
import dto.SongDTO;
import dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import retrofit2.Response;


@Component
@Slf4j
public class ShazamCaller {
	private final Shazam shazam;
	private final KafkaProducer kafkaProducer;

	public ShazamCaller(Shazam shazam, KafkaProducer kafkaProducer) {
		this.shazam = shazam;
		this.kafkaProducer = kafkaProducer;
	}

	public int getAllSongsFor(UserDTO userDTO, @Nullable Long upto) {
		try {
			String token = null;
			Tag firstTag = null;
			int limit = 20;

			boolean breakout = false;
			String cookie = "codever=" + userDTO.getCodever();
			String inid = userDTO.getInid();

			do {
				Response<ShazamResponse> response = shazam.getSongsForUser(
						inid,
						cookie,
						limit,
						token
				).execute();

				if (response.isSuccessful()) {
					if (response.body() != null) {

						ShazamResponse shazamResponse = response.body();

						for (int i = 0; i < shazamResponse.getTags().size(); i++) {
							Tag tag = shazamResponse.getTags().get(i);

							if (upto != null && tag.getTimestamp() <= upto) {
								//if inside here, it means that we are checking for ONLY NEW songs of this user "AND" have found all the new songs as decided by the timestamp and "upto" param
								breakout = true;
								break;
							}

							if (firstTag == null) {
								//save the first song
								firstTag = tag;
							}

							this.kafkaProducer.produceOnSongReceived(userDTO, tag);
						}

						if (breakout) {
							break;
						}

						token = shazamResponse.getToken();
						if (token == null) {
							break;
						}


					} else {
						break;
					}
				} else {
					break;
				}
			} while (true);


			//now "firstTag" contains the first song fetched that has the latest timestamp thanks to shazam sending me data in desc order ALWAYS
			//sending the newest "leading" song over kafka which contains the user object as well
			if (firstTag != null) {
				if (userDTO.isBootStrapped()) {
					this.kafkaProducer.produceOnNewSongsFetched(userDTO, firstTag);
				} else {
					userDTO.setBootStrapped(true);
					this.kafkaProducer.produceOnAllSongsFetched(userDTO, firstTag);
				}
				return 0;
			} else {
				log.warn("No new songs found for the user {}", userDTO.getObjectId());
				return -1;
			}


		} catch (Exception e) {
			e.printStackTrace();
			return -2;
		}
	}

	public void getNewSongsFor(SongDTO songDTO, long getSongsInThisTimeWindow) {
		int result = getAllSongsFor(songDTO.getShazamedBy(), getSongsInThisTimeWindow);
		if (result == -1) {
			this.kafkaProducer.produceOnNoNewSongsFetched(songDTO.getShazamedBy(), songDTO);
		}
	}
}
