// Created on Jun 3, 2015 by Luzius Meisser

package com.agentecon.price;


public class HardcodedPrice implements IPrice {
	
	private double price;

	public HardcodedPrice(double price) {
		this.price = price;
	}

	@Override
	public double getPrice() {
		return price;
	}

	@Override
	public void adapt(boolean increasePrice) {
	}

	@Override
	public boolean isProbablyUnobtainable() {
		return false;
	}

}