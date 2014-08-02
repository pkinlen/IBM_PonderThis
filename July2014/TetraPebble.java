import java.util.TreeMap;

// Here is some java code that aims to solve the IBM Ponder This July 2014 puzzle:
// http://domino.research.ibm.com/Comm/wwwr_ponder.nsf/Challenges/July2014.html
// Author: Philip Kinlen
// July 2014

public class TetraPebble {

	private static       Face[]    m_faces;
	private static       int[]     m_initialPebbles;
	private static       int[][]   m_pairs;
	private static final int       m_numEdges = 6;
	private static final int       m_numFaces = 4;
	private static       long      m_trial;	
	private              int[][]   m_solutions;       
	private static       long      m_startTimeMilliSec;
	
	private TreeMap<KnownState, Result>    m_knownAStates;
	private TreeMap<KnownState, Result>    m_knownBStates;
    	
    ////////////////////////////////////////////////////
	public static void main(String[] args) {
		m_startTimeMilliSec = System.currentTimeMillis();
		TetraPebble tb = new TetraPebble();
		System.out.println("Starting to look for solution to the pebbles on a tetrahedron puzzle");
		tb.calc();
		Long runTimeMS = System.currentTimeMillis() - m_startTimeMilliSec;
		System.out.println("\nFinished, with run-time of " 
		                   + Double.toString( runTimeMS * 0.001) 
		                   + " seconds.");
	}
	////////////////////////////////////////////////////
	public void calc(){
		Turn ATurn = new Turn( m_initialPebbles);
		int numSolutions = 0;
		
		boolean finished = false;
		
		while ( ! finished) {
			int pebbles[] = ATurn.getNext();
			
			m_trial++;
			printPebbles(Long.toString(m_trial)        + ", first move: ", pebbles, true);
			printPebbles(Long.toString(m_trial)        + ", have:       ", pebbles, false);
			
			System.out.println(     "Num A stored: "   + Integer.toString(m_knownAStates.size())
					            + ", Num B stored: "   + Integer.toString(m_knownBStates.size()));
			
			if (pebbles == null)
				finished = true;
			
			else if  ( AllBsMovesLeadToAWin(pebbles, 1) ) {
				printPebbles("Found winning solution ", pebbles, true);
				m_solutions[numSolutions] = deepCopy(pebbles);
				numSolutions++;							
			} 			
		} 
	
		System.out.println("\nWith " + Long.toString(m_trial-1) 
		                   + " trials, total number of solutions found: " + Integer.toString(numSolutions));
		
		for( int i = 0; i < numSolutions; i++)
			printPebbles("", m_solutions[i], true);
	}
	////////////////////////////////////////////////////
	public TetraPebble(){
		m_initialPebbles = new int[]{1,2,3,5,8,13};
        //m_initialPebbles = new int[]{1,1,0,0,0,0};
		//m_initialPebbles = new int[]{2,2,0,0,0,2}; // initial state: all on one face, for debugging

		m_solutions      = new int[1000][];		// looks like there are 762 possible opening moves.
		m_trial          = 0;
		
		//m_turnCounter    = 0;
		m_knownAStates = new TreeMap<KnownState, Result>();
		m_knownBStates = new TreeMap<KnownState, Result>();

		setPairs();
		setFaces();
	}
	///////////////////////////////////////////////////////
	public void addResult(int[] pebbles, Result res, boolean useA){
		KnownState knownState = new KnownState(pebbles);
		
		TreeMap<KnownState, Result> stored = ( useA ? m_knownAStates : m_knownBStates);
		
		Result existingRes = stored.get(knownState);
		
		if ( existingRes == null)
			stored.put(knownState, res);	
		else if ( existingRes != res){
			printPebbles("Got conflicting results: " + res.toString() + ", " 
					     + existingRes.toString(), pebbles, false);
		}
	}
	////////////////////////////////////////////////////////////
	public Result findResult(int[] pebbles, boolean useA){
		KnownState knownState = new KnownState(pebbles);
		TreeMap<KnownState, Result> stored = ( useA ? m_knownAStates : m_knownBStates);
		
		Result res = stored.get(knownState);
		
		if ( res == null)
			return Result.UNKNOWN;
		else 
			return res;
	}
    ///////////////////////////////////////////////////////
	public void setFaces(){
		m_faces = new Face[m_numFaces];

		int i = 0;		
		m_faces[i]  = new Face( new int[]{0,1,5}); i++;
		m_faces[i]  = new Face( new int[]{0,2,3}); i++; 
		m_faces[i]  = new Face( new int[]{1,3,4}); i++;
		m_faces[i]  = new Face( new int[]{2,4,5}); i++;			
	}
	/////////////////////////////////////////////////////////
	
