package io.spring.examples.sockshop.orders.service.support;

import io.spring.examples.sockshop.orders.service.support.OrderException;

/**
 * An exception that is thrown if the payment is declined.
 */
public class PaymentDeclinedException extends OrderException {
	public PaymentDeclinedException(String s) {
		super(s);
	}
}
