import java.util.Arrays;
import java.util.Random;

//Author: Philip Kinlen, June 2014

// The following code was written to solve the puzzle posed by IBM:
// http://domino.research.ibm.com/Comm/wwwr_ponder.nsf/Challenges/June2014.html
/*
    Design 25 cubes so that each cube's six faces display integer numbers in the range 0,1,...,31 
    such that: 
      1. All the numbers on each cube are different 
      2. Any given two cubes share exactly one common number
    Please give your answer as 25 lines of six numbers.
 */
//
// The RandSearch() method often comes up with a result within one minute ( on my laptop).

// On the other hand the SequentialSearch() method is much much slower.
// After 8 hours of running it was found to have made little progress.
// That said, I'm confident that if it were left for an infinite amount of time,
// then it would come up with a solution!
public class Cubes {
	private final int  numSides          = 6;
	private final int  numCubes          = 25;
	private final int  maxAllowed        = 31; // sides of cube can have integers 0 to max inclusive

	// private final int  numAllowedRepeats = ( numSides * numCubes) / ( maxAllowed + 1);

	private       int  minRollBack       = numCubes + 1;
	private       int  maxRollBack       = -1;
	private       long numRollBacks      = 0;

	private       long numTrialRows      = 0;

	///////////////////////////////////////////////////////////////
	public static void main(String[] args) {

		Cubes cubes = new Cubes();

		//cubes.SequentialSearch();
		cubes.RandSearch();
	}
	///////////////////////////////////////////////////////////////
	private void RandSearch(){
		int     data[][]   = new int[numCubes][numSides]; 
		int     row        = 0;
		Random  rndGen     = new Random(10);

		int     bestSoFar  = 0;
		long    counters[] = new long[numCubes];

		boolean finished   = false;
		while ( ! finished ){
			setRowWithRnd(data, row, rndGen);

			if (acceptable( data, row)){
				row++;
				if ( row == numCubes) {
					System.out.println("\n\nFound solution:");
					finished = true;
				} else 
					counters[row] = 0;

			} else {
				counters[row]++;

				if ( row > 0)
					counters[row-1]++;

				if ( row > bestSoFar ){
					bestSoFar = row;

					System.out.println("\nNumber of valid rows: " + Integer.toString(row));

					for(int r = 0; r < row; r++)
						printRow(data, r, true);
					System.out.println("\nNumber of rows tried: " + Long.toString(numTrialRows));

				}
				// sometimes we want to roll back to the previous row
				if ( counters[row] > 20 * ( row * row * row + 1) )  {  	
					//System.out.println("\n\nResetting to row zero\n\n");
					counters[row] = 0;
					row = ( row == 0 ? 0 : row - 1);
				}
			}
		}
		displayResults(data);

		System.out.println("Number of rows tried: " + Long.toString(numTrialRows));
		System.out.println("Finished.");
	}
	////////////////////////////////////////////////////////////////////////
	private void displayResults(int[][] data){
		SortableRow[] rows = new SortableRow[data.length];

		for ( int i = 0; i < data.length; i++){
			rows[i] = new SortableRow(data[i]);
		}

		Arrays.sort(rows);

		for( int r = 0; r < rows.length; r++){
			String str = "";
			for ( int c = 0; c < data[0].length; c++){
				str += " " + Integer.toString(rows[r].get(c));
			}
			System.out.println(str);
		}

	}
	///////////////////////////////////////////////////////////////////////
	private void setRowWithRnd(int data[][], int row, Random rndGen){
		for( int i = 0; i < data[row].length; i++)
			data[row][i] = rndGen.nextInt(maxAllowed + 1);

		Arrays.sort(data[row]);

		numTrialRows++;
	}
	/////////////////////////////////////////////////////
	// Very very slow at finding a solution!
	public  void SequentialSearch(){

		int     data[][] = new int[numCubes][numSides]; 
		int     row      = 0;

		setTrialRow(data, row);

		boolean finished = false;
		while ( ! finished ){

			if (canSetNextRow(data, row)){
				row++;

				if ( row == numCubes) {
					System.out.println("\n\nFound solution:");
					finished = true;
				} else 	
					setTrialRow(data, row);

			} else {
				row--;
				reportRollBack(data, row);

				if ( (row < 0) || ! moveToNext(data, row)){
					System.out.println("\n\nWas unable to find solution");
					finished = true;
				}
			}
		}
		printData(data, false);

	}
	/////////////////////////////////////////////////////////////////////    
	private boolean canSetNextRow(int data[][], int row){

		while  ( ! acceptable( data, row) ){

			if ( ! moveToNext( data, row) )
				return false;
		}	
		return true;
	}
	/////////////////////////////////////////////////////////////////
	private void setTrialRow(int data[][], int row){

		for(int i = 0; i < data[row].length; i++){
			if ( row == 0)
				data[row][i] = i;
			else
				data[row][i] = data[row-1][i];
		}
		numTrialRows++;
	}
	/////////////////////////////////////////////////////////////////
	private boolean moveToNext(int data[][], int row){

		int     i        = 0;
		boolean finished = false;

		while ( ! finished ){

			if (   ((i+1) == data[row].length) 
					|| ((data[row][i] + 1) != data[row][i+1] )) {

				data[row][i]++;
				finished = true;
			} else 
				i++;   
		}

		for ( int j = 0; j < i; j++)
			data[row][j] = j;

		return (data[row][i] <= maxAllowed);

	}
	/////////////////////////////////////////////////////////////////////
	private boolean acceptable(int data[][], int row){

		for(int r = 0; r < row; r++){

			if ( numCommonElms( data, r, row) != 1)
				return false;
		}
		/* We could apply a maximum allowed number of repeats
    	for ( int i = 0; i < data[row].length; i++){
    		if  (countRepeats(data, data[row][i],  row) > numAllowedRepeats) {
    			//System.out.println("Had too many repeats in row: " + Integer.toString(row));
    			return false;
    		}    		
    	}
		 */
		// if all the elems of the row are different 
		// then numCommonElms will return numSides
		return (numCommonElms(data, row, row) == numSides);
	}
	//////////////////////////////////////////////////////////////////////
	public int countRepeats(int data[][], int x, int row){

		int repeats = 0;

		for(int r = 0; r < row; r++){
			for(int c = 0; c < data[0].length; c++){
				if ( data[r][c] == x)
					repeats++;
			}
		}
		// System.out.println(Integer.toString(repeats) + " ");

		return repeats;
	}

