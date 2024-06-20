package br.com.braullio.simplify_transfer_spring.api.authoration;

import br.com.braullio.simplify_transfer_spring.api.authoration.response.AuthorizationResponse;
import br.com.braullio.simplify_transfer_spring.exception.UnauthorizedException;
import br.com.braullio.simplify_transfer_spring.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AuthorizationService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationService.class);
	private final WebClient.Builder webClientBuilder;

	@Value("${simplify_transaction.auth.url}")
	private String URL_AUTH;

	public AuthorizationService(WebClient.Builder webClientBuilder) {
		this.webClientBuilder = webClientBuilder;
	}

	public void call(Transaction transaction) {
		String uri = buildAuthorizationUri(transaction);
		WebClient webClient = webClientBuilder.baseUrl(uri).build();

		Mono<ClientResponse> responseMono = webClient.get().exchange();
		ClientResponse clientResponse = responseMono.block();

		if (clientResponse != null) {
			HttpStatusCode statusCode = clientResponse.statusCode();
			AuthorizationResponse responseBody = clientResponse.bodyToMono(AuthorizationResponse.class).block();

			if (statusCode == HttpStatus.OK) {
				if (responseBody != null && responseBody.getData().isAuthorization()) {
					LOGGER.info("Authorization successful for responseBody: {}", responseBody);
					return;
				}

				LOGGER.info("Unauthorized for transaction: {}, status code: {}, response body: {}", transaction, statusCode, responseBody);
				throw new UnauthorizedException("Unauthorized");
			}

			if (statusCode == HttpStatus.FORBIDDEN) {
				LOGGER.warn("Unauthorized for transaction: {}, status code: {}, response body: {}", transaction, statusCode, responseBody);
				throw new UnauthorizedException("Unauthorized");
			}

			LOGGER.error("StatusCode not trated Authorization for transaction: {}, status code: {}, response body: {}", transaction, statusCode, responseBody);
			throw new UnauthorizedException("Unauthorized");
		}
	}

	private String buildAuthorizationUri(Transaction transaction) {
		String uriString = URL_AUTH + "?amount=" + transaction.getAmount().doubleValue()
				+ "&payerId=" + transaction.getPayer().getId()
				+ "&payeeId=" + transaction.getPayee().getId();

		return uriString;
	}
}