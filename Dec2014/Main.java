import java.util.Random;

public class Main {

	private int         m_numRows;
	private int         m_numCols;
	
	private Random      m_randGen;
	
	public int[]       m_rowCounter;
	public int[]       m_colCounter;
    /////////////////////////////////////////////////////////////////////////	
	public static void main(String[] args) {
	    System.out.println("Starting...");
		Main main = new Main();
		
		int numRows             = 7;
		int numCols             = 7;
		int targetNumSolutions  = 29;
		
		main.findSolution(numRows, numCols, targetNumSolutions);
		
		System.out.println("Finished.");
	}
	///////////////////////////////////////////////////////////////////////
	public Main(){
		
		resetRandomGenerator(0); // to use the timer as the seed, use negative parameter
	}
	///////////////////////////////////////////////////////////////////////////////
	public void findSolution(int numRows, int numCols, long targetNumSolutions){
		
		boolean finished = false;
		long    counter  = 0;
		
		while( ! finished ) {
			
			m_numRows = numRows;
			m_numCols = numCols;
					
			resetRowsAndCols();
			
			State initState = new State( m_numRows, m_numCols, this);
			
			long numSoln    = initState.getNumSolutions();
			
			if ( numSoln == targetNumSolutions){
				System.out.println("\n\nFound Solution!\n\n");
				printResults(targetNumSolutions, numSoln, counter);
				finished = true;
			} else if ( counter % 1 == 0){
				printResults(targetNumSolutions, numSoln, counter);
			}
			counter++;
		}
	}
    ////////////////////////////////////////////////////////////////////
	public void printResults(long target, long numSoln, long counter){
		System.out.println(  "After " + Long.toString(counter) 
				           + " tries, with target: " + Long.toString(target) 
				           + ", num soln: "   + Long.toString(numSoln)
                           + " RC: " + arrIntToString(m_rowCounter)
			               + " CC: " + arrIntToString(m_colCounter));	
	}
    ////////////////////////////////////////////////////////////////////
    public String arrIntToString(int[] arr)	{
    	String str =  Integer.toString(arr[0]);
    	
    	for(int i = 1; i < arr.length; i++){
    		str += ", " + Integer.toString(arr[i]);
    	}
    	
    	return str;
    }
	// for negative seeds the timer will be used.
    private void resetRandomGenerator(long seed){
    	
    	if (seed < 0)
        	m_randGen = new Random(); // could use a seed
    	else 	
    	    m_randGen = new Random(seed);
    }
    /////////////////////////////////////////////////////////////
    private void resetRowsAndCols(){
    	
    	m_rowCounter         = new int[m_numRows];
    	m_colCounter         = new int[m_numCols];    	
    	
    	int totalCounter = 0;
    	
    	for(int r = 0; r < m_numRows; r++){
    		m_rowCounter[r] = m_randGen.nextInt(m_numCols+1);
    		totalCounter += m_rowCounter[r];
    	}
    		
    	int colCounter = 0;
    	for(int c = 0; c < m_numCols; c++) {
    		int maxTotalRemaining = ( m_numCols - c - 1) * m_numRows;
    		int remainingRequired = totalCounter - colCounter;
    		int minPossible       = Math.max(0,         remainingRequired - maxTotalRemaining);
    		int maxPossible       = Math.min(m_numRows, remainingRequired);
    	
    		if ( maxPossible < minPossible)
    			System.out.println("Error: max ("+ Integer.toString(maxPossible)
    					    + ") less than min (" + Integer.toString(minPossible) 
    					    + ") "
    					   );
    		else if ( maxPossible == minPossible)
    			m_colCounter[c] = minPossible;
    		else
    		    m_colCounter[c] = minPossible + m_randGen.nextInt(maxPossible - minPossible + 1);    		
    		
    		colCounter += m_colCounter[c];
    	}
    	
    	if ( totalCounter != colCounter)
    		System.out.println("We have a coding error, counters aren't equal.");
    }
        ////////////////////////////////////////////////////////////////   
}       // end of class Main
