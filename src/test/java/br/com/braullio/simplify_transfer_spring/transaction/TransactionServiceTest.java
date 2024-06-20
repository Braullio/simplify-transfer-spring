package br.com.braullio.simplify_transfer_spring.transaction;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import br.com.braullio.simplify_transfer_spring.api.authoration.AuthorizationService;
import br.com.braullio.simplify_transfer_spring.api.notification.NotificationService;
import br.com.braullio.simplify_transfer_spring.exception.BadRequestException;
import br.com.braullio.simplify_transfer_spring.exception.UserNotFoundException;
import br.com.braullio.simplify_transfer_spring.user.Payee;
import br.com.braullio.simplify_transfer_spring.user.Payer;
import br.com.braullio.simplify_transfer_spring.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.boot.test.mock.mockito.MockBean;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private AuthorizationService authorizationService;

	@Mock
	private NotificationService notificationService;

	@MockBean
	private Logger logger;

	@InjectMocks
	private TransactionService transactionService;

	@BeforeEach
	public void setUp() {
		logger = mock(Logger.class);
	}

	@Test
	@DisplayName("01 - Teste de sucesso na criação de uma transação")
	public void createTransaction_Success() throws Exception {
		// Given
		TransactionDTO transactionDTO = new TransactionDTO(BigDecimal.valueOf(100), 1L, 2L);
		Payer payer = new Payer("Payer Full Name", "payer@example.com", "12345678901", "hash", "salt", BigDecimal.valueOf(1000));
		Payee payee = new Payee("Payee Full Name", "payee@example.com", "09876543210", "hash", "salt", BigDecimal.valueOf(500));

		when(userRepository.findPayerById(1L)).thenReturn(payer);
		when(userRepository.findPayeeById(2L)).thenReturn(payee);

		// Mocking debit and credit methods
		doNothing().when(payer).debit(any(BigDecimal.class));
		doNothing().when(payee).credit(any(BigDecimal.class));

		// When
		Transaction transaction = transactionService.create(transactionDTO);

		// Then
		verify(userRepository).save(payer);
		verify(userRepository).save(payee);
		verify(transactionRepository).save(any(Transaction.class));
		verify(authorizationService).call(any(Transaction.class));
		verify(notificationService).notify(transactionDTO);
	}

	@Test
	@DisplayName("02 - Teste de criação de transação com valor zero deve lançar exceção")
	public void valueIsZero() {
		TransactionDTO transactionDTO = new TransactionDTO(BigDecimal.ZERO, 1L, 2L);

		assertThrows(BadRequestException.class, () -> {
			transactionService.create(transactionDTO);
		});
	}

	@Test
	@DisplayName("03 - Teste de criação de transação com pagador não encontrado deve lançar exceção")
	public void payerNotFound() {
		TransactionDTO transactionDTO = new TransactionDTO(BigDecimal.valueOf(100), 1L, 2L);
		when(userRepository.findPayerById(1L)).thenReturn(null);

		assertThrows(UserNotFoundException.class, () -> {
			transactionService.create(transactionDTO);
		});
	}

	@Test
	@DisplayName("04 - Teste de criação de transação com beneficiário não encontrado deve lançar exceção")
	public void payeeNotFound() {
		TransactionDTO transactionDTO = new TransactionDTO(BigDecimal.valueOf(100), 1L, 2L);
		Payer payer = new Payer("Payer Full Name", "payer@example.com", "12345678901", "hash", "salt", BigDecimal.valueOf(1000));
		when(userRepository.findPayerById(1L)).thenReturn(payer);
		when(userRepository.findPayeeById(2L)).thenReturn(null);

		assertThrows(UserNotFoundException.class, () -> {
			transactionService.create(transactionDTO);
		});
	}

	@Test
	@DisplayName("05 - Teste de criação de transação com saldo insuficiente deve lançar exceção")
	public void insufficientBalance() {
		TransactionDTO transactionDTO = new TransactionDTO(BigDecimal.valueOf(1000), 1L, 2L);
		Payer payer = new Payer("Payer Full Name", "payer@example.com", "12345678901", "hash", "salt", BigDecimal.valueOf(100));
		Payee payee = new Payee("Payee Full Name", "payee@example.com", "09876543210", "hash", "salt", BigDecimal.valueOf(500));

		when(userRepository.findPayerById(1L)).thenReturn(payer);
		when(userRepository.findPayeeById(2L)).thenReturn(payee);

		assertThrows(BadRequestException.class, () -> {
			transactionService.create(transactionDTO);
		});
	}
}
