package br.com.braullio.simplify_transfer_spring.config;

import br.com.braullio.simplify_transfer_spring.exception.BadRequestException;
import br.com.braullio.simplify_transfer_spring.exception.UnauthorizedException;
import br.com.braullio.simplify_transfer_spring.exception.UserNotFoundException;
import br.com.braullio.simplify_transfer_spring.exception.errorResponse.ErrorResponseBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<Object> handleAllBadRequest(Exception ex) {
		String errorMessage = ex.getMessage();
		log.error(errorMessage, ex);

		return ErrorResponseBuilder.builder()
				.status(HttpStatus.BAD_REQUEST)
				.message(errorMessage)
				.build();
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<Object> handleUnauthorized(Exception ex) {
		String errorMessage = ex.getMessage();
		log.error(errorMessage, ex);

		return ErrorResponseBuilder.builder()
				.status(HttpStatus.BAD_REQUEST)
				.message(errorMessage)
				.build();
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<Object> handleUserNotFound(Exception ex) {
		String errorMessage = ex.getMessage();
		log.error(errorMessage, ex);

		return ErrorResponseBuilder.builder()
				.status(HttpStatus.NOT_FOUND)
				.message(errorMessage)
				.build();
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleAllInternalServer(Exception ex) {
		String errorMessage = ex.getMessage();
		log.error(errorMessage, ex);

		return ErrorResponseBuilder.builder()
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.message(errorMessage)
				.build();
	}
}
