// Created by Luzius on Apr 28, 2014

package com.agentecon.firm;

import java.util.Arrays;

import com.agentecon.agent.Agent;
import com.agentecon.agent.Endowment;
import com.agentecon.api.IFirm;
import com.agentecon.firm.production.IPriceProvider;
import com.agentecon.firm.production.IProductionFunction;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.good.Stock;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.metric.FirmListeners;
import com.agentecon.metric.IFirmListener;
import com.agentecon.price.IPriceFactory;
import com.agentecon.util.MovingAverage;

public class Firm extends Agent implements IFirm, IPriceProvider {

	private static final boolean FRACTIONAL_SPENDING = false;

	public static double DIVIDEND_RATE = 0.1;

	// private ShareRegister register; clone?
	protected InputFactor[] inputs;
	protected OutputFactor output;
	private IProductionFunction prod;

	private FirmListeners monitor;

	protected IPriceFactory prices;

	public Firm(String type, Endowment end, IProductionFunction prod, IPriceFactory prices) {
		super(type, end);
		this.prod = prod;
		this.prices = prices;
		// this.register = new ShareRegister(getName(), getMoney());

		Good[] inputs = prod.getInput();
		this.inputs = new InputFactor[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			this.inputs[i] = createInputFactor(prices, getStock(inputs[i]));
		}
		IStock outStock = getStock(prod.getOutput());
		this.output = createOutputFactor(prices, outStock);
		this.monitor = new FirmListeners();
	}

	protected OutputFactor createOutputFactor(IPriceFactory prices, IStock outStock) {
		return new OutputFactor(outStock, prices.createPrice(outStock.getGood()));
	}

	protected InputFactor createInputFactor(IPriceFactory prices, IStock stock) {
		return new InputFactor(stock, prices.createPrice(stock.getGood()));
	}

	public void addFirmMonitor(IFirmListener prodmon) {
		this.monitor.add(prodmon);
	}

	public void offer(IPriceMakerMarket market) {
		double totSalaries = getTotSalaries();
		if (!getMoney().isEmpty()) {
			for (InputFactor f : inputs) {
				if (f.isObtainable()) {
					double amount = prod.getExpenses(f.getGood(), f.getPrice(), totSalaries);
					if (amount > 0) {
						f.createOffers(market, getMoney(), amount);
					} else {
						// so we find out about the true price even if we are not interested
						createSymbolicOffer(market, f);
					}
				} else {
					// in case it becomes available
					createSymbolicOffer(market, f);
				}
			}
		}
		output.createOffer(market, getMoney(), output.getStock().getAmount());
	}

	static double totActual;
	static double totDesired;

	protected double getTotSalaries() {
		if (FRACTIONAL_SPENDING) {
			double totSalaries = prod.getCostOfMaximumProfit(this);
			double actual = getMoney().getAmount() * 0.2;

			return actual;
		} else {
			double budget = getMoney().getAmount() * 0.5;
			double totSalaries = prod.getCostOfMaximumProfit(this);
			double actual = Math.min(budget, totSalaries);
			totActual += actual;
			totDesired += totSalaries;
//			System.out.println(actual + " vs desired " + totSalaries + ", avg " + (totActual/totDesired));
			return actual;
		}
	}

	@Override
	public Firm clone() {
		Firm klon = (Firm) super.clone();
		klon.output = output.duplicate(klon.getStock(output.getGood()));
		klon.inputs = new InputFactor[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			klon.inputs[i] = inputs[i].duplicate(klon.getStock(inputs[i].getGood()));
		}
		return klon;
	}

	private void createSymbolicOffer(IPriceMakerMarket market, InputFactor f) {
		if (getMoney().getAmount() > 100) {
			f.createOffers(market, getMoney(), 1);
		}
	}

	public void adaptPrices() {
		adaptInputPrices();
		adaptOutputPrice();
	}

	public void adaptInputPrices() {
		for (int i = 0; i < inputs.length; i++) {
			inputs[i].adaptPrice();
		}
	}

	public void adaptOutputPrice() {
		output.adaptPrice();
	}

	public double produce(int day) {
		IStock[] inputAmounts = new IStock[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			inputAmounts[i] = inputs[i].getStock().duplicate();
		}
		double produced = prod.produce(getInventory());
		monitor.notifyProduced(getType(), inputAmounts, new Stock(output.getGood(), produced));
		return produced;
	}

	public Good getGood() {
		return output.getGood();
	}

	public double payDividends(int day) {
		IStock wallet = getMoney();
		double dividend = Math.max(0, FRACTIONAL_SPENDING ? calcProfitBasedDividend() : calcRelativeDividend(wallet));
		assert dividend >= 0;
		monitor.reportDividend(dividend);

		// register.payDividend(wallet, dividend);
		wallet.remove(dividend);
		return dividend;
	}

	private MovingAverage avgProfit = new MovingAverage(0.6);

	private double calcProfitBasedDividend() {
		double profits = calcProfits();
		avgProfit.add(profits);
		return avgProfit.getAverage();
	}

	private double calcRelativeDividend(IStock wallet) {
		return wallet.getAmount() * DIVIDEND_RATE;
	}

	public double calcProfits() {
		return output.getVolume() - calcCogs();
	}

	private double calcCogs() {
		double cogs = 0.0;
		for (InputFactor input : inputs) {
			cogs += input.getVolume();
		}
		return cogs;
	}

	public IProductionFunction getProductionFunction() {
		return prod;
	}

	public void setProductionFunction(IProductionFunction prodFun) {
		this.prod = prodFun;
	}

	public double getOutputPrice() {
		return output.getPrice();
	}

	public Firm createNextGeneration(Endowment end, IProductionFunction prod) {
		return new Firm(getType(), end, prod, prices);
	}

	@Override
	public double getPrice(Good output) {
		if (output.equals(this.output.getGood())) {
			return this.output.getPrice();
		} else {
			for (InputFactor in : inputs) {
				if (in.getGood().equals(output)) {
					return in.isObtainable() ? in.getPrice() : Double.POSITIVE_INFINITY;
				}
			}
		}
		return Double.POSITIVE_INFINITY;
	}

	@Override
	public Good[] getInputs() {
		Good[] goods = new Good[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			goods[i] = inputs[i].getGood();
		}
		return goods;
	}

	@Override
	public Good getOutput() {
		return output.getGood();
	}

	@Override
	public String toString() {
		return "Firm with " + getMoney() + ", " + output + ", " + Arrays.toString(inputs);
	}

}
