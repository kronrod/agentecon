package com.agentecon;

import com.agentecon.price.EPrice;
import com.agentecon.price.PriceConfig;
import com.agentecon.sim.SimConfig;
import com.agentecon.sim.Simulation;
import com.agentecon.verification.Result;
import com.agentecon.verification.StolperSamuelson;
import com.agentecon.verification.StolperSamuelsonParameterExploration;

public class CompEconCharts {

	public String createAccuracyBenchmark() {
		String table = "Method\tp_pizza / p_fondue\tx_pizza";
		final StolperSamuelson bm = new StolperSamuelson(3.0);
		Result hint = null;
		for (PriceConfig config : PriceConfig.STANDARD_CONFIGS) {
			if (config.isSensor()) {
				Result res = bm.runAgentBased(config, 2000);
				table += "\n" + config.getName() + "\t" + res.getRatio(bm.getPizza(), bm.getFondue()) + "\t" + res.getAmount(bm.getPizza());
				hint = res;
			}
		}
		Result resBenchmark = bm.runConstrainedOptimization(null, 0.0001);
		table += "\nBenchmark\t" + resBenchmark.getRatio(bm.getPizza(), bm.getFondue()) + "\t" + resBenchmark.getAmount(bm.getPizza());
		return table;
	}

	public String createChartData(PriceConfig priceConfig) {
		StolperSamuelson ss = new StolperSamuelson(3.0);
		SimConfig config = ss.createConfiguration(priceConfig, 2000);
		for (int i = 0; i < StolperSamuelson.CONSUMERS_PER_TYPE; i++) {
			ss.enableShock(config, 1000 + i, 3.0);
		}
		Simulation sim = new Simulation(config);
		ChartData data = new ChartData(ss.getPizza(), ss.getFondue(), ss.getItalianHours(), ss.getSwissHours());
		sim.addListener(data);
		sim.finish();
		return data.getTable();
	}

	public static void main(String[] args) {
		CompEconCharts charts = new CompEconCharts();
		System.out.println("\n***************** FIGURE 8 *****************");
		System.out.println(charts.createChartData(new PriceConfig(true, EPrice.EXPSEARCH)));
		System.out.println("\n***************** FIGURE 9 *****************");
		System.out.println(charts.createChartData(new PriceConfig(true, EPrice.CONSTANTFACTOR)));
		System.out.println("\n***************** ACCURACY BENCHMARK *****************");
		System.out.println(charts.createAccuracyBenchmark());
		System.out.println("\n***************** PARAMETER EXPLORATION *****************");
		System.out.println(new StolperSamuelsonParameterExploration(1.0, 5.0, 0.1).run());
	}
}