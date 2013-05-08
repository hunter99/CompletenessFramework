package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.StatRes;

/**
 * The third order Jackknife estimator for the number of all possible trace classes.
 * <p> refer to p.928 of [burhanm1979]
 * @author hedong
 *
 */
@AnEstimator

public class JK3Estimator extends BaseEstimator{
	public JK3Estimator(){
		super("JK3");
	}
	public void estimate(StatRes res){
		successful=false;
		double t=res.getLogLength();
		double part1=(3.0-6.0/t)*res.getFreqFreq(1);
		double part2=(3*t*t-15*t+19)/(t*(t-1))*res.getFreqFreq(2);
		double part3=(t-3)*(t-3)*(t-3)/(t*(t-1)*(t-2))*res.getFreqFreq(3);
		
		res.setU(Math.round(part1-part2+part3));
		res.setW(res.getNumOfObservedUnits()+res.getU());
		
		/**
		 * The coverage and the probability of new unit class are the consequent results of W/U.
		 */
		res.setC(1.0*res.getNumOfObservedUnits()/res.getW());
		res.setN(1.0-res.getC());

		successful=true;
	}
}
