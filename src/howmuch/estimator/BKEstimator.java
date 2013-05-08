package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.StatRes;

/**
 * Boender&Rinnooy Kan's Bayesian Estimator.
 * <p> refer to [BoenderRinnooyKan1983]
 * @author hedong
 *
 */
@AnEstimator
public class BKEstimator extends BaseEstimator{
	public BKEstimator(){
		super("BRK");
	}

	public void estimate(StatRes res){
		successful=false;
		int n=res.getLogLength();
		int w=res.getNumOfObservedUnits();
		if(n>=w+3){
			res.setW((long)(1.0*w*(n-1)/(n-w-2)));
			res.setU(res.getW()-res.getNumOfObservedUnits());
		}
		if(n>=w+2){
			res.setN(1.0*w*(w+1)/(n*(n-1)));
			res.setC(1-res.getN());
		}
		successful=true;
	}
}
