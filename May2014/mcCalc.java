
public class mcCalc {
	/////////////////////////////////////////////////////
    public static void main(String[] args) {
    	//MCForLoop();
    	
    	doMonteCarlo();
    }
    /////////////////////////////////////////////////////
    public static void doMonteCarlo(){    	
    	
    	long       numSims = 10000L; // The number of simulations to run
    	
    	long       seed    = -1L;  // when the seed is negative, the timer will be 
    	                           // used to initialize the random number generator.
    	
    	// If you want to use this little java monte carlo engine to solve your own problem,
    	// then write a class that implements the 'Simulator' interface.
    	// after that instantiate it here and then instantiate a Calculator object.
    	
    	// Simulator  sim  = new SurnameOnIsland();    	
    	Simulator     sim  = new CollidingBullets(); 
    	
    	Calculator calc    = new Calculator(sim, numSims, seed);
    	   	
    	String msg = "Average MC result: "  + Double.toString( calc.getAvg())
    			    + " with std dev: "     + Double.toString( calc.getStdDev());
    	
    	System.out.println(msg);
    	
    	System.out.println("\nFinished.");
    }    
    ////////////////////////////////////////////////////////////////
    public static void reportError(String msg){
        System.out.println(msg); // Display the string.
    }
    ////////////////////////////////////////////////////////////////    
    public static void MCForLoop(){
    	int n = 100;
    	for ( int i = 0; i < n; i++)
    		doMonteCarlo();
    }
}    

