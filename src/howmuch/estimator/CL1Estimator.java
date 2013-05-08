package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.StatRes;

/**
 * Chao & Lee's Estimator #1.
 * <p> refer to [CandolfiSastri2004]
 * @author hedong
 *
 */
@AnEstimator
public class CL1Estimator extends BaseEstimator{
	public CL1Estimator(){
		super("CL1");
	}

	public void estimate(StatRes res){
		int n1=res.getFreqFreq(1);
		int n=res.getLogLength();
		
		successful=false;
		double gamma2=0;
		for (int j=1;j<=n;j++){
			gamma2+=j*(j-1)*res.getFreqFreq(j);
		}
		gamma2=1.0*n*res.getNumOfObservedUnits()/((n-n1)*n*(n-1))*gamma2-1;
		if(gamma2<0)gamma2=0;
		res.setW(Math.round(1.0*n*res.getNumOfObservedUnits()/(n-n1)+1.0*n*n1/(n-n1)*gamma2));
		res.setU(res.getW()-res.getNumOfObservedUnits());

		/**
		 * The coverage and the probability of new unit class are the consequent results of W/U.
		 */

		res.setC(1.0*res.getNumOfObservedUnits()/res.getW());
		res.setN(1.0-res.getC());

		successful=true;
	}
}
