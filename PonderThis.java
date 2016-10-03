package Calc;


public class PonderThis {
	public static long m_counter;  // This counter is just used for diagnostics. 
	
     public static void main ( String[] argss){
    	 
    	 System.out.println("Has begun.");
    	 
    	 solve();
    	 
    	 System.out.println("Finished.");
     }
     
     ////////////////////////////////////////////////////
     public static Solution solve(){
    	     	     	 
    	 m_counter = 0;
    	 
    	 boolean  finished           = false;
    	 int      numAllowedFlaws    = 0;
    	 Solution finalSolution      = null;
    	 
    	 while ( ! finished ){
    	     System.out.println(   "About to start, the number of allowed flaws is: " + Integer.toString(numAllowedFlaws) 
                                 + " \tCounter: "                                     + Long.toString(m_counter)          );       
             
    		 finalSolution = solve(new Solution(numAllowedFlaws));

    		 if ( finalSolution != null){
    			 System.out.println("Found solution, number of allowed flaws is: "+ Integer.toString(numAllowedFlaws));
    			 System.out.println("Number of steps: " + Long.toString(m_counter));
    			 finalSolution.print();
                 finalSolution.printTranspose();
    			 
    			 finished = true;
    			 
    		 } else 
    			 numAllowedFlaws++;    	    		 
    	 }    	 
    	 
    	 return finalSolution;
     }
     ////////////////////////////////////////////////////
     // The following function is recursive.
     public static Solution solve(Solution s){
     
    	if ( s.m_current == (Solution.m_length - 1))
    		return s; // we have a solution
    	 
    	int i = 0;
    	    	
    	while ( i < Solution.m_length) {
    		
    		
    		if ( s . isAllowed(i)){
    			
    			Solution attempt = solve(new Solution(s, i));
    			
    			if ( attempt != null)
    			    return attempt;
    		}
    		
    		i++;
    		//////////////////////////////////////////////////////////////////
    		// We now have a bit of code that might be helpful for debugging:
            m_counter++;    
            //if ( true) {
            if ( m_counter % 10000L == 0L) {
            	System.out.println("counter: " + Long.toString(m_counter) + ", current: " + Integer.toString(s . m_current));
                s . print();
            }
    	}
        return null; // i.e. a solution not found.    	 
    	     		
     }
     ///////////////////////////////////////////
}