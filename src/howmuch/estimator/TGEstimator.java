package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.StatRes;

/*
 * Turing-Good estimation.
 * <p> refer to [CandolfiSastri2004]
 */
@AnEstimator
public class TGEstimator extends BaseEstimator{
	public TGEstimator(){
		super("TG");
	}
	public void estimate(StatRes res){
		successful=false;
		res.setC(1.0-res.getFreqFreq(1)*1.0/res.getLogLength());
		res.setN(1.0-res.getC());
		res.setW(Math.round(res.getNumOfObservedUnits()/res.getC()));//valid only when trace classes being equal-probability.
		res.setU(res.getW()-res.getNumOfObservedUnits());
		successful=true;
	}
}