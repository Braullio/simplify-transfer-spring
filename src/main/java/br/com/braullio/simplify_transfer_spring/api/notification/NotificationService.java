package br.com.braullio.simplify_transfer_spring.api.notification;

import br.com.braullio.simplify_transfer_spring.api.notification.request.NotificationRequest;
import br.com.braullio.simplify_transfer_spring.api.notification.response.NotificationResponse;
import br.com.braullio.simplify_transfer_spring.exception.NotificationException;
import br.com.braullio.simplify_transfer_spring.kafka.KafkaProducerService;
import br.com.braullio.simplify_transfer_spring.transaction.request.TransactionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class NotificationService {
	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);
	private final WebClient.Builder webClientBuilder;
	private final KafkaProducerService kafkaProducerService;

	@Value("${simplify_transaction.notification.url}")
	private String URL_NOTIFY;

	public NotificationService(WebClient.Builder webClientBuilder, KafkaProducerService kafkaProducer) {
		this.webClientBuilder = webClientBuilder;
		this.kafkaProducerService = kafkaProducer;
	}

	public void notify(TransactionRequest transactionRequest) {
		kafkaProducerService.sendNotification(transactionRequest);
	}

	public void call(TransactionRequest transactionRequest) {
		WebClient webClient = webClientBuilder.baseUrl(URL_NOTIFY).build();

		NotificationRequest request = new NotificationRequest(
				transactionRequest.value(),
				transactionRequest.payer(),
				transactionRequest.payee()
		);

		Mono<ClientResponse> responseMono = webClient.post()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.bodyValue(request)
				.exchange();

		ClientResponse clientResponse = responseMono.block();

		if (clientResponse != null) {
			HttpStatusCode statusCode = clientResponse.statusCode();

			if (statusCode == HttpStatus.GATEWAY_TIMEOUT) {
				NotificationResponse responseBody = clientResponse.bodyToMono(NotificationResponse.class).block();
				LOGGER.warn("Notification failed for transaction: {}, status code: {}, response body: {}", transactionRequest, statusCode, responseBody);
				throw new NotificationException("Notification failed");
			}

			if (statusCode == HttpStatus.NO_CONTENT) {
				LOGGER.info("Notification successful for transaction: {}", transactionRequest);
				return;
			}

			String responseBody = clientResponse.bodyToMono(String.class).block();
			LOGGER.warn("Notification failed for transaction: {} - Status code: {} - Response body: {}", transactionRequest, statusCode, responseBody);
			throw new NotificationException("Notification failed - Unexpected status code: " + statusCode);
		}

		throw new NotificationException("Error during Notification");
	}
}