package br.com.braullio.simplify_transfer_spring.transaction;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transfer")
public class TransactionController {
	private final TransactionService transactionService;

	public TransactionController(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	@PostMapping
	public Transaction createPost(@Valid @RequestBody TransactionDTO transactionDTO) throws Exception {
		return transactionService.create(transactionDTO);
	}
}
