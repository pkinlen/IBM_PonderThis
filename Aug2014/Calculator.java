
public class Calculator {

	private int    m_min;
	private int    m_max;
	
	private double m_feeWhenGuessLESoln; // LE: less than or equal to
	private double m_feeWhenGuessGTSoln; // GT: greater than
		
    ////////////////////////////////////////////////////////////////////////
	public static void main(String[] args) {
    	Calculator obj = new Calculator();
    	obj.calc();
    	System.out.println("Finished");
    }    
    ////////////////////////////////////////////////////////////////////////
    public Calculator(){
    	m_min                = 51;
    	m_max                = 150;
    	
    	m_feeWhenGuessLESoln = 0.01;
    	m_feeWhenGuessGTSoln = 0.1;
    }
    ////////////////////////////////////////////////////////////////////////
    public void calc(){
    	int    maxNumStates = m_max - m_min + 1;
    	
    	// We have an array of costs. The index is the number of states.
    	// When we have 2 states the cost will be 
    	// We know for example that 
    	//     costs[2] = min(m_feeWhenGuessLESoln, m_feeWhenGuessGTSoln)
    	double costs[]      = new double[maxNumStates+1];
    	costs[0] = 0;
    	costs[1] = 0; // when we have only 1 state, we've found the solution
    	
    	for ( int numStates = 2; numStates <= maxNumStates; numStates++ ){
    		
    		boolean haveSetMinCost = false;
    		int     optimalGuess   = 0;
    		double  minCost        = 0; // just set to zero to avoid compilation warning 
    		
    		// We now need to find the best guess, i.e. the one with the lowest cost.
    		for ( int guess = 1; guess < numStates; guess++ ){
    			
    			double cost = getCostOfGuess(guess, numStates, costs);
    			
    			if ( ! haveSetMinCost){
    			     minCost        = cost;
    			     optimalGuess   = guess;
    			     haveSetMinCost = true;
    			} else if ( cost    < minCost){
    				 minCost        = cost;
    			     optimalGuess   = guess;
    			}
    		}
    		costs[numStates] = minCost;
    		System.out.println(    "When we have "             + Integer.toString(numStates) 
    				           + " states, the min cost is: "  + Double. toString(minCost) 
    				           + ", optimal guess is: "        + Integer.toString(optimalGuess));
     	}
    }
    /////////////////////////////////////////////////////
    public double getCostOfGuess(int guess, int numStates, double[] costs){
    	double probGuessLESoln = (double)( numStates - guess) / (double)numStates;
    	
    	return  probGuessLESoln * (m_feeWhenGuessLESoln + costs[numStates - guess]) 
    			 + ( 1.0 - probGuessLESoln) * (m_feeWhenGuessGTSoln + costs[guess]);
    }
    ////////////////////////////////////////////////////////////////////////
}