	//////////////////////////////////////////////////////////////////////
	private int numCommonElms(int data[][], int r1, int r2){

		int counter = 0;

		for(     int i = 0; i < data[r1].length; i++){
			for( int j = 0; j < data[r2].length; j++){

				if (data[r1][i] == data[r2][j])
					counter++;
			}
		}

		return counter;
	}    
	/////////////////////////////////////////////////////////////////////
	private void printRow(int data[][], int row, boolean printRowNumber){
		String str = "";

		if (printRowNumber)
			str = Integer.toString(row) + ":";

		for ( int i = 0; i < data[row].length; i++)
			str += " " + Integer.toString(data[row][i]);

		System.out.println(str);
	}
	///////////////////////////////////////////////
	private void printData(int data[][], boolean printRowNumbers){
		for ( int r = 0; r < data.length; r++)
			printRow(data,r, printRowNumbers);
	}    
	/////////////////////////////////////////////////////////////////////    
	// The following method only reports progress to the user.
	// It doesn't progress the algorithm any closer to the solution.
	private void reportRollBack(int data[][], int row){

		numRollBacks++;
		int numRBBetweenReports = 1000;
		int numIntsPerLine      = 20;

		if ( row < minRollBack)   
			minRollBack = row;

		if ( row > maxRollBack) {
			maxRollBack = row;

			System.out.println("");
			System.out.println(   "Going back to row: " + Integer.toString(row)
					+ ", min rollback: "    + Integer.toString(minRollBack)
					+ ", max rollback: "    + Integer.toString(maxRollBack));

			printData(data, true);
			System.out.println("");
		} else if (numRollBacks % numRBBetweenReports == 0){
			System.out.print(Integer.toString(row) + " ");

			if ( numRollBacks % (numRBBetweenReports * numIntsPerLine) == 0)
				System.out.println("");
		}
	}
	//////////////////////////////////////////////////////////////////
	class SortableRow implements Comparable<SortableRow>{
		private int[] m_row;

		public SortableRow(int[] row){
			m_row = row;
		}    	
		///////////////////////////////////////////////////////
		public int get(int i){
			return m_row[i];
		}    	
		///////////////////////////////////////////////////////
		public int compareTo(SortableRow row){
            // We assume this.m_row.length == row.m_row.length
			
			int i = 0;

			// we seek the first non equal elements:
			while ( (i < m_row.length) && (m_row[i] == row.get(i))){
				i++;
			}
			
			if ( i == m_row.length)
				return 0;  // row and this are deemed to be equal
			else
				return (m_row[i] - row.get(i));
		}
		///////////////////////////////////////////////////////
	}
	//////////////////////////////////////////////////////////
}
