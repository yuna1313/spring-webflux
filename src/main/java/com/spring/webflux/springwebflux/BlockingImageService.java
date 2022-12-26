package com.spring.webflux.springwebflux;

import java.time.Duration;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.codec.multipart.FilePart;

import reactor.core.publisher.Flux;

public class BlockingImageService {

	private final ImageService imageService;
	
	public BlockingImageService(ImageService imageService) {
		this.imageService = imageService;
	}
	
	public List<Image> findAllImages() {
		return imageService.findAllImages()
				.collectList()
				.block(Duration.ofSeconds(10));
	}
	
	public Resource findOneImage(String filename) {
		return imageService.findOneImage(filename)
				.block(Duration.ofSeconds(30));
	}
	
	public void createImage(List<FilePart> files) {
		imageService.createImage(Flux.fromIterable(files))
		.block(Duration.ofSeconds(1));
	}
	
	public void deleteImage(String filename) {
		imageService.deleteImage(filename)
		.block(Duration.ofSeconds(5));
	}
}
