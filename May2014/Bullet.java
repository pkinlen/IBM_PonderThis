import java.util.Random;


class Bullet{
		
	double   m_startTime;
	double   m_speed;
	boolean  m_hasCollided;

	Bullet( double startTime, Random randGen){
			m_startTime   = startTime;
			m_speed       = randGen.nextDouble();	
			m_hasCollided = false;
	}
	/////////////////////////////////////////////////////////////////
	double  getSpeed()     { return m_speed;       }
	double  getStartTime() { return m_startTime;   }
	boolean hasCollided()  { return m_hasCollided; }
	
	void    setHasCollided(boolean hasCollided) { m_hasCollided = hasCollided; }
	
	/////////////////////////////////////////////////////////////////
	// will return a negative result if the bullets don't collide.
	double  timeOfCollision(Bullet bullet){
		
	   if ( m_speed == bullet.getSpeed())	
		   return -1.0D;	// the bullets don't collide
	   else {
		   double collisionTime  = ( m_startTime * m_speed - bullet.getStartTime() * bullet.getSpeed()) 
				         / ( m_speed - bullet.getSpeed() );
		   
		   if (   ( collisionTime > m_startTime )
               && ( collisionTime > bullet.getStartTime()))
               
               return collisionTime;
		   else 
			   return -1.0D; // indicating no collision occurred.
	   }
	}	
	/////////////////////////////////////////////////////////////////
	// Distance covered assuming no collision
	double distanceCovered( double time){
		if ( time < m_startTime)
			return 0.0D;
		else 
			return (( time - m_startTime) * m_speed);
	}
}
