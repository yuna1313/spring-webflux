package com.spring.webflux.springwebflux;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;

@RunWith(SpringRunner.class)
@WebFluxTest(controllers = HomeController.class)
@Import({ThymeleafAutoConfiguration.class})
public class HomeControllerTests {
	
	@Autowired WebTestClient webClient;
	@MockBean ImageService imageService;
	
	@Test
	// 테스트할 내용을 간단히 메소드명으로 요약
	public void baseRouteShouldListAllImages() {
		// given
		Image alphaImage = new Image("1", "alpha.png");
		Image bravoImage = new Image("2", "bravo.png");
		given(imageService.findAllImages()).willReturn(Flux.just(alphaImage, bravoImage));
		
		// when
		EntityExchangeResult<String> result = webClient
				.get().uri("/")
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class).returnResult();
		
		// then
		verify(imageService).findAllImages();
		verifyNoMoreInteractions(imageService);
		assertThat(result.getResponseBody())
		.contains("<title>Learning Spring Boot: Spring-a-Gram</title>")
		.contains("<a href=\"/images/alpha.png/raw\">")
		.contains("<a href=\"/images/bravo.png/raw\">");
	}
}
