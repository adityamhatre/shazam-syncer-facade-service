package com.adityamhatre.shazamfacade.Kafka;

import com.adityamhatre.shazamfacade.service.ShazamCaller;
import dto.SongDTO;
import dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.adityamhatre.shazamfacade.Kafka.Topics.TopicConstants.ON_POLL_TIMER_TICK;
import static com.adityamhatre.shazamfacade.Kafka.Topics.TopicConstants.ON_RECEIVE_NEW_USER;

@Component
@Slf4j
public class KafkaConsumer {

	private final ShazamCaller shazamCaller;

	public KafkaConsumer(ShazamCaller shazamCaller) {
		this.shazamCaller = shazamCaller;
	}

	@KafkaListener(topics = ON_RECEIVE_NEW_USER, groupId = "shazam.facade.group1", concurrency = "5", containerFactory = "kafkaListenerContainerFactory")
	void pollUser(ConsumerRecord<String, UserDTO> record) {
		log.info("Got data on channel {}", ON_RECEIVE_NEW_USER);
		shazamCaller.getAllSongsFor(record.value(), null);
	}

	@KafkaListener(topics = ON_POLL_TIMER_TICK, groupId = "shazam.facade.group1", concurrency = "5", containerFactory = "kafkaListenerSongContainerFactory")
	void pollUserNewSong(ConsumerRecord<String, SongDTO> record) {
		log.info("Got data on channel {}", ON_POLL_TIMER_TICK);

		SongDTO songDTO = record.value();
		long getSongsInThisTimeWindow = songDTO.getTimestamp();

		shazamCaller.getNewSongsFor(songDTO, getSongsInThisTimeWindow);
	}

}