	private void setPairs(){
		m_pairs = new int[12][];
		
		int i = 0;
		
		m_pairs[i] = new int[] {0, 1} ; i++;  // face 0
		m_pairs[i] = new int[] {0, 5} ; i++;  // face 0
		m_pairs[i] = new int[] {1, 5} ; i++;  // face 0
		
		m_pairs[i] = new int[] {0, 2} ; i++;  // face 1
		m_pairs[i] = new int[] {0, 3} ; i++;  // face 1
		m_pairs[i] = new int[] {2, 3} ; i++;  // face 1
		
		m_pairs[i] = new int[] {1, 3} ; i++;  // face 2
		m_pairs[i] = new int[] {1, 4} ; i++;  // face 2
		m_pairs[i] = new int[] {3, 4} ; i++;  // face 2
		
		m_pairs[i] = new int[] {2, 4} ; i++;  // face 3
		m_pairs[i] = new int[] {2, 5} ; i++;  // face 3
		m_pairs[i] = new int[] {4, 5} ; i++;  // face 3
	}
	/////////////////////////////////////////////////////////////////////
	// we are assuming that it is  now B's turn
	private boolean AllBsMovesLeadToAWin(int[] pebbles, long turnNumber){

		if ( countNonZeros(pebbles) == 0 )
			return true;
		
		Turn BTurn = new Turn( pebbles);

		boolean finished = false;
		
		while ( ! finished) {
			int afterBsTurn[] = BTurn.getNext();
						
			if ( afterBsTurn == null)
				return true; // we know B has had at least one valid move since sum(pebbles) != 0
			else {
				Result existingResult = findResult(afterBsTurn, true);
				if ( existingResult == Result.CANNOT_FORCE_WIN)
				   return false;
				else if ( existingResult != Result.CAN_FORCE_WIN){
				    if ( ACanForceWin(afterBsTurn, turnNumber+1)){	
					   addResult(afterBsTurn, Result.CAN_FORCE_WIN, true);
					   // we continue and try B's next move.
				    } else {
					   addResult(afterBsTurn, Result.CANNOT_FORCE_WIN, true);
					   return false; // in this case we've found a move for B, 
					                 // that result in B winning
				    }
				}

			}
		} 
		return false; // won't ever get to this line.
	}
	////////////////////////////////////////////////
	public boolean ACanForceWin(int[] pebbles, long turnNumber){
		
		int numOfNonZeros = countNonZeros(pebbles);
		
		if ( numOfNonZeros == 0 )
			return false;
		
		Turn    ATurn    = new Turn( pebbles);
		boolean finished = false;
		
		while ( ! finished) {
			int afterAsTurn[] = ATurn.getNext();
						
			if ( afterAsTurn == null)
				return false; // we haven't found a turn for A in which he can force a win.
			else  {
				Result existingResult = findResult(afterAsTurn, false);
				if ( existingResult == Result.ALWAYS_LOSES)
					return true;
				else if ( existingResult != Result.CAN_EITHER_WIN_OR_LOSE){
					if (AllBsMovesLeadToAWin(afterAsTurn, turnNumber + 1)) {
						addResult(afterAsTurn, Result.ALWAYS_LOSES, false);
						return true;
					} else {
						addResult(afterAsTurn, Result.CAN_EITHER_WIN_OR_LOSE, false );
					}
				} 
			}
		} 
		return false; // won't ever get to this line.
	}
    ////////////////////////////////////////////////
    public int countNonZeros(int[] arr){
    	int counter = 0;
    	for(int i = 0; i< arr.length; i++)
    		if ( arr[i] != 0)
    			counter++;
    	
    	return counter;
    }
	///////////////////////////////////////////////
	private int sum(int[] arr){
		int res = 0;
		for ( int i = 0; i < arr.length; i++)
			res += arr[i];
		
		return res;
	}
	////////////////////////////////////////////////////////
	public static int[] deepCopy( int[] arr){
		int[] res = new int[arr.length];
		for ( int i = 0; i < arr.length; i++)
			res[i] = arr[i];
		
		return res;
	}	
	////////////////////////////////////////////////////
	public void printPebbles(String preamble, int[] pebbles, boolean compareInit){

		String str = preamble;
		
		if ( pebbles == null)
			str += "null";		
		else if ( compareInit) {
			str += Integer.toString(m_initialPebbles[0] - pebbles[0]);;
		
			for( int i = 1 ; i < pebbles.length; i++)
				str += ", " + Integer.toString(m_initialPebbles[i] - pebbles[i]);
		
		} else {
			
			str += Integer.toString(pebbles[0]);;
			
			for( int i = 1 ; i < pebbles.length; i++)
				str += ", " + Integer.toString(pebbles[i]);

		}
		System.out.println(str);
	}
	/////////////////////////////////////////////////////////
	public class Turn {
		int[] m_startPebbles;		
		int[] m_curPebbles;
		
