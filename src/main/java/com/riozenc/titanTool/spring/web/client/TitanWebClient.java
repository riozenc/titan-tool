/**
 * Author : chizf
 * Date : 2020年8月14日 下午4:05:50
 * Title : com.riozenc.titanTool.spring.web.client.TitanWebClient.java
 *
**/
package com.riozenc.titanTool.spring.web.client;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

public class TitanWebClient {

	private WebClient.Builder webClientBuilder;

	public TitanWebClient(WebClient.Builder webClientBuilder) {
		this.webClientBuilder = webClientBuilder;
	}

	public Mono<String> postReturnMonoString(String uri, Object body) {
		return webClientBuilder.build().post().uri(uri).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).bodyValue(body).retrieve().bodyToMono(String.class);
	}

}
