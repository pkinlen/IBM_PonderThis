import java.util.Random;

//
// The following code was written to solve a puzzle posed by IBM:
// http://domino.research.ibm.com/Comm/wwwr_ponder.nsf/Challenges/April2014.html
//

// The code below clearly has not been written as 'production quality'.
// It is what I would call 'throw-away code'.

public class TwosFours {
	
    public static void main(String[] args) {
        System.out.println("Here you go:");  
        int numSlots = 5;
        
        System.out.println("With num of slots: " + Integer.toString(numSlots));
        
        Fraction res = findExpectedMax(numSlots);
        System.out.println("we have expected max: " + res.toString(true));
        System.out.println("i.e.:                 " + res.toString(false));
        System.out.println("which is approx:      " + Double.toString(res.toDouble()));    
        
        long numSim = 1000000;
        doMonteCarlo(numSlots,numSim);
    }

    /////////////////////////////////////////////////////////////////////////
    // for fractions of the form ( num / ( 2^pDenom) )
    private static class Fraction{
    	public long num;
    	public long pDenom;    	
    	
    	public String toString(boolean expandPower){
    		if (expandPower)
    			return Long.toString(num) + " / " + Long.toString(power(2L, pDenom));
    		else
    			return Long.toString(num) + " over 2^" + Long.toString(pDenom);
     	}
    	
    	public double toDouble(){
    		return (double) (num / Math.pow(2, pDenom));
    	}

    	public Fraction(long iNum, long ipDenom){
    		num    = iNum;
    		pDenom = ipDenom;
    	}
    	
    }
    /////////////////////////////////////////////////////////////////////////
    private static long power(long a, long b){
    	// we assume that b >=0
    	long res = 1;
    	
    	for(long i = 0; i < b; i++){
    		res *= a;
    	}
    	
    	return res;
    }
    /////////////////////////////////////////////////////////////////////////        
    private static Fraction findExpectedMax(int numSlots){

    	long[] arr = initArray(numSlots,0);

        return getConditionalExpectedMax(arr);
    }
    
    private static void doMonteCarlo(int numSlots, long numSim){
    	
    	System.out.println("\nNow doing Monte Carlo simulations:");
    	
    	double rSum    = 0;
    	double rSumSq  = 0;
    	
    	Random randGen = new Random();
    	
    	for ( long i = 0; i < numSim; i++){
    		double sim = getMaxForOneInstance(numSlots, randGen);
    		rSum   += sim;
    		rSumSq += sim * sim;    		
    	}
    	
    	double avgMax   = rSum / numSim;
    	double s_sq     = ( rSumSq / numSim ) - (rSum * rSum / (numSim * numSim));
    	double stdDev   = Math.pow(s_sq  / numSim, 0.5);
    	
    	System.out.println("With numSlots:           " + Long.toString(numSlots));
    	System.out.println("and numSim:              " + Long.toString(numSim));
    	System.out.println("found average max to be: " + Double.toString( avgMax));
    	System.out.println("with standard deviation: " + Double.toString(stdDev));
    	
    }
    
    private static double getMaxForOneInstance(int numSlots, Random randGen){
    	long[] arr = initArray(numSlots,0);

    	boolean finished = false;
    	
    	while ( ! finished ){
    		long elm = getRandTwoOrFour(randGen);
    		arr = insert(arr, elm); // for speed, could introduce a flag in insert
    		                        // which would turn on and off the deep copy.
    		
    		finished = alreadyHaveMax(arr);
    	}
    	
    	return (double) maxElm(arr);
    }
    
    private static long getRandTwoOrFour(Random randGen){
    	
    	if (randGen.nextInt(2) == 0 )
    		return 2L;
    	else 
    		return 4L;
    	
    }
    
