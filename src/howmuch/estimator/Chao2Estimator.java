package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.StatRes;

/**
 * Chao's Estimator #2.
 * <p> refer to [CandolfiSastri2004]
 * @author hedong
 *
 */
@AnEstimator
public class Chao2Estimator extends BaseEstimator{
	public Chao2Estimator(){
		super("Chao2");
	}

	public void estimate(StatRes res){
		successful=false;
		int n1=res.getFreqFreq(1);
		int n2=res.getFreqFreq(2);
		res.setW(Math.round(1.0*res.getNumOfObservedUnits()+1.0*n1*n1/(2*n2+2)-1.0*n1*n2/(2*(n2+1)*(n2+1))));
		res.setU(res.getW()-res.getNumOfObservedUnits());

		/**
		 * The coverage and the probability of new unit class are the consequent results of W/U.
		 */
		res.setC(1.0*res.getNumOfObservedUnits()/res.getW());
		res.setN(1.0-res.getC());

		successful=true;
	}
}
