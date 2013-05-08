package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.StatRes;

/**
 * The fifth order Jackknife estimator for the number of all possible trace classes.
 * <p> refer to p. 928 of [burhanm1979]
 * @author hedong
 *
 */
@AnEstimator

public class JK5Estimator extends BaseEstimator{
	public  JK5Estimator(){
		super( "JK5");
	}
	public void estimate(StatRes res){
		successful=false;
		double t=res.getLogLength();
		double part1=(5.0-15.0/t)*res.getFreqFreq(1);
		double part2=(10*t*t-70*t+125)/(t*(t-1))*res.getFreqFreq(2);
		double part3=(10*t*t*t-120*t*t+485*t-660)/(t*(t-1)*(t-2))*res.getFreqFreq(3);
		double part4=(Math.pow(t-4,5)-Math.pow(t-5,5))/(t*(t-1)*(t-2)*(t-3))*res.getFreqFreq(4);
		double part5=Math.pow(t-5,5)/(t*(t-1)*(t-2)*(t-3)*(t-4))*res.getFreqFreq(5);
		
		res.setU(Math.round(part1-part2+part3-part4+part5));
		res.setW(res.getNumOfObservedUnits()+res.getU());
		
		/**
		 * The coverage and the probability of new unit class are the consequent results of W/U.
		 */
		res.setC(1.0*res.getNumOfObservedUnits()/res.getW());
		res.setN(1.0-res.getC());

		successful=true;
	}
}
