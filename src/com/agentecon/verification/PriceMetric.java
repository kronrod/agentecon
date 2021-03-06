package com.agentecon.verification;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.agentecon.api.IMarket;
import com.agentecon.api.Price;
import com.agentecon.good.Good;
import com.agentecon.metric.IMarketListener;
import com.agentecon.metric.SimulationListenerAdapter;
import com.agentecon.util.AccumulatingAverage;
import com.agentecon.util.Average;
import com.agentecon.util.IAverage;
import com.agentecon.util.InstantiatingHashMap;

public class PriceMetric extends SimulationListenerAdapter implements IMarketListener {

	private HashMap<Good, AccumulatingAverage> prices;
	private HashMap<Good, IAverage> volume;

	private int startRecordingDate;
	private int endRecordingDate;

	public PriceMetric(int startRecordingDate) {
		this(startRecordingDate, Integer.MAX_VALUE);
	}
	
	public PriceMetric(int start, int end){
		this.startRecordingDate = start;
		this.endRecordingDate = end;
		this.prices = new InstantiatingHashMap<Good, AccumulatingAverage>() {

			@Override
			protected AccumulatingAverage create(Good key) {
				AccumulatingAverage wma = new AccumulatingAverage();
				return wma;
			}
		};
		this.volume = new InstantiatingHashMap<Good, IAverage>() {

			@Override
			protected IAverage create(Good key) {
				return new Average();
			}
		};
	}

	@Override
	public void notifyOffered(Good good, double quantity, Price price) {
	}

	@Override
	public void notifySold(Good good, double quantity, Price price) {
		this.prices.get(good).add(quantity, price.getPrice());
	}

	@Override
	public void notifyTradesCancelled() {
		for (AccumulatingAverage avg : prices.values()) {
			avg.reset();
		}
	}

	@Override
	public void notifyMarketOpened(IMarket market) {
		market.addMarketListener(this);
	}

	public boolean isStable() {
		for (AccumulatingAverage ma : prices.values()) {
			if (!ma.isStable()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void notifyDayEnded(int day, double utility) {
		if (day == startRecordingDate) {
			notifyTradesCancelled();
			System.out.print("\t");
			for (Good good: prices.keySet()){
				System.out.print(good.toString() + "\t");
			}
			System.out.println();
		} else if (day >= startRecordingDate && day < endRecordingDate) {
			String line = Integer.toString(day);
			for (Entry<Good, AccumulatingAverage> e : prices.entrySet()) {
				AccumulatingAverage avg = e.getValue();
				volume.get(e.getKey()).add(avg.getWeight());
				double val = avg.flush();
				line += "\t" + val;
			}
			System.out.println(line);
		}
	}

	public void printResult(PrintStream ps) {
		for (Map.Entry<Good, AccumulatingAverage> e : prices.entrySet()) {
			double price = e.getValue().getWrapped().getAverage();
			ps.println(e.getKey() + " price: " + price);
			if (volume.containsKey(e.getKey())) {
				double vol = volume.get(e.getKey()).getAverage();
				ps.println("\tproduction: " + vol);
				ps.println("\tturnover: " + vol * price);
			}
		}

		// if (prices.containsKey(new Good("output 0"))) {
		// AccumulatingAverage pizzaPrice = prices.get(new Good("output 0"));
		// double priceNormalization = pizzaPrice.getWrapped().getAverage();
		// System.out.println("\nNormalized prices:");
		// for (Map.Entry<Good, AccumulatingAverage> e : prices.entrySet()) {
		// ps.println(e.getKey() + " price: " + e.getValue().getWrapped().normalize(priceNormalization));
		// if (volume.containsKey(e.getKey())) {
		// ps.println(e.getKey() + " production: " + volume.get(e.getKey()).normalize(priceNormalization));
		// }
		// }
		// }
	}

	public Result getResult() {
		Result res = new Result();
		for (Map.Entry<Good, AccumulatingAverage> e : prices.entrySet()) {
			res.include(e.getKey(), e.getValue().getWrapped().getAverage(), volume.get(e.getKey()).getAverage());
		}
		return res;
	}

}
