package br.com.braullio.simplify_transfer_spring.exception.errorResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import java.time.Instant;

public class ErrorResponseBuilder {

	private ErrorResponse errorResponse;

	@Value(value="${simplify_transaction.response-error.stackTrace}")
	private boolean printStackTrace;

	public static ErrorResponseBuilder builder() {
		ErrorResponseBuilder instance = new ErrorResponseBuilder();
		instance.errorResponse = new ErrorResponse();
		instance.errorResponse.setTimestamp(Instant.now());

		return instance;
	}

	public ErrorResponseBuilder status(HttpStatusCode httpStatusCode) {
		errorResponse.setStatus(httpStatusCode.value());
		return this;
	}

	public ErrorResponseBuilder message(String message) {
		errorResponse.setMessage(message);
		return this;
	}

	public ResponseEntity<Object> build() {
		return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
	}
}
