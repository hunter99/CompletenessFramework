package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.StatRes;

/**
 * The first order Jackknife estimator for the number of all possible trace classes.
 * <p> refer to p.928 of [burhanm1979]
 * @author hedong
 *
 */
@AnEstimator
public class JK1Estimator extends BaseEstimator{
	public  JK1Estimator(){
		super("JK1");
	}
	public void estimate(StatRes res){
		successful=false;
		res.setU(Math.round((1-(double)1.0/res.getLogLength())*res.getFreqFreq(1)));
		res.setW(res.getNumOfObservedUnits()+res.getU());
		
		/**
		 * The coverage and the probability of new unit class are the consequent results of W/U.
		 */
		res.setC(1.0*res.getNumOfObservedUnits()/res.getW());
		res.setN(1.0-res.getC());

		successful=true;
	}
}

