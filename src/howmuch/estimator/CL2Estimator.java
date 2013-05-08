package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.StatRes;

/**
 * Chao & Lee's Estimator #2.
 * <p> refer to [CandolfiSastri2004]
 * @author hedong
 *
 */
@AnEstimator
public class CL2Estimator extends BaseEstimator{
	public CL2Estimator(){
		super("CL2");
	}

	public void estimate(StatRes res){
		int n1=res.getFreqFreq(1);
		int n=res.getLogLength();
		
		successful=false;
		double gamma1=0;
		for (int j=1;j<=n;j++){
			gamma1+=j*(j-1)*res.getFreqFreq(j);
		}
		double gamma2=1.0*n*res.getNumOfObservedUnits()/((n-n1)*n*(n-1))*gamma1-1;
		if(gamma2<0)gamma2=0;
		double gamma3=1.0+n1*gamma2/((n-1)*(n-n1));
		gamma3=gamma3*gamma2;
		if(gamma3<0)gamma3=0;
		res.setW(Math.round(1.0*n*res.getNumOfObservedUnits()/(n-n1)+1.0*n*n1/(n-n1)*gamma3));
		res.setU(res.getW()-res.getNumOfObservedUnits());
		
		/**
		 * The coverage and the probability of new unit class are the consequent results of W/U.
		 */
		res.setC(1.0*res.getNumOfObservedUnits()/res.getW());
		res.setN(1.0-res.getC());

		successful=true;
	}
}