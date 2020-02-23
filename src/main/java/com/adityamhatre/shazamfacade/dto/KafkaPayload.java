package com.adityamhatre.shazamfacade.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaPayload {
	String userObjectId;
	Tag song;
}
