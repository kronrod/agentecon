package com.agentecon.events;

import com.agentecon.agent.Endowment;
import com.agentecon.good.Good;
import com.agentecon.good.Stock;
import com.agentecon.sim.config.SimConfig;
import com.agentecon.trader.Arbitrageur;
import com.agentecon.world.IWorld;

public class ArbitrageurEvent extends EvolvingEvent {
	
	private static final double CAPITAL = 10;

	private Arbitrageur agent;

	public ArbitrageurEvent(Good good) {
		this(new Arbitrageur(createEndowment(), good));
	}
	
	protected ArbitrageurEvent(Arbitrageur agent) {
		super(0, -1);
		this.agent = agent;
	}

	private static Endowment createEndowment() {
		return new Endowment(new Stock[]{new Stock(SimConfig.MONEY, CAPITAL)}, new Stock[]{});
	}
	
	@Override
	public void execute(IWorld sim) {
		sim.add(agent);
	}
	
	public double getScore() {
		return agent.getMoney().getAmount() - CAPITAL;
	}

	public Arbitrageur getAgent() {
		return agent;
	}

	@Override
	public EvolvingEvent createNextGeneration() {
		return new ArbitrageurEvent(agent.createNextGeneration(createEndowment()));
	}
	
	public String toString(){
		return "Arbitrageur with " + agent.getInventory();
	}

}
