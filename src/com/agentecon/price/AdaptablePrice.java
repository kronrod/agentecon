// Created by Luzius on May 10, 2014

package com.agentecon.price;

import com.agentecon.stats.Numbers;

public abstract class AdaptablePrice implements IPrice {

	public static final double MIN = 0.000001;
	public static final double MAX = 100000;

	private double price;
	
	public AdaptablePrice() {
		this(10.0);
	}

	public AdaptablePrice(double initial) {
		this.price = initial;
	}
	
	public void adapt(boolean increase) {
		double factor = getFactor(increase);
		if (increase) {
			price = Math.min(MAX, price * factor);
		} else {
			price = Math.max(MIN, price / factor);
		}
	}
	
	@Override
	public void adaptWithCeiling(boolean increase, double max) {
		if (price < max || !increase){
			adapt(increase);
			if (price > max){
				price = max;
			}
		}
	}

	@Override
	public void adaptWithFloor(boolean increase, double min) {
		if (price > min || increase){
			adapt(increase);
			if (price < min){
				price = min;
			}
		}
	}
	
	public void adapt(double towards, double weight){
		this.price = price * (1-weight) + towards * weight;
	}

	protected abstract double getFactor(boolean increase);
	
	public double getSensorDelta(){
		return getFactor(true) - 1.0;
	}

	public double getPrice() {
		return price;
	}
	
	public boolean isProbablyUnobtainable(){
		return price >= MAX;
	}
	
	@Override
	public IPrice clone(){
		try {
			return (IPrice) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new java.lang.RuntimeException(e);
		}
	}
	
	@Override
	public String toString() {
		return Numbers.toString(price) + "$";
	}

}
