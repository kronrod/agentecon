package com.agentecon.events;

import java.util.Collection;
import java.util.Random;

import org.junit.Test;

import com.agentecon.agent.Endowment;
import com.agentecon.api.IConsumer;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.LogUtil;
import com.agentecon.good.Stock;
import com.agentecon.metric.ISimulationListener;
import com.agentecon.sim.config.IUtilityFactory;
import com.agentecon.sim.config.SimConfig;
import com.agentecon.world.IConsumers;
import com.agentecon.world.IFirms;
import com.agentecon.world.ITraders;
import com.agentecon.world.IWorld;

public class SinConsumerEventTest implements IWorld, IConsumers {
	
	private int day;
	private int consumers;

	@Test
	public void test() {
		SinConsumerEvent e = new SinConsumerEvent(50, 7, 100, 150, "test", new Endowment(new Stock(SimConfig.BEER, 1)), new IUtilityFactory() {
			
			@Override
			public IUtility create(int number) {
				return new LogUtil();
			}
		});
		for (day = 50; day<200; day++){
			e.execute(this);
		}
		assert consumers == 107;
	}

	@Override
	public IConsumers getConsumers() {
		return this;
	}

	@Override
	public IFirms getFirms() {
		return null;
	}

	@Override
	public Random getRand() {
		return null;
	}

	@Override
	public int getDay() {
		return day;
	}

	@Override
	public void add(Consumer consumer) {
		consumers++;
	}

	@Override
	public Collection<Consumer> getRandomConsumers() {
		return null;
	}

	@Override
	public Collection<Consumer> getRandomConsumers(int cardinality) {
		return null;
	}

	@Override
	public Consumer getRandomConsumer() {
		return null;
	}

	@Override
	public ITraders getTraders() {
		return null;
	}

	@Override
	public void addListener(ISimulationListener listener) {
	}

	@Override
	public Collection<? extends IConsumer> getAllConsumers() {
		return null;
	}

}
