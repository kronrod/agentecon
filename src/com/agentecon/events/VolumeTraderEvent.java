package com.agentecon.events;

import com.agentecon.agent.Endowment;
import com.agentecon.good.Good;
import com.agentecon.good.Stock;
import com.agentecon.trader.VolumeTrader;
import com.agentecon.world.IWorld;

public class VolumeTraderEvent extends EvolvingEvent {

	private Good good;
	private double quantity;
	private VolumeTrader agent;

	public VolumeTraderEvent(Good money, double quantity, Good good) {
		super(0, -1);
		this.good = good;
		this.quantity = quantity;
		this.agent = new VolumeTrader(createEndowment(money), quantity, good, 500);
	}

	private static Endowment createEndowment(Good money) {
		return new Endowment(new Stock[] { new Stock(money, 100000) }, new Stock[] {});
	}

	@Override
	public void execute(IWorld sim) {
		sim.add(agent);
	}

	public String toString() {
		return agent.toString();
	}

	@Override
	public EvolvingEvent createNextGeneration() {
		return new VolumeTraderEvent(agent.getMoney().getGood(), quantity + 0.1, good);
	}

	@Override
	public double getScore() {
		return agent.getProfits();
	}

	public VolumeTrader getTrader() {
		return agent;
	}

}
