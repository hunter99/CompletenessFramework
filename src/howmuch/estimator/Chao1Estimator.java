package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.StatRes;

/**
 * Chao's Estimator #1.
 * <p> refer to [CandolfiSastri2004]
 * @author hedong
 *
 */
@AnEstimator
public class Chao1Estimator extends BaseEstimator{
	public Chao1Estimator(){
		super("Chao1");
	}

	public void estimate(StatRes res){
		successful=false;
		if(res.getFreqFreq(2)==0) return;
		res.setW(Math.round(res.getNumOfObservedUnits()+res.getFreqFreq(1)*res.getFreqFreq(1)/(2*res.getFreqFreq(2))));
		res.setU(res.getW()-res.getNumOfObservedUnits());
		
		/**
		 * The coverage and the probability of new unit class are the consequent results of W/U.
		 */
		res.setC(1.0*res.getNumOfObservedUnits()/res.getW());
		res.setN(1.0-res.getC());
		successful=true;
	}
}
