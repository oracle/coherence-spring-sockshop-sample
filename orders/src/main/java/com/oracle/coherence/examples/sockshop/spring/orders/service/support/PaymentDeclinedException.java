package com.oracle.coherence.examples.sockshop.spring.orders.service.support;

/**
 * An exception that is thrown if the payment is declined.
 */
public class PaymentDeclinedException extends OrderException {
	public PaymentDeclinedException(String s) {
		super(s);
	}
}
