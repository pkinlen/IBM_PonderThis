
# The following code is used to solve the IBM September 2014
# Ponder This puzzle.
#    http://domino.research.ibm.com/Comm/wwwr_ponder.nsf/Challenges/September2014.html
# Let b = (2^(3^(4^5))) / (e^n), where n is an integer such that
# 1 < b < e.
# Find b, with an accuracy of 10 decimal digits.

# To load the code:
#    source("~/dev/R/scripts/IBM_2014Sep.r")

require("Rmpfr")
# may first need to:     install.packages("Rmpfr", dependencies=T)
# that in turn may require you to install other libraries 
# At a Unix terminal had typed:
#   sudo apt-get install libmpfr-dev
#   sudo apt-get install libmpfr4
#   sudo apt-get install build-essential

solveIBMSep2014 <- function(bits = 1700, doPrintBits = FALSE){
  
     two        <- mpfr( 2, precBits = bits)
     three      <- mpfr( 3, precBits = bits)
     
     ln2        <- log( two)   # ln2 will now be an mpfr object, 
                               # rather than a standard numeric
     twoP10     <- two ^ 10    # = 1024
     threeP1024 <- three ^ twoP10
     
     ln2_3P1024 <- ln2 * threeP1024
     
     N          <- floor( ln2_3P1024 )
     lnB        <- ln2_3P1024 - N
     B          <- exp(lnB)
     
     numB       <- as.numeric(B)     
     msg        <- paste("Found b = ", format(numB, digits=10), sep="")
     
     if ( doPrintBits ) {
       msg      <- paste( msg, ", using ", as.character(bits), 
                          " bits for each float.", sep="")
     }

     print  ( msg  )
     return ( numB ) 
}

recalcWithRangeOfBits <- function( from=1600, to = 1700, step=10){
  bits  <- seq(from, to, by=step)
  sapply(bits, solveIBMSep2014, T)
}
