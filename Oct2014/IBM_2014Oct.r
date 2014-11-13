# The following code is used to solve the IBM Oct 2014
# Ponder This puzzle.
#    http://domino.research.ibm.com/Comm/wwwr_ponder.nsf/Challenges/October2014.html
# find the last 10 digits of:
#  2^(3^(4^(5^(6^(7^(8^9))))))
# To load the code:
#    source("~/dev/R/scripts/IBM_2014Oct.r")

# Suppose we have a vector N[.]
# with N[8] = 8^9
# N[7] = 7^N[8]
# N[6] = 6 ^ N[7]
# N[5] = 5 ^ N[6]
# N[4] = 4 ^ N[5]
# N[3] = 3 ^ N[4]
# N[2] = 2 ^ N[3]

#Note that:
#  N[2] mod (10^10)  = 2^N[3] mod ( 10^10)
# = 2^{N[3] mod (4 * 5^9)} mod (10^10)

# and
# N[3] mod ( 4 * 5 ^ 9) = 3 ^   N[4]                mod ( 4 * 5 ^ 9)
#                       = 3 ^ { N[4] mod (4 * 5^8)} mod ( 4 * 5 ^ 9)

# and
# N[4] mod (4 * 5^8)  = ( 4 ^  N[5])                mod ( 4 * 5 ^ 8)
#                     =   4 ^ {N[5] mod (2 * 5^7 )} mod ( 4 * 5 ^ 8)
# and
# N[5] mod ( 2 * 5^7) = 5 ^ N[6] mod  ( 2 * 5^7)
#                     = 78125  
# note: (5^x) mod ( 2*5^7) = 78125, for positive integers x>=7

# so
# N[4] mod ( 4 * 5 ^ 8) = 390624 = basePowerMod(4,78125, 4*5^8)
# N[3] mod ( 4 * 5 ^ 9) = 5765981 = basePowerMod(3, 390624, 4*5^9)
# N[2] mod 10^10        = 8,170,340,352

calc <- function(){
  
  # We have a vector of quotients, initialized to zero
  Q    <- rep(0,5)  
  Q[2] <- 10^10               
  Q[3] <- findRepeat(2, Q[2]) # Q[3] = 4 * 5^9
  Q[4] <- findRepeat(3, Q[3]) # Q[4] = 4 * 5^8
  Q[5] <- findRepeat(4, Q[4]) # Q[5] = 2 * 5^7
  
  # print(Q)
  
  # perhaps should find out how many initial steps it takes before the repeating begins
  # for example if we look at 2^n mod 100,
  # for positive integers n, the following holds true for many x:  
  #     (2^(x+ n * 20)) mod 100 = (2^x) mod 100
  # but not when x = 1, 
  # since 2^ 1 mod 100 =  2 
  # and   2^21 mod 100 = 52
  # 
  N    <- rep(0,5) # initialize with zeros.
  N[5] <- basePowerMod( 5, 10,   Q[5])  # 5^10 mod Q[5]
  N[4] <- basePowerMod( 4, N[5], Q[4]) 
  N[3] <- basePowerMod( 3, N[4], Q[3]) 
  N[2] <- basePowerMod( 2, N[3], Q[2])  
  
  print(paste("2^3^4^5^6^7^8^9 mod 10^10 = ", N[2]))
  
  return (N[2])
}

# If we examine the series (b^i mod m)
# for a set of sequential integers i,
# we'll find that it eventually repeats.
# The following function will find out how long it takes to repeat.
findRepeat <- function(b = 3, m = 4 * 5^4) {
  
  previous   <-  1  
  currentMax <- -1
  i          <-  1
  finished   <-  FALSE
  
  # we deliberately don't use a vector here, because when we did, we ran out of memory!
  # m can be very large, for example 10^10
  while( ! finished){
      current <- (b * previous) %% m 
      if ( current > currentMax){
        currentMax <- current
        maxIdx1    <- i
        maxIdx2    <- 0
      } else if (( current == currentMax) && (maxIdx2 == 0)){
        maxIdx2    <- i
        finished   <- TRUE
      }
      
      i <- i+1
      previous     <- current

      if ( i > m)
        finished <- TRUE
  }
  
  if ( maxIdx2 != 0)
    return (maxIdx2 - maxIdx1)
  else {
    print(paste( "Did not find repeat with: b= ", b, ", m=", m))
    return ("did not find repeat")
  }  
}

# the following function will return:
# (b ^ p) mod m
# it uses a for loop to avoid an overflow.
basePowerMod <- function ( b, p, m){ 
  x <- 1
  finished <- FALSE
  j <- 1
  while (! finished){
  # formerly had: for( j in 1:p), but that could run out of memory 
    x <- (b * x) %% m
    
    if( j >= p)
      finished <- TRUE
    else
       j <- j+1
  }
  return (x)
}

# could use:  sapply( 1:100, base2Mod100)
base2Mod100 <- function (i){
   return (basePowerMod(2,i,100))
}

base3Mod1000 <- function (i){
  return (basePowerMod(3,i,1000))
}
