package br.com.braullio.simplify_transfer_spring.exception.errorResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
	private int status;
	private String message;
	private Instant timestamp;

	public String toJson() {
		return "{" +
				"\"status\":" + status + "," +
				"\"message\":\"" + message + "\"," +
				"\"timestamp\":\"" + timestamp.toString() + "\"," +
				"}";
	}
}
