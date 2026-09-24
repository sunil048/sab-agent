package dev.danvega.dvaas;

import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class DvaasClientApplication {

	@Value("${spring.ai.ollama.base-url:http://localhost:11434}")
	private String baseUrl;

	public static void main(String[] args) {
		SpringApplication.run(DvaasClientApplication.class, args);
	}

	@Bean
	public OllamaApi ollamaApi(RestClient.Builder restClientBuilder,
							   WebClient.Builder webClientBuilder) {
		return OllamaApi.builder()
				.baseUrl(baseUrl)
				.restClientBuilder(restClientBuilder)
				.webClientBuilder(webClientBuilder)
				.build();
	}

}
