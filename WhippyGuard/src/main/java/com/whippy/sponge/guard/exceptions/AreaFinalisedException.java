package com.whippy.sponge.guard.exceptions;

public class AreaFinalisedException extends Exception {

	public AreaFinalisedException(String message) {
		super(message);
	}

	public AreaFinalisedException() {
		super("Area has already been completly defined");
	}

}
