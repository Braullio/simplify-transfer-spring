package br.com.braullio.simplify_transfer_spring.api.authoration.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AuthorizationResponse {
	private String status;
	private AuthorizationDataResponse data;
}
