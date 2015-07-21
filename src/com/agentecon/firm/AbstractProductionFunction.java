package com.agentecon.firm;

import com.agentecon.consumer.Weight;
import com.agentecon.good.Good;

public abstract class AbstractProductionFunction implements IProductionFunction {

	protected Good output;
	protected Weight[] inputs;

	public AbstractProductionFunction(Good output, Weight... weights) {
		assert output != null;
		this.output = output;
		this.inputs = weights;
	}

	@Override
	public Good[] getInput() {
		Good[] goods = new Good[inputs.length];
		for (int i=0; i<goods.length; i++){
			goods[i] = inputs[i].good;
		}
		return goods;
	}

	@Override
	public Good getOutput() {
		return output;
	}

	@Override
	public double getWeight(Good input) {
		for (int i = 0; i < inputs.length; i++) {
			if (inputs[i].good.equals(input)) {
				return inputs[i].weight;
			}
		}
		return 0;
	}
	
}