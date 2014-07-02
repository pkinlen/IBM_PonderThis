import java.util.Arrays;
import java.util.Random;

// The following class is written to solve a puzzle posted by IBM:
//     http://domino.research.ibm.com/Comm/wwwr_ponder.nsf/Challenges/May2014.html
//
public class CollidingBullets  implements Simulator{

	int      m_numBullets;
	int      m_numOdd;
	double   m_collisionTimes[][];
	Bullet   m_evens[];
	Bullet   m_odds[];
	
	CollidingBullets(){
		// System.out.println("Will work out the prob that all the bullets are annihilated.");
		
		m_numBullets = 20; // must be even
		m_numOdd     = m_numBullets / 2;
		
		m_collisionTimes = new double [m_numOdd][m_numOdd];	
		
    	m_evens = new Bullet[m_numOdd];
    	m_odds  = new Bullet[m_numOdd];
	}
    //////////////////////////////////////////////////////////////////
	private Collision[] getCollisions(){
		 Collision collisions[] = new Collision[m_numOdd * m_numOdd];
				
         for    ( int odd  = 0;  odd  < m_numOdd; odd++)  {
        	 for( int even = 0;  even < m_numOdd; even++) {

        		 double colTime = m_odds[odd].timeOfCollision(m_evens[even]);

        		 // System.out.println("Found col time: " + Double.toString(colTime));
        		 
        		 collisions[odd * m_numOdd + even] = new Collision(even, odd, colTime);
        	 }
         }
         
         return collisions;
	}
    //////////////////////////////////////////////////////////////////
	void 	initializeOddsAndEvenBullets(Random randGen){

		for ( int i = 0; i < m_numOdd; i++){
			m_evens[i] = new Bullet(2.0D * i,        randGen);
			m_odds[i]  = new Bullet(2.0D * i + 1.0D, randGen);
		}
	}
    /////////////////////////////////////////////////////////////////
	int getNumConfirmedCollisions(Collision[] arrCollisions){
		int numOfCollisions = 0;
		
		for (int i = 0; i < arrCollisions.length; i++){
			//System.out.println("collision time: " + Double.toString(arrCollisions[i].getTime()));
			if ( arrCollisions[i].isValid()){
				Bullet even = m_evens[arrCollisions[i].getEvenIdx()];
				Bullet odd  = m_odds [arrCollisions[i].getOddIdx() ];

				if ( ! even.hasCollided() && ! odd.hasCollided()){
					even.setHasCollided(true);
					odd.setHasCollided(true);
					numOfCollisions++;
				}
			}
		}
		return numOfCollisions;
	}
    /////////////////////////////////////////////////////////////////	
	/*
	int countConfirmedCollisions(){
		int counter = 0;
		
		for (int i = 0; i < m_numOdd; i++){
			if (m_evens[i].hasCollided())
				counter++;
			
			if (m_odds[i].hasCollided())
				counter++;
			
		}
		
		return counter++;
	}
	*/
	/////////////////////////////////////////////////////////////////
	// returns 1 when there is at least one surviver.
    public double runOneSim(Random randGen){
    	
    	initializeOddsAndEvenBullets(randGen);
    	Collision arrCollisions[] = getCollisions();
    	Arrays.sort(arrCollisions);

    	int numConfirmedCollisions = getNumConfirmedCollisions(arrCollisions);

    	//System.out.println("num of collisions:" + Integer.toString(numConfirmedCollisions));
    	
    	if ( numConfirmedCollisions < m_numOdd)
    		return 0.0D; // we have at least one survivor
    	else 
    		return 1.0D; // all annihilated    	
    }
    ////////////////////////////////////////////////////////
    int countPositiveElements(double[][] mat){
    	
    	int counter = 0;
    	
    	for( int r = 0; r < mat.length; r++){
    		for ( int c = 0; c < mat[0].length; c++) {
    			if ( mat[r][c] >= 0) 
    				counter++;
    		}
    	}
    	
    	return counter;
    }
}
