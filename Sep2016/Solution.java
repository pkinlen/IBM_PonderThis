package Calc;

public class Solution {
    public  static final int m_length = 24;
    private static int[]     m_val;     // containing 24 ints between 1234 and 4321
        
    private        boolean[] m_used;     // The data in m_used could be deduced from the contents of m_indices
    private        int[]     m_indices;  // however we have used m_used to help the run-time efficiency.
    private        int       m_current;
    
    private        int       m_allowedFlaws;
    private        int       m_flawsSoFar;
    
    /////////////////////////////////////////////////////
    public Solution(Solution a, int i){
    	  	
    	m_indices = new int    [m_length];
    	m_used    = new boolean[m_length];

    	// I have a feeling that the efficiency of this algorithm can could be much improved.
   	    // Currently there are lots of superfluous copying of arrays.
    	System.arraycopy(a.m_indices, 0, m_indices, 0, m_length);
    	System.arraycopy(a.m_used,    0, m_used,    0, m_length);
    	
    	m_current      = a.m_current;
    	m_allowedFlaws = a.m_allowedFlaws;
    	m_flawsSoFar   = a.m_flawsSoFar    +  getNumFlaws(i);

        m_current++;    	
    	m_indices[m_current] = i;
    	
    	m_used[i] = true;
    	
    }
    ///////////////////////////////////////////////////
    public boolean isAllowed(int i){

    	if ( m_used[i])
    		return false;
    	
    	int newFlaws = getNumFlaws(i);
    	
    	return ( (newFlaws + m_flawsSoFar) <= m_allowedFlaws);
    	
    }
    ///////////////////////////////////////////////////
    public void print(){
    	System.out.println(  "Allowed flaws: "  + Integer.toString(m_allowedFlaws) 
    			           + ", flaws so far: " + Integer.toString(m_flawsSoFar  )  
    			           + ", current: "      + Integer.toString(m_current     ) );
    	int i;
    	for ( i = 0; i <= m_current; i++)
    		System.out.println(Integer.toString(m_val[m_indices[i]]));
    	
    	if ( m_current < (m_length - 1)){
    		System.out.println("The left overs: ");
    		int j = 0;
    	    for ( i = 0; i < m_length; i++){
    	    	
    	    	if ( !m_used[i]) {
    	    	    System.out.println(Integer.toString(j) + ": " + Integer.toString(m_val[i]));
    	    	    j++;   
    	    	}
    	    }
    	}
    }
    ////////////////////////////////////////////////////
    public void printTranspose(){
    	String[] m_strArr = new String[m_length];
    	
    	int i;
    	for ( i = 0 ; i <= m_current; i++){
    		m_strArr[i] = Integer.toString(m_val[m_indices[i]]);
    	}
    	
    	for( int j = 0; j < 4 ; j++){
    		String r = "";
    		
    		for ( i = 0; i <= m_current; i++)
    			r = r + getChar(m_strArr[i], j);
    		
    		System.out.println(r);
    	}    	
    }
    ///////////////////////////////////////////////////
    public String getChar(String str, int i){
    	char c = str.charAt(i);
    	
    	switch (c) {
    	  case '1' : return "a";
    	  case '2' : return "b";
    	  case '3' : return "c";
    	  case '4' : return "d";
    	  default:   return "?";
    	}
    }
    ///////////////////////////////////////////////////
    public Solution(int allowedFlaws){
    	
    	m_used         = new boolean[m_length];
    	m_used[0]      = true;
    	m_flawsSoFar   = 0;
    	m_allowedFlaws = allowedFlaws;
    	
    	m_indices    = new int    [m_length];
    	
    	m_current    = 0;
    	m_indices[0] = 0;
     
    	if ( m_val == null){ // Should only need to initialise this once.
        	m_val        = new int    [m_length];
    	    	
	    	m_val[ 0] = 1234;
	    	m_val[ 1] = 1243;
	    	m_val[ 2] = 1324;
	    	m_val[ 3] = 1342;
	    	m_val[ 4] = 1423;
	    	m_val[ 5] = 1432;
	    	m_val[ 6] = 2134;
	    	m_val[ 7] = 2143;
	    	m_val[ 8] = 2314;
	    	m_val[ 9] = 2341;
	    	m_val[10] = 2413;
	    	m_val[11] = 2431;
	    	m_val[12] = 3124;
	    	m_val[13] = 3142;
	    	m_val[14] = 3241;
	    	m_val[15] = 3214;
	    	m_val[16] = 3412;
	    	m_val[17] = 3421;
	    	m_val[18] = 4123;
	    	m_val[19] = 4132;
	    	m_val[20] = 4213;
	    	m_val[21] = 4231;
	    	m_val[22] = 4312;
	    	m_val[23] = 4321;
    	}
    }
    ////////////////////////////////////////////////////////////////
    public int getNumFlaws(int i){
    	int numFlaws = getNumFlaws(i, m_indices[m_current]);
    	
    	if ( m_current > 1)
    	    numFlaws = numFlaws + getNumFlaws(i,m_indices[m_current-1]);
    	
    	return numFlaws;
    }
    
    ////////////////////////////////////////////////////////////////
    // similar if one of the 4 digits is similar
    public int getNumFlaws( int iA, int iB){
    	int a = m_val[iA];
    	int b = m_val[iB];
    	
    	int numFlaws = 0;
    	
    	if ( (a % 10) == (b % 10)){
    		numFlaws++;
    	} 
    	
    	a = a - ( a % 10);
    	b = b - ( b % 10);
    		
    	if ( (a % 100) == (b % 100)){
    		numFlaws++;
    	} 
    	
    	a = a - ( a % 100);
    	b = b - ( b % 100);
         		
    	if ( (a % 1000) == (b % 1000)){
    		numFlaws++;
    	} 
    	
    	a = a - ( a % 1000);
    	b = b - ( b % 1000);
        		
    	if ( a == b)
    		numFlaws++;
    	
    	return numFlaws;
    }
    //////////////////////////////////////////////
    public boolean alreadyUsed(int i){
    	return m_used[i];    	
    }
    //////////////////////////////////////////////
    public int getCurrent() { return m_current; }
    //////////////////////////////////////////////
}
