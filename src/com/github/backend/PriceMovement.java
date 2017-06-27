package com.github.backend;

public class PriceMovement {
	
	/*
	 * PriceMovement object exists to ensure that the rows on the "Significant Changes" object
	 * on the JavaFX are able to accurately represents a change in price. 
	 */
	
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
