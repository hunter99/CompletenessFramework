package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.StatRes;

/*
 * Maximum Likelihood Estimation.
 * <P>under the condition where probability of each trace class is equal to each other.
 * <p> refer to [CandolfiSastri2004]
 */
@AnEstimator
public class MLEstimator extends BaseEstimator{
	public MLEstimator(){
		super ("MLE");
	}

	public void estimate(StatRes res){
		successful=false;
		double res1,res2;
		int T=res.getNumOfObservedUnits();
		int L=res.getLogLength();
		if (T == L) {
			res.setW(-1);
			res.setU(-1);
			return;
		}

		res1=T*Math.exp(-res.getLogLength()*1.0/T);
		long counts=0;
		for(;;T++){
			counts++;
			if(counts>1000) break;
			res2=res.getNumOfObservedUnits()-T*(1-Math.exp(-L*1.0/T));
			if(res2<0){
				if(Math.abs(res2)>res1){
					T--;
				}
				break;
			}else{
				res1=res2;
			}
		}
		res.setW(T);
		res.setU(res.getW()-res.getNumOfObservedUnits());
		res.setC(res.getNumOfObservedUnits()*1.0/res.getW());
		res.setN(1-res.getC());
		successful=true;
	}
}
