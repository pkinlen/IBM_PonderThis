import java.util.Random;

public class Calculator {
	private double m_avg;
	private double m_stdDev;

	public  double getAvg()    { return m_avg; }
	public  double getStdDev() { return m_stdDev; }

	public Calculator(Simulator sim, long numSims, long seed){
		// System.out.println("\nNow doing the " + Long.toString(numSims) + " Monte Carlo simulations.\n");
		double rSum    = 0;  
		double rSumSq  = 0;

		Random randGen; 
		if ( seed < 0) // if the seed is below zero we use the timer to initialize the rand generator
			randGen = new Random( );
		else 
			randGen = new Random(seed);

		for ( long i = 0; i < numSims; i++){
			double res = sim.runOneSim(randGen);
			rSum   += res;
			rSumSq += res * res;    
		}

		double s_sq = ( rSumSq / numSims ) - (rSum * rSum / (numSims * numSims));

		m_avg    = rSum / numSims;
		m_stdDev = Math.pow(s_sq  / numSims, 0.5);
	}
	// For the record, in June 2014 I ran a little test to check that the
	// standard deviation calculated here is correct.
	// From the results it looked reasonable. 
	
    //////////////////////////////////////////////////////////////////
}
