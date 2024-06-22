package br.com.braullio.simplify_transfer_spring.transaction;

import br.com.braullio.simplify_transfer_spring.api.authoration.AuthorizationService;
import br.com.braullio.simplify_transfer_spring.api.notification.NotificationService;
import br.com.braullio.simplify_transfer_spring.exception.BadRequestException;
import br.com.braullio.simplify_transfer_spring.exception.UserNotFoundException;
import br.com.braullio.simplify_transfer_spring.transaction.request.TransactionRequest;
import br.com.braullio.simplify_transfer_spring.user.Payee;
import br.com.braullio.simplify_transfer_spring.user.Payer;
import br.com.braullio.simplify_transfer_spring.user.User;
import br.com.braullio.simplify_transfer_spring.user.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.math.BigDecimal;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
	private static BigDecimal value000, value100, value500;

	@Mock
	private UserRepository userRepository;
	@Mock
	private TransactionRepository transactionRepository;
	@Mock
	private AuthorizationService authorizationService;
	@Mock
	private NotificationService notificationService;

	private AutoCloseable autoCloseable;

	private final Validator validator;
	private final ValidatorFactory factory;

	@InjectMocks
	private TransactionService underTest;

	public TransactionServiceTest(){
		factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}


	@BeforeAll
	public static void setUpValues() {
		value000 = new BigDecimal("0");
		value100 = new BigDecimal("100");
		value500 = new BigDecimal("500");
	}

	@BeforeEach
	public void setUp() {
		autoCloseable = MockitoAnnotations.openMocks(this);
		underTest = new TransactionService(userRepository, transactionRepository, authorizationService, notificationService);

		value000 = new BigDecimal("0");
		value100 = new BigDecimal("100");
		value500 = new BigDecimal("500");
	}

	@AfterEach
	public void tearDown() throws Exception {
		autoCloseable.close();
	}

	@Test
	@DisplayName("Nao pode faltar informacoes na request - valueIsNull")
	public void requestBodyIncompleteValueIsNull() {
		TransactionRequest request = new TransactionRequest(null, 1L, 2L);

		Set<ConstraintViolation<TransactionRequest>> violations = validator.validate(request);

		Assertions.assertEquals(1, violations.size());
		ConstraintViolation<TransactionRequest> violation = violations.iterator().next();
		Assertions.assertEquals("Value cannot be null", violation.getMessage());
	}

	@Test
	@DisplayName("Nao pode faltar informacoes na request - payerIsNull")
	public void requestBodyIncompletePayerIsNull() {
		TransactionRequest request = new TransactionRequest(value100, null, 2L);

		Set<ConstraintViolation<TransactionRequest>> violations = validator.validate(request);

		Assertions.assertEquals(1, violations.size());
		ConstraintViolation<TransactionRequest> violation = violations.iterator().next();
		Assertions.assertEquals("Payer cannot be null", violation.getMessage());
	}

	@Test
	@DisplayName("Nao pode faltar informacoes na request - PayeeIsNull")
	public void requestBodyIncompletePayeeIsNull() {
		TransactionRequest request = new TransactionRequest(value100, 1L, null);

		Set<ConstraintViolation<TransactionRequest>> violations = validator.validate(request);

		Assertions.assertEquals(1, violations.size());
		ConstraintViolation<TransactionRequest> violation = violations.iterator().next();
		Assertions.assertEquals("Payee cannot be null", violation.getMessage());
	}

	@Test
	@DisplayName("Usuario pode enviar dinheiro para outro Usuario - Payer -> Payer")
	public void payerCanPayPayer() throws Exception {
		Payer payer01 = createPayer(value100, '1');
		Payer payer02 = createPayer(value000, '2');

		when(userRepository.findPayerById(payer01.getId())).thenReturn(payer01);
		when(userRepository.findPayeeById(payer02.getId())).thenReturn(payer02);
		when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
			Transaction transaction = invocation.getArgument(0);
			transaction.setId(1L);
			return transaction;
		});

		doNothing().when(authorizationService).call(any(Transaction.class));
		doNothing().when(notificationService).notify(any(TransactionRequest.class));

		TransactionRequest dto = new TransactionRequest(value100, payer01.getId(), payer02.getId());
		Transaction transaction = underTest.create(dto);

		verify(transactionRepository).save(any(Transaction.class));

		Assertions.assertEquals(payer01.getBalance(), value000);
		Assertions.assertEquals(payer02.getBalance(), value100);
		Assertions.assertNotNull(transaction.getId());
	}


	@Test
	@DisplayName("Usuario pode enviar dinheiro para Lojista - Payer -> Payee")
	public void payerCanPayPayee() throws Exception {
		Payer payer = createPayer(value100, '1');
		Payee payee = createPayee(value000, '2');

		when(userRepository.findPayerById(payer.getId())).thenReturn(payer);
		when(userRepository.findPayeeById(payee.getId())).thenReturn(payee);
		when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
			Transaction transaction = invocation.getArgument(0);
			transaction.setId(1L);
			return transaction;
		});

		doNothing().when(authorizationService).call(any(Transaction.class));
		doNothing().when(notificationService).notify(any(TransactionRequest.class));

		TransactionRequest dto = new TransactionRequest(value100, payer.getId(), payee.getId());
		Transaction transaction = underTest.create(dto);

		verify(transactionRepository).save(any(Transaction.class));

		Assertions.assertEquals(payer.getBalance(), value000);
		Assertions.assertEquals(payee.getBalance(), value100);
		Assertions.assertNotNull(transaction.getId());
	}

	@Test
	@DisplayName("Valor nao pode ser zerado")
	public void valueIsZero() throws Exception {
		Payer payer = createPayer(value100, '1');
		Payee payee = createPayee(value000, '2');

		var exceptionBase = new BadRequestException("Value cannot be zero");

		TransactionRequest dto = new TransactionRequest(value000, payer.getId(), payee.getId());
		TransactionService underTestSpy = Mockito.spy(underTest);
		doThrow(exceptionBase).when(underTestSpy).create(dto);
		Exception exception = Assertions.assertThrows(BadRequestException.class, () -> {
			underTestSpy.create(dto);
		});

		Assertions.assertEquals(exceptionBase.getMessage(), exception.getMessage());
	}


	@Test
	@DisplayName("Usuario nao pode enviar dinheiro para ele mesmo - Payer(1) -> Payer(1)")
	public void payerCannotPayThemselves() throws Exception {
		Payer payer = createPayer(value100, '1');

		var exceptionBase = new BadRequestException("Transaction not allowed for the same payer");

		TransactionRequest dto = new TransactionRequest(value100, payer.getId(), payer.getId());
		TransactionService underTestSpy = Mockito.spy(underTest);
		doThrow(exceptionBase).when(underTestSpy).create(dto);
		Exception exception = Assertions.assertThrows(BadRequestException.class, () -> {
			underTestSpy.create(dto);
		});

		Assertions.assertEquals(exceptionBase.getMessage(), exception.getMessage());
	}

	@Test
	@DisplayName("Lojista nao pode enviar dinheiro para Usuario - Payee -> Payer")
	public void payeeCannotPayPayer() throws Exception {
		Payee payee = createPayee(value100, '1');
		Payer payer = createPayer(value100, '2');

		var exceptionBase = new UserNotFoundException("Payer not found or type is 'LOJISTA'");

		TransactionRequest dto = new TransactionRequest(value100, payee.getId(), payer.getId());
		TransactionService underTestSpy = Mockito.spy(underTest);
		doThrow(exceptionBase).when(underTestSpy).create(dto);
		Exception exception = Assertions.assertThrows(UserNotFoundException.class, () -> {
			underTestSpy.create(dto);
		});

		Assertions.assertEquals(exceptionBase.getMessage(), exception.getMessage());
	}

	@Test
	@DisplayName("Lojista nao pode enviar dinheiro para Lojista - Payee -> Payee")
	public void payeeCannotPayPayee() throws Exception {
		Payee payee01 = createPayee(value100, '1');
		Payee payee02 = createPayee(value000, '2');

		var exceptionBase = new UserNotFoundException("Payer not found or type is 'LOJISTA'");

		TransactionRequest dto = new TransactionRequest(value100, payee01.getId(), payee02.getId());
		TransactionService underTestSpy = Mockito.spy(underTest);
		doThrow(exceptionBase).when(underTestSpy).create(dto);
		Exception exception = Assertions.assertThrows(UserNotFoundException.class, () -> {
			underTestSpy.create(dto);
		});

		Assertions.assertEquals(exceptionBase.getMessage(), exception.getMessage());
	}

	@Test
	@DisplayName("Usuario nao pode enviar dinheiro se nao tiver saldo - Payer -> Payee")
	public void payerCannotPayIfNotHaveBalance() throws Exception {
		Payer payer = createPayer(value100, '1');
		Payee payee = createPayee(value000, '2');

		var exceptionBase = new BadRequestException("Insufficient balance in Payer");

		TransactionRequest dto = new TransactionRequest(value500, payer.getId(), payee.getId());
		TransactionService underTestSpy = Mockito.spy(underTest);
		doThrow(exceptionBase).when(underTestSpy).create(dto);
		Exception exception = Assertions.assertThrows(BadRequestException.class, () -> {
			underTestSpy.create(dto);
		});

		Assertions.assertEquals(exceptionBase.getMessage(), exception.getMessage());
	}

	private Payer createPayer(BigDecimal balance, char index) {
		Payer payer = new Payer(
				"Usuario Comum " + index,
				"usuario.comum" + index + "@example.com",
				"1234567890" + index,
				"senhaHash",
				"chavePrivada",
				balance
		);
		payer.setId((long) index);
		return payer;
	}

	private Payee createPayee(BigDecimal balance, char index) {
		Payee payee = new Payee(
				"Usuario Lojista " + index,
				"usuario.lojista" + index + "@example.com",
				"1234567800019" + index,
				"1a00e1a179373aa9b13d3e6765b809ec91a4d0cddf406214a144516466125015",
				"9b1f560dec75c06882073667ed0d760691fc4b03a0ad81415ec7315dbef82662",
				balance
		);
		payee.setId((long) index);
		return payee;
	}

}
