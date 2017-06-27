package com.github.backend;

public class PriceMovement {
	
	private String from;
	private String to;
	private double percentChange;
	
	public PriceMovement(String from, String to, double percentChange) {
		this.from = from;
		this.to = to;
		this.percentChange = percentChange;
	}
	
	public String getFrom() {
		return this.from;
	}
	
	public String getTo() {
		return this.to;
	}
	
	public double getPercentChange() {
		return this.percentChange;
	}

}
