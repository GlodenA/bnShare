package com.ipu.server.kafka.exception;

public class OffsetException extends Exception {

	private static final long serialVersionUID = 1L;

	public OffsetException (String msg){
		super(msg);
	}
}
