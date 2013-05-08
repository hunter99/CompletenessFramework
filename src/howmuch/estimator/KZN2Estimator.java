package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.EstimatorConfigure;
import howmuch.parse.StatRes;
import howmuch.probability.DistributionPercentile;
import howmuch.probability.NormalPercentile;
@AnEstimator
public class KZN2Estimator extends KZN1Estimator {
	DistributionPercentile percentile=new NormalPercentile();
	public KZN2Estimator(EstimatorConfigure config) {
		super(config,"KZN2");
		
	}
	private double zAlpha(double alpha){
		return  percentile.z(1-alpha);
	}
	private double zHalfAlpha(double alpha){
		return percentile.z(1-alpha/2);
	}
	@Override
	void calculateYBar(double alhpa,int numunits,int loglength,StatRes res){
		//calculate y-bar
		ybar=new double[numunits];
		int index=0;
		for(String unit:res.getObservedUnits().keySet()){
			double yfreq=1.0*res.getObservedUnits().get(unit).size()/loglength;
			ybar[index]=yfreq-zHalfAlpha(alpha)*Math.sqrt(yfreq*(1-yfreq)/loglength);
			index++;
		}
	}

}