    ////////////////////////////////////////////////////////////////////////
    // the following function gets called recursively.
    private static Fraction getConditionalExpectedMax(long[] arr){
          
          if ( alreadyHaveMax(arr)) 
        	  // here we have a denominator of 1 i.e. 2^0
              return new Fraction(maxElm(arr), 0L);
          else {
              // note that the insert method makes a copy before altering the array        
              Fraction cem2 = getConditionalExpectedMax(insert(arr, 2));
              cem2.pDenom = cem2.pDenom + 1;
            
              Fraction cem4 = getConditionalExpectedMax(insert(arr, 4));
              cem4.pDenom = cem4.pDenom + 1; 
            
              // we return ((0.5 * condExpMaxWith2Inserted) + (0.5 * condExpMaxWith4Inserted)) 
              return addFractions( cem2, cem4);
          }  
    }  
    ///////////////////////////////////////////////////////////////////
    private static long[] insert(long[] arr, long n){
      
        long[]   res                = deepCopy(arr);
        boolean  finished           = false;
        long     counter            = 0;
        int      indexOfFirstEmpty  = getIdxOfFirstEmpty(arr);
        res[indexOfFirstEmpty] = n;
      
        // keep doing merges until no neighbours are the same
        while( !finished){
            int idxOfEq = indexOfEqualNeighbours(res);
          
            if ( idxOfEq == -1)
                finished =  true;
            else 
                mergeNeighbours(res, idxOfEq); 
          
            counter++;
          
            if ( counter > (res.length + 1))
               reportError("Error in while loop" );        
        }
      
        return res;
    }
    ////////////////////////////////////////////////////////////////
    private static void reportError(String msg){
        System.out.println(msg); // Display the string.
    }
    
    ////////////////////////////////////////////////////////////////////////
    private static int indexOfEqualNeighbours(long[] arr){
      
        for (int i = 0; i < (arr.length - 1); i++){
            if (( arr[i] != 0) && (arr[i] == arr[i+1]))
               return i;
        }
        return -1;
    }
    ////////////////////////////////////////////////////////////////////////
    private static void mergeNeighbours(long[] arr, int idx){
      
          arr[idx] = arr[idx] + arr[idx+1];

          for (int i = idx+1; i < arr.length-1; i++){
              arr[i] = arr[i+1];       
          }
          arr[arr.length-1] = 0;
    } 
    
    private static int getIdxOfFirstEmpty(long[] arr){
              
        for(int i = 0; i < arr.length; i++){
             if ( arr[i] == 0) 
               return i;
        }
        
        reportError("Didn't find empty elm.");
        return -1;
    }

    private static long[] deepCopy(long[] arr){
        long[] res = new long[arr.length];
      
        for(int i = 0; i < arr.length; i++){
             res[i] = arr[i];  
        }  
        return res;
    }      

    private static boolean alreadyHaveMax(long[] arr){
        // we could be more clever here.
        // for example if we had [2,32,0,0,0], then we know we already have the max
      
        long maximum = maxElm(arr);
        
        if ( maximum == getMaxPossible(arr))
           return true;
        else
          // if there are any zeros left, i.e. spaces, then we may be able to get a higher max
           return ( minElm(arr) >= 2 );
    }

    private static long getMaxPossible(long[] arr){
      
       long x = arr.length + 1; // - numIncreasingElements(arr);
      
       return (long) power(2L, x);
    }

    private static long numIncreasingElements(long[] arr){
     
         long res = 0;
         
         for (int i = 1; i < arr.length; i++){
             if ( arr[i-1] < arr[i])
                res++;
         }
         return res;
    }  

    private static long maxElm(long[] arr){
        long  currentMax = arr[0];
        for ( int index = 1; index < arr.length; index++){
             if ( arr[index] > currentMax)
                 currentMax = arr[index];
        }
        return currentMax;
    }  

    private static  long minElm(long[]  arr){
        long  currentMin = arr[0];
        for (int index = 1; index < arr.length; index++){
             if ( arr[index] < currentMin)
                 currentMin = arr[index];
        }
        return currentMin;
    }  

    // The denominators are in powers of 2
    static Fraction addFractions(Fraction A, Fraction B) {
      
      Fraction Res = new Fraction(0,0);
      
      if ( A.pDenom > B.pDenom) {
        Res.num = (long) (A.num + B.num * power(2, A.pDenom - B.pDenom));
        Res.pDenom = A.pDenom;
        
      } else {
        
        Res.num = (long) (B.num + A.num * power(2, B.pDenom - A.pDenom));
        Res.pDenom = B.pDenom;
        
      }
      
      // divide out 2's from numerator and denominators
      while ( isEven(Res.num) && (Res.pDenom > 0)){
         Res.num = Res.num / 2;
         Res.pDenom = Res.pDenom - 1;
      }   
        
      return Res;     
    }

    static private boolean isEven(long n) 
    {
       return ((n % 2) == 0);
    }

    static long[] initArray(int length, long value) {
        long[] arr = new long[length];

        for(int i = 0; i < length; i++)
        	arr[i] = value;

        return arr;
    }
    /////////////////////////////////////////////////////////  
    // for debbugging:
    /*
    private static String arrToString(long[] arr){
        String res = Long.toString(arr[0]);
        for(int i = 1; i < arr.length; i++){
          res = res + ", " +  Long.toString(arr[i]); 
        }
      
        return res;
    }
    */
}    

