package br.com.braullio.simplify_transfer_spring.kafka;

import br.com.braullio.simplify_transfer_spring.transaction.request.TransactionRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
	private final KafkaTemplate<String, TransactionRequest> kafkaTemplate;

	@Value("${simplify_transaction.kafka.notification}")
	private String topic;

	public KafkaProducerService(KafkaTemplate<String, TransactionRequest> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void sendNotification(TransactionRequest transactionRequest) {
		kafkaTemplate.send(topic, transactionRequest);
	}
}
