package com.spring.webflux.springwebflux;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ImageTests {
	@Test
	public void imagesManagedByLombokShouldWork() {
		Image image = new Image("id", "file-name.jpg");
		assertThat(image.getId()).isEqualTo("id");
		assertThat(image.getName()).isEqualTo("file-name.jpg");
	}
	
}
