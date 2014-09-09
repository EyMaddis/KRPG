package de.kaleydra.krpg;

public class InvalidItemException extends Exception {

	private static final long serialVersionUID = 4933721252386142137L;

	public InvalidItemException(String message){
		super(message);
	}
}
