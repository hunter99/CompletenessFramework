package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.StatRes;

/**
 * The second order Jackknife estimator for the number of all possible trace classes.
 * <p> refer to p.928 of [burhanm1979]
 * @author hedong
 *
 */
@AnEstimator
public class JK2Estimator extends BaseEstimator{
	public  JK2Estimator(){
		super("JK2");
	}
	public void estimate(StatRes res){
		successful=false;
		double t=res.getLogLength();
		double part1=(2.0-3.0/t)*res.getFreqFreq(1);
		double part2=(t-2)*(t-2)/(t*(t-1))*res.getFreqFreq(2);
		
		res.setU(Math.round(part1-part2));
		res.setW(res.getNumOfObservedUnits()+res.getU());
		
		/**
		 * The coverage and the probability of new unit class are the consequent results of W/U.
		 */
		res.setC(1.0*res.getNumOfObservedUnits()/res.getW());
		res.setN(1.0-res.getC());

		successful=true;
	}
}
