package com.whippy.sponge.guard.exceptions;

public class MultipleWorldInAreaException extends Exception {

	
	public MultipleWorldInAreaException(){
		super("An area can not span multiple worlds");
	}
}
