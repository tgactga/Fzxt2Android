package com.fzxt;

public class NoNetWorkException extends Exception{
	
private static final long serialVersionUID = 1L;
	
	public NoNetWorkException(){}
	
	public NoNetWorkException(Exception e){
		super(e);
	}
	
	public NoNetWorkException(String msg){
		super(msg);
	}
}