		int   m_edge;
		int   m_face;
		int   m_pair;
		int   m_numEdges;
		int[] m_removedPebbles; 
		
		///////////////////////////////////////////////////////////////
		Turn( int[] startPebbles){
			m_startPebbles     = TetraPebble.deepCopy(startPebbles);
			m_edge             = 0;
			m_face             = 0;
			m_numEdges         = 3;
			m_removedPebbles   = new int[]{1,1,1};
		}
		///////////////////////////////////////////////////////////////
		// will return null when there are no more available turns
		int[] getNext(){
			if (sum(m_startPebbles) == 0)
				return null;
			else if (canRemovePebblesFromThreeEdges())
			    return m_curPebbles;
			else if (canRemovePebblesFromTwoEdges())
				return m_curPebbles;
			else if (canRemovePebblesFromOneEdge())
				return m_curPebbles;
			else
				return null;
		}
        //////////////////////////////////////////////////////////////////////		
		private boolean canRemovePebblesFromOneEdge(){
						
			if ( m_numEdges != 1)
				return false;
			else {
				if ( m_removedPebbles[0]  <= m_startPebbles[m_edge]) {
					 m_curPebbles          = TetraPebble.deepCopy(m_startPebbles);
					 m_curPebbles[m_edge] -= m_removedPebbles[0];
				     m_removedPebbles[0]++;
				     return true;
				} else if( findNextNonEmptyEdge()){
					 m_curPebbles        = TetraPebble.deepCopy(m_startPebbles);
				     m_curPebbles[m_edge]--;
					 m_removedPebbles[0] = 2;
					 return true;
				} else {
					 m_removedPebbles[0] = 1;
					 m_removedPebbles[1] = 1;
					 m_removedPebbles[2] = 1;
					 m_edge              = 0;
					 m_numEdges--; // we should now try pairs
					 
					 return false;
				}
			}
		}
		//////////////////////////////////////////////////////////////
		private boolean findNextNonEmptyEdge(){
			boolean finished = false;
			while (! finished) {
				m_edge++;
				if ( m_edge >= TetraPebble.m_numEdges)
					return false;
				else if ( m_startPebbles[m_edge] > 0)
					return true;
				// else 
				//    go round the while loop again								
			}
			return false; // won't ever get to this line.
		}
        //////////////////////////////////////////////////////////////////
		public boolean canRemovePebblesFromTwoEdges(){
			if ( m_numEdges != 2)
				return false;
			
			else if( findNextValidPair()){
				
				 m_curPebbles = TetraPebble.deepCopy(m_startPebbles);
				 m_curPebbles[TetraPebble.m_pairs[m_pair][0]] -= m_removedPebbles[0];	
				 m_curPebbles[TetraPebble.m_pairs[m_pair][1]] -= m_removedPebbles[1];
				 removeExtraOneFromPair();
				 
				 return true;
				 
			} else {
				m_numEdges--;
				m_removedPebbles[0] = 1;
				m_removedPebbles[1] = 1;
				m_removedPebbles[2] = 1;
				
				return false;
			}
		}
        //////////////////////////////////////////////////////////////////	
		public boolean findNextValidPair(){
			boolean finished = false;
			
			while ( ! finished){
				if ( canRemoveFromCurrentPair())
					return true;
				else {
					m_pair++;
					m_removedPebbles[0] = 1;
					m_removedPebbles[1] = 1;
					
					if ( m_pair >= TetraPebble.m_pairs.length)
						return false;
				}	
			}
			return false; // won't ever get to this line
		}
		//////////////////////////////////////////////////////////////////////
		public boolean canRemoveFromCurrentPair(){
			return (    m_startPebbles[TetraPebble.m_pairs[m_pair][0]] >= m_removedPebbles[0]	
			        &&  m_startPebbles[TetraPebble.m_pairs[m_pair][1]] >= m_removedPebbles[1] );
		}
        ///////////////////////////////////////////////////////////////////////		
		public void removeExtraOneFromPair(){
			if (m_removedPebbles[1] >= m_startPebbles[TetraPebble.m_pairs[m_pair][1]]  ){
				m_removedPebbles[0]++;				 
			    m_removedPebbles[1] = 1;
			} else {
				m_removedPebbles[1]++;
			}
		}
        //////////////////////////////////////////////////////////////////
		public boolean canRemovePebblesFromThreeEdges(){
			if ( m_numEdges != 3)
				return false;
			
			else if( findNextValidFace()){
				
				 m_curPebbles = TetraPebble.deepCopy(m_startPebbles);
				 m_curPebbles[TetraPebble.m_faces[m_face].getIndex(0)] -= m_removedPebbles[0];	
				 m_curPebbles[TetraPebble.m_faces[m_face].getIndex(1)] -= m_removedPebbles[1];	
				 m_curPebbles[TetraPebble.m_faces[m_face].getIndex(2)] -= m_removedPebbles[2];	
				 
				 removeExtraOneFromFace();
				 
				 return true;
				 
			} else {
				m_numEdges--;
				m_removedPebbles[0] = 1;
				m_removedPebbles[1] = 1;
				m_removedPebbles[2] = 1;
				
				return false;
			}
		}
        ////////////////////////////////////////////////////////////////////
		public void removeExtraOneFromFace(){
			if (m_startPebbles[TetraPebble.m_faces[m_face].getIndex(2)] > m_removedPebbles[2])
				m_removedPebbles[2]++;				 
			else if (m_startPebbles[TetraPebble.m_faces[m_face].getIndex(1)] > m_removedPebbles[1]){
				m_removedPebbles[1]++;
				m_removedPebbles[2] = 1;
			} else {
				m_removedPebbles[0]++;
				m_removedPebbles[1] = 1;
				m_removedPebbles[2] = 1;				
			}				
		}
        ///////////////////////////////////////////////////////////////////
		public boolean findNextValidFace(){
			boolean finished = false;
			
			while ( ! finished){
				if ( canRemoveFromCurrentFace())
					return true;
				else {
					m_face++;
					m_removedPebbles[0] = 1;
					m_removedPebbles[1] = 1;
					m_removedPebbles[2] = 1;
					
					if ( m_face >= TetraPebble.m_faces.length)
						return false;
				}	
			}
			return false; // won't ever get to this line			
		}
		/////////////////////////////////////////////////////////////////////		
		public boolean canRemoveFromCurrentFace(){
			return (    m_startPebbles[TetraPebble.m_faces[m_face].getIndex(0)] >= m_removedPebbles[0]	
			        &&  m_startPebbles[TetraPebble.m_faces[m_face].getIndex(1)] >= m_removedPebbles[1] 
				    &&  m_startPebbles[TetraPebble.m_faces[m_face].getIndex(2)] >= m_removedPebbles[2]); 
		}
	}
	//////////////////////////////////////////////////////////////////////
	public class Face {
		private int[] m_indices;
		
		///////////////////////////////////////////////////////////
		public Face( int[] indices){
			m_indices  = indices;
		}
		///////////////////////////////////////////////////////////		
		public int getIndex(int i){
			return m_indices[i];
		}
	}
	/////////////////////////////////////////////////////////
	public class KnownState implements Comparable<KnownState>{
		private int[]  m_pebbles;
		
		//////////////////////////////////////////////////////////
		public KnownState( int[] pebbles){
			m_pebbles = TetraPebble.deepCopy(pebbles);
		}
		//////////////////////////////////////////////////////////
		public int compareTo(KnownState that){
			
			for(int i = 0; i < m_pebbles.length; i++){
				
				int diff = m_pebbles[i] - that.get(i);
				
				if ( diff != 0)
					return diff;
			}
			return 0;
		}
		//////////////////////////////////////////////////////////
		public int get(int i){
			return m_pebbles[i];
		}
		//////////////////////////////////////////////////////////
	}
	/////////////////////////////////////////////////////////
	public enum Result {
		ALWAYS_LOSES,
		CAN_FORCE_WIN,
		CANNOT_FORCE_WIN,
		CAN_EITHER_WIN_OR_LOSE,
		UNKNOWN
	}
	/////////////////////////////////////////////////////////
}