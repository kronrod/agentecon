# An Agent-Based Simulation of the Stolper-Samuelson Effect

This branch contains the model described in the paper "An Agent-Based Simulation of the Solper-Samuelson effect" as revised and submitted to the journal Computational Economics.

In order to modify and run the model yourself, you should follow these steps:

1. Make sure a git client is installed. I am using SourceTree: https://www.sourcetreeapp.com/
2. Make sure an IDE for Java is installed. I am using eclipse for Java EE developers, obtainable from https://eclipse.org/downloads/ .
3. Add this repository (https://github.com/kronrod/agentecon.git) in your git and checkout the branch ComputationalEconomicsPaper (https://github.com/kronrod/agentecon/tree/ComputationalEconomicsPaper)
4. Import the project to eclipse. In case you are using a different IDE, make sure to add jar\agenteconinterface.jar and jar\jacop-4.3.0.jar library to the classpath.
5. Run the class CompEconCharts to get the results presented in the paper. The programs output is also listed in the CompEconCharts.output file https://github.com/kronrod/agentecon/blob/ComputationalEconomicsPaper/src/com/agentecon/CompEconCharts.output .

Experimentally, the resulting data is also visualized on http://master.agentecon.com/sim.html?id=ComputationalEconomicsPaper (this might not work in your browser).