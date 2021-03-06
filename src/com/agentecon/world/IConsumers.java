package com.agentecon.world;

import java.util.Collection;

import com.agentecon.api.IConsumer;
import com.agentecon.consumer.Consumer;

public interface IConsumers {

	public Consumer getRandomConsumer();

	public Collection<Consumer> getRandomConsumers();
	
	/**
	 * @param number of randomly chosen agents, or all if -1
	 * @return
	 */
	public Collection<Consumer> getRandomConsumers(int cardinality);

	public Collection<? extends IConsumer> getAllConsumers();

}
