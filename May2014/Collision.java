
public class Collision implements Comparable<Collision>{
      int     m_evenIdx;
      int     m_oddIdx;
      double  m_time;
      
      Collision(int evenIdx, int oddIdx, double time){
    	  m_evenIdx     = evenIdx;
    	  m_oddIdx      = oddIdx;
    	  m_time        = time;
      }
      
      int     getEvenIdx()   { return m_evenIdx;    }
      int     getOddIdx()    { return m_oddIdx;     }
      double  getTime()      { return m_time;       }
      boolean isValid()      { return (m_time > 0); }

	@Override
	public int compareTo(Collision collision) {

		if ( ! isValid() && ! collision.isValid())
			return 0;
		else if ( ! isValid() )
			return +1;
		else if ( ! collision.isValid() )
			return -1;
		else if ( m_time == collision.getTime())
			return 0;
		else if ( m_time < collision.getTime())
			return -1;
		else
			return +1;
	}
}
