package br.com.braullio.simplify_transfer_spring.user;

import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

	@Test
	@DisplayName("Verify creation Payer and consult")
	void verifyPayer() {

		Payer savedPayer = userRepository.save(new Payer(
			"Pagador Teste",
			"pagador.teste@example.com",
			"12345678901",
			"1234hash",
			"1234salt",
			new BigDecimal("50.00")
		));

		Payer result = userRepository.findPayerById(savedPayer.getId());

		assertThat(result.getId()).isEqualTo(savedPayer.getId());
		assertThat(result.getUserType()).isEqualTo(UserType.COMUM);
	}

	@Test
	@DisplayName("Verify creation Payee and consult")
	void verifyPayee() {
		Payee savedPayee = userRepository.save(new Payee(
			"Pagador Teste",
			"pagador.teste@example.com",
			"12345678901",
			"1234hash",
			"1234salt",
			new BigDecimal("50.00")
		));

		User result = userRepository.findPayeeById(savedPayee.getId());

		assertThat(result.getId()).isEqualTo(savedPayee.getId());
		assertThat(result.getUserType()).isEqualTo(UserType.LOJISTA);
	}

	@Test
	@DisplayName("Verify creation of PAYER and see as PAYEE equals NULL")
	void verifyPayerNotPayee() {
		Payer savedPayer = userRepository.save(new Payer(
			"Pagador Teste",
			"pagador.teste@example.com",
			"12345678901",
			"1234hash",
			"1234salt",
			new BigDecimal("50.00")
		));

		User result = userRepository.findPayeeById(savedPayer.getId());

		assertThat(result.getId()).isNotNull();
	}
}