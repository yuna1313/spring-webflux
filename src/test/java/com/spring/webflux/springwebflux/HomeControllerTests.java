package com.spring.webflux.springwebflux;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@WebFluxTest(controllers = HomeController.class)
@Import({ThymeleafAutoConfiguration.class})
public class HomeControllerTests {
	
	@Autowired WebTestClient webClient;
	@MockBean ImageService imageService;
	
	@Test
	// 테스트할 내용을 간단히 메소드명으로 요약
	public void baseRouteShouldListAllImages() {
		// service에서 findAllImages()가 호출되었을 때 return 하는 값 세팅
		Image alphaImage = new Image("1", "alpha.png");
		Image bravoImage = new Image("2", "bravo.png");
		given(imageService.findAllImages()).willReturn(Flux.just(alphaImage, bravoImage));
		
		// HTTP 상태를 200으로 세팅하고 결과의 본문을 문자열로 출력
		EntityExchangeResult<String> result = webClient
				.get().uri("/")
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class).returnResult();
		
		// 모키도의 verify를 이용하여 findAllImages()가 호출되었다는 것을 증명
		verify(imageService).findAllImages();
		// 모키도의 verifyNoMoreInteractions()를 이용하여 다른 호출이 없었다는 것을 증명
		verifyNoMoreInteractions(imageService);
		// 렌더링된 html 파일에서 일치하는 것이 있는지 확인 (유효성 검사)
		assertThat(result.getResponseBody())
		.contains("<title>Learning Spring Boot: Spring-a-Gram</title>")
		.contains("<a href=\"/images/alpha.png/raw\">")
		.contains("<a href=\"/images/bravo.png/raw\">");
	}
	
	@Test
	// 테스트할 내용을 간단히 메소드명으로 요약
	public void fetchingImageShouldWork() {
		// service에서 findOneImage()가 호출되었을 때 return 하는 값 세팅
		given(imageService.findOneImage(any()))
		.willReturn(Mono.just(new ByteArrayResource("data".getBytes())));
		
		// /images/alpha.png/raw URL 호출 후 결과가 세팅한 값과 일치하는지 확인
		webClient
		.get().uri("/images/alpha.png/raw")
		.exchange()
		.expectStatus().isOk()
		.expectBody(String.class).isEqualTo("data");
		
		// 모키도의 verify를 이용하여 findOneImage()가 호출되었다는 것을 증명
		verify(imageService).findOneImage("alpha.png");
		// 모키도의 verifyNoMoreInteractions()를 이용하여 다른 호출이 없었다는 것을 증명
		verifyNoMoreInteractions(imageService);
	}
	
	@Test
	// 테스트할 내용을 간단히 메소드명으로 요약
	public void fetchingNullImageShouldFail() throws IOException {
		// Exception을 실행한다.
		Resource resource = mock(Resource.class);
		given(resource.getInputStream())
		.willThrow(new IOException("Bad file"));
		given(imageService.findOneImage(any()))
		.willReturn(Mono.just(resource));
		
		// URl 요청 후 return 값이 일치하는지 확인
		webClient
		.get().uri("/images/alpha.png/raw")
		.exchange()
		.expectStatus().isBadRequest()
		.expectBody(String.class)
		.isEqualTo("Couldn't find alpha.png => Bad file");
		
		// 모키도의 verify를 이용하여 findOneImage()가 호출되었다는 것을 증명
		verify(imageService).findOneImage("alpha.png");
		// 모키도의 verifyNoMoreInteractions()를 이용하여 다른 호출이 없었다는 것을 증명
		verifyNoMoreInteractions(imageService);
	}
	
	@Test
	public void deleteImageShouldWork() {
		given(imageService.deleteImage(any())).willReturn(Mono.empty());
		
		webClient
		.delete().uri("/images/alpha.png")
		.exchange()
		.expectStatus().isSeeOther()
		.expectHeader().valueEquals(HttpHeaders.LOCATION, "/");
		
		// 모키도의 verify를 이용하여 findOneImage()가 호출되었다는 것을 증명
		verify(imageService).deleteImage("alpha.png");
		// 모키도의 verifyNoMoreInteractions()를 이용하여 다른 호출이 없었다는 것을 증명
		verifyNoMoreInteractions(imageService);
	}
}
