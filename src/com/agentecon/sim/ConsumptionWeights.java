package com.agentecon.sim;

import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.LogUtil;
import com.agentecon.consumer.Weight;
import com.agentecon.good.Good;

public class ConsumptionWeights {

	public static final double TIME_WEIGHT = 14.0;
	public static final double[] WEIGHTS = new double[] { 3.0, 1.0, 6.0 };
	private static final int MAX_CONSUMPTION_GOODS = 3;

	private Weight[] inputs;
	private Weight[] outputs;

	public ConsumptionWeights(Good[] inputs, Good[] outputs) {
		this(inputs, outputs, WEIGHTS);
	}

	public ConsumptionWeights(Good[] inputs, Good[] outputs, double... weights) {
		this.inputs = new Weight[inputs.length];
		this.outputs = new Weight[outputs.length];
		for (int i = 0; i < inputs.length; i++) {
			this.inputs[i] = new Weight(inputs[i], TIME_WEIGHT);
		}
		for (int i = 0; i < outputs.length; i++) {
			this.outputs[i] = new Weight(outputs[i], weights[i % weights.length]);
		}
	}

	public LogUtil createUtilFun(int type) {
		int count = Math.min(MAX_CONSUMPTION_GOODS, outputs.length);
		Weight[] prefs = new Weight[count + 1];
		for (int i = 0; i < count; i++) {
			prefs[i] = outputs[i];
		}
		prefs[prefs.length - 1] = inputs[type];
		return new LogUtil(prefs);
	}

	public LogUtil createDeviation(LogUtil basis, Good changedGood, double newWeight) {
		if (basis.isValued(changedGood)) {
			Good[] goods = basis.getGoods();
			double[] weights = basis.getWeights();
			Weight[] newWeights = new Weight[goods.length];
			for (int i = 0; i < goods.length; i++) {
				double w = goods[i] == changedGood ? newWeight : weights[i];
				newWeights[i] = new Weight(goods[i], w);
			}
			return new LogUtil(newWeights);
		} else {
			return basis;
		}
	}

	public IUtilityFactory getFactory(final int type) {
		return new IUtilityFactory() {

			@Override
			public IUtility create(int number) {
				return createUtilFun(type);
			}
		};
	}

}