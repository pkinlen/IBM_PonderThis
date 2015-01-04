///////////////////////////////////////////////////////////////////////////////   
public class State {
	public boolean[][] m_mat;
	public int         m_numRows;
	public int         m_numCols;
	public int         m_nextRow;
	public int         m_nextCol;
	
	public Main        m_main;
	public boolean     m_isAllowed;
	
    ///////////////////////////////////////////////////////////////////////////////       	
	public State(int numRows, int numCols, Main main) {
		m_numRows = numRows;
		m_numCols = numCols;
		m_nextRow = 0;
		m_nextCol = 0;
		m_mat     = new boolean[m_numRows][m_numCols];
		m_main    = main;
		checkIfAllowed(-1, numCols-1);
	}
    ///////////////////////////////////////////////////////////////////////////////       	
    public State( State that, boolean next, Main main){

    	m_numRows      = that.m_numRows;
    	m_numCols      = that.m_numCols;
    	
    	int currentRow = that.m_nextRow;
    	int currentCol = that.m_nextCol;
    	
    	if ( currentCol + 1 >= m_numCols){
    	    m_nextCol  = 0;
    	    m_nextRow  = currentRow + 1;
    	} else {
    		m_nextCol  = currentCol + 1;
    		m_nextRow  = currentRow;
    	}
		
    	m_mat = that.m_mat; // we actually don't need a deep copy!
    	        	
    	if(that.m_nextRow < m_numRows)
    		m_mat[currentRow][currentCol] = next;
    	
        m_main = main; 
    	checkIfAllowed(currentRow, currentCol);
    }
    //////////////////////////////////////////////////////////////////////////////
    public void checkIfAllowed(int currentRow, int currentCol){
    	if(currentRow < 0){
    	    m_isAllowed = true;
    	    return;
    	}
    	
    	int numTruesOnRow = countTruesOnRow(currentRow, currentCol);
    	
    	if ( numTruesOnRow > m_main.m_rowCounter[currentRow]  ){
    		m_isAllowed = false;
    	    return;
    	}
    	
    	int remainingCols = m_numCols - currentCol;
        if ((numTruesOnRow + remainingCols) < m_main.m_rowCounter[currentRow] ) {
        	m_isAllowed = false;
    	    return;
        }
    
    	int numTruesOnCol = countTruesOnCol(currentRow, currentCol);
    	
    	if ( numTruesOnCol > m_main.m_colCounter[currentCol]  ){
    		m_isAllowed = false;
    	    return;
    	}
    	
    	int remainingRows = m_numRows - currentRow - 1;
        if ((numTruesOnCol + remainingRows) < m_main.m_colCounter[currentCol] ) {
        	m_isAllowed = false;
    	    return;
        }

        m_isAllowed = true;
    }
    ///////////////////////////////////////////////////////////////////////////////
    public int countTruesOnCol( int currentRow, int currentCol){
    	int counter = 0;
    	for ( int r = 0; r <= currentRow; r++){
    		if (m_mat[r][currentCol])
    			counter++;
    	}
    	return counter;
    }
    ///////////////////////////////////////////////////////////////////////////////
    public int countTruesOnRow( int currentRow, int currentCol){
    	int counter = 0;
    	for ( int c = 0; c <= currentCol; c++){
    		if (m_mat[currentRow][c])
    			counter++;
    	}
    	return counter;
    }
    ///////////////////////////////////////////////////////////////////////////////       	
	public void setMat( boolean[][] mat){ // making a deep copy
	    
	    int r, c;
	    for (r = 0; r < m_nextRow; r++){
	    	
	    	for(c = 0; c < m_numCols; c++){
	    		m_mat[r][c] = mat[r][c];
	    	}
	    }
        
	    // The next row is incomplete
	    for ( c = 0; c < m_nextCol; c++){
	    	m_mat[m_nextRow][c] = mat[m_nextRow][c];    	    	
	    }    	
	}
	///////////////////////////////////////////////////////////////
    public boolean isAllowed(){
        return m_isAllowed;        	
    }
	///////////////////////////////////////////////////////////////
	public long getNumSolutions(){

		if ( ! isAllowed())
		   return 0L;
		else if ( m_nextRow == m_numRows)
		    return 1L;	
		else {
			long numSolnWhenNextIsFalse = nextNumSolutions(false);
			long numSolnWhenNextIsTrue  = nextNumSolutions(true);
			
			return(numSolnWhenNextIsFalse + numSolnWhenNextIsTrue);
		}
	}
	////////////////////////////////////////////////////////////////
    long nextNumSolutions(boolean nextElm){
    	State state = new State(this, nextElm, m_main);
    	return state.getNumSolutions();
    }

}   // end of class State