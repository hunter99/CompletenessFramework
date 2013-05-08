package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.EstimatorConfigure;
import howmuch.parse.StatRes;
import howmuch.probability.DistributionPercentile;
import howmuch.probability.NormalPercentile;
/**
 * Esitmator from Lin's Book 'Introduction to Stochastic Mathmatics', P. 202
 * @author hedong
 *
 */
@AnEstimator
public class LinEstimator extends BaseEstimatorWithParameters {
	DistributionPercentile percentile=new NormalPercentile();
	public LinEstimator(EstimatorConfigure config){
		super(config,"Lin");
	}

	public void estimate(StatRes res) {
		successful=false;
		//check the validity of epsilon value.
		if (epsilon <= 0 || epsilon > 1) {
			System.err.println("epsilon should be in (0,1)");
			return ;
		}
		if (confidence <= 0 || confidence >= 1) {
			System.err.println("confidence should be in (0,1)");
			return ;
		}
		double z=percentile.z((1+confidence)/2);
		double d1=z*z;
		double d2=4*epsilon*epsilon;
		res.setL((long)Math.ceil(d1/d2));
		successful=true;
	}
}
