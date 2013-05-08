package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.StatRes;


/**
 * GS Bayesian Estimator.
 * <p>
 * refer to [CandolfiSastri2004]
 * <P> This estimator is copied from LogMetrics. Boudewijn modified the 
 * approach and renamed the estimator as "GS2" other than original "GS".
 * @author hedong
 * 
 */
@AnEstimator
public class GS2Estimator extends BaseEstimator{
	public GS2Estimator(){
		super( "GS2" );
	}


	public void estimate(StatRes res) {
		double n1=res.getFreqFreq(1);
		double n=res.getLogLength();
		double N=res.getNumOfObservedUnits();
		double gamma2 = -n * n1 - N * n1 + n1 * n1 + n1
				* Math.sqrt(5 * n * n + 2 * n * (N - 3 * n1) + (N - n1) * (N - n1));
		gamma2 = gamma2 / (2 * n * n1);
		if ((n1 == n) || (gamma2 == 0)) {
			return;
		}
		double c = n * N / (n - n1) + n * n1 / (n - n1) * gamma2;
		if (c >= N) {
			res.setW((long)c);
			res.setU(res.getW()-res.getNumOfObservedUnits());
		}
	}
}