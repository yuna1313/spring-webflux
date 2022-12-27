package com.spring.webflux.springwebflux;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document
//@NoArgsConstructor
public class Image {
	
	@Id final private String id;
	final private String name;
	
	public Image(String id, String name) {
		this.id = id;
		this.name = name;
	}

}
