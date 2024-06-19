package br.com.braullio.simplify_transfer_spring.transaction;

import br.com.braullio.simplify_transfer_spring.api.authoration.AuthorizationService;
import br.com.braullio.simplify_transfer_spring.api.notification.NotificationService;
import br.com.braullio.simplify_transfer_spring.user.Payer;
import br.com.braullio.simplify_transfer_spring.user.User;
import br.com.braullio.simplify_transfer_spring.user.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionService {
	private final UserRepository userRepository;
	private final TransactionRepository transactionRepository;
	private final AuthorizationService authorizationService;
	private final NotificationService notificationService;

	private final static Logger LOGGER = LoggerFactory.getLogger(TransactionService.class);

	public TransactionService(UserRepository userRepository, TransactionRepository transactionRepository, AuthorizationService authorizationService, NotificationService notificationService) {
		this.userRepository = userRepository;
		this.transactionRepository = transactionRepository;
		this.authorizationService = authorizationService;
		this.notificationService = notificationService;
	}

	@Transactional
	public Transaction create(TransactionDTO transactionDTO) throws Exception {
		if (transactionDTO.value().compareTo(BigDecimal.ZERO) == 0.0) {
			throw new Exception("Value cannot be zero");
		}

		BigDecimal amount = transactionDTO.value();

		Payer payer = findPayerById(transactionDTO.payer());
		User payee = findPayeeById(transactionDTO.payee());

		Transaction transaction = new Transaction(amount, payer, payee);

		payer.debit(transaction.getAmount());
		payee.credit(transaction.getAmount());

		userRepository.save(payer);
		userRepository.save(payee);
		transactionRepository.save(transaction);

		// auth
		authorizationService.call(transaction);

		// notify kafka topic
		notificationService.call(transaction);

		LOGGER.info("transaction executed: {}", transaction);

		return transaction;
	}

	private Payer findPayerById(Long payerId) throws Exception {
		Payer payer = userRepository.findPayerById(payerId);
		if (payer == null) {
			throw new Exception("Payer not found or not allowed");
		}
		return payer;
	}

	private User findPayeeById(Long payeeId) throws Exception {
		User payee = userRepository.findPayeeById(payeeId);
		if (payee == null) {
			throw new Exception("Payee not found or not allowed");
		}
		return payee;
	}
}
