package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.StatRes;

/**
 * The fourth order Jackknife estimator for the number of all possible trace classes.
 * <p> refer to p.928 of [burhanm1979]
 * @author hedong
 *
 */
@AnEstimator

public class JK4Estimator extends BaseEstimator{
	public JK4Estimator(){
		super("JK4");
	}
	public void estimate(StatRes res){
		successful=false;
		double t=res.getLogLength();
		double part1=(4.0-10.0/t)*res.getFreqFreq(1);
		double part2=(6*t*t-36*t+55)/(t*(t-1))*res.getFreqFreq(2);
		double part3=(4*t*t*t-42*t*t+148*t-175)/(t*(t-1)*(t-2))*res.getFreqFreq(3);
		double part4=(t-4)*(t-4)*(t-4)*(t-4)/(t*(t-1)*(t-2)*(t-3))*res.getFreqFreq(4);
		
		res.setU(Math.round(part1-part2+part3-part4));
		res.setW(res.getNumOfObservedUnits()+res.getU());
		
		/**
		 * The coverage and the probability of new unit class are the consequent results of W/U.
		 */
		res.setC(1.0*res.getNumOfObservedUnits()/res.getW());
		res.setN(1.0-res.getC());

		successful=true;
	}
}
