package br.com.braullio.simplify_transfer_spring.kafka;

import br.com.braullio.simplify_transfer_spring.transaction.TransactionDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
	private final KafkaTemplate<String, TransactionDTO> kafkaTemplate;

	@Value("${simplify_transaction.kafka.notification}")
	private String topic;

	public KafkaProducerService(KafkaTemplate<String, TransactionDTO> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void sendNotification(TransactionDTO transactionDTO) {
		kafkaTemplate.send(topic, transactionDTO);
	}
}
