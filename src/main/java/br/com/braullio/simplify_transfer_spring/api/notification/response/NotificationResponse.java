package br.com.braullio.simplify_transfer_spring.api.notification.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NotificationResponse {
	private String status;
	private String message;
}