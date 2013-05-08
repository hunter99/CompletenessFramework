package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.StatRes;

/**
 * GS Bayesian Estimator.
 * <p> refer to [CandolfiSastri2004]
 * @author hedong
 *
 */
@AnEstimator
public class GSEstimator extends BaseEstimator{
	public  GSEstimator(){
		super("GS");
	}
	public void estimate(StatRes res){
		successful=false;
		int n1=res.getFreqFreq(1);
		int n=res.getLogLength();
		int N=res.getNumOfObservedUnits();
		double gamma2=-n*n1-N*n1+n1*n1+n1*Math.sqrt(5*n*n+2*n*(N-3*n1)+(N-n1)*(N-n1));
		gamma2=gamma2/(2*n*n1);
		res.setW(Math.round(1.0*n*N/(n-n1)+1.0*n*n1/(n-n1)*gamma2));
		res.setU(res.getW()-res.getNumOfObservedUnits());
		successful=true;
	}
}
