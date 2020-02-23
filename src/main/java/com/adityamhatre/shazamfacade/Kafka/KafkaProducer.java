package com.adityamhatre.shazamfacade.Kafka;

import com.adityamhatre.shazamfacade.dto.Tag;
import dto.SongDTO;
import dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.adityamhatre.shazamfacade.Kafka.Topics.TopicConstants.ON_RECEIVE_NEW_SONG;
import static com.adityamhatre.shazamfacade.Kafka.Topics.TopicConstants.ON_USER_FETCHED_ALL_SONGS;
import static com.adityamhatre.shazamfacade.Kafka.Topics.TopicConstants.ON_USER_FETCHED_NEW_SONGS;

@Service
@Slf4j
public class KafkaProducer {
	private final KafkaTemplate<String, SongDTO> kafkaTemplate;

	public KafkaProducer(KafkaTemplate<String, SongDTO> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void produceOnSongReceived(UserDTO userDTO, Tag song) {
		SongDTO songDTO = convertToSongDTO(userDTO, song);
		log.info("Producing data on channel {} with song {} shazamed by user {}", ON_RECEIVE_NEW_SONG, songDTO.getSongName(), userDTO.getUsername());
		this.kafkaTemplate.send(ON_RECEIVE_NEW_SONG, songDTO);
	}

	public void produceOnAllSongsFetched(UserDTO userDTO, Tag song) {
		SongDTO songDTO = convertToSongDTO(userDTO, song);
		log.info("Producing data on channel {} with latest song {} shazamed by user {}", ON_USER_FETCHED_ALL_SONGS, songDTO.getSongName(), userDTO.getUsername());
		this.kafkaTemplate.send(ON_USER_FETCHED_ALL_SONGS, songDTO);
	}


	public void produceOnNewSongsFetched(UserDTO userDTO, Tag song) {
		SongDTO songDTO = convertToSongDTO(userDTO, song);
		log.info("Producing data on channel {} with latest song {} shazamed by user {}", ON_USER_FETCHED_NEW_SONGS, songDTO.getSongName(), userDTO.getUsername());
		this.kafkaTemplate.send(ON_USER_FETCHED_NEW_SONGS, songDTO);
	}

	private SongDTO convertToSongDTO(UserDTO userDTO, Tag song) {
		return SongDTO.builder()
				.shazamSongId(song.getTrack().getKey())
				.songName(String.format("%s - %s", song.getTrack().getHeading().getTitle(), song.getTrack().getHeading().getSubtitle()))
				.shazamedBy(userDTO)
				.timestamp(song.getTimestamp())
				.build();
	}

	public void produceOnNoNewSongsFetched(UserDTO userDTO, SongDTO songDTO) {
		log.info("Producing data on channel {} with latest song {} shazamed by user {}", ON_USER_FETCHED_NEW_SONGS, songDTO.getSongName(), userDTO.getUsername());
		this.kafkaTemplate.send(ON_USER_FETCHED_NEW_SONGS, songDTO);
	}
}
