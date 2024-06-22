package br.com.braullio.simplify_transfer_spring.kafka;

import br.com.braullio.simplify_transfer_spring.api.notification.NotificationService;
import br.com.braullio.simplify_transfer_spring.transaction.request.TransactionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);

	private final NotificationService notificationService;

	public KafkaConsumerService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@KafkaListener(
			topics = "${simplify_transaction.kafka.notification}",
			groupId = "${simplify_transaction.kafka.group_id}")
	@RetryableTopic(
			attempts = "${simplify_transaction.kafka.retry.attempts}",
			backoff = @Backoff(delay = 2000, multiplier = 2),
			autoCreateTopics = "false",
			topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
			dltStrategy = DltStrategy.FAIL_ON_ERROR
	)
	public void receiveNotification(TransactionRequest transactionRequest) {
		LOGGER.info("Notifying transaction {}...", transactionRequest);
		notificationService.call(transactionRequest);
	}
}
