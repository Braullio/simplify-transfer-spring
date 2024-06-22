package br.com.braullio.simplify_transfer_spring.transaction;

import br.com.braullio.simplify_transfer_spring.transaction.request.TransactionRequest;
import br.com.braullio.simplify_transfer_spring.transaction.response.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transfer")
public class TransactionController {
	private final TransactionService transactionService;

	public TransactionController(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<TransactionResponse> createPost(@Valid @RequestBody TransactionRequest transactionRequest) throws Exception {
		Transaction transaction = transactionService.create(transactionRequest);
		TransactionResponse response = new TransactionResponse(transaction);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
