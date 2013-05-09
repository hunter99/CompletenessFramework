package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.EstimatorConfigure;
import howmuch.parse.StatRes;
import howmuch.probability.TraceDistribution;

/**
 * The actual probability coverage of observed information units.
 * <P>Actually this is not an estimator, but a calculator.
 * <P>The estimator can only be used in controlled experiments where the unit occurrence distribution is known.
 * @author hedong
 *
 */
@AnEstimator
public class ZActualUnitCoverage extends BaseEstimatorWithParameters{
	//The occurrence probability distribution of information units.
	private  TraceDistribution realDist;
	/**
	 * Constructor.
	 * @param config configuration
	 */
	public ZActualUnitCoverage(EstimatorConfigure config) throws Exception{
		super(config,"Actual CV");
		
		this.realDist=new TraceDistribution(config.getString("unitdistribution"));
		this.realDist.load();
	}
	public void estimate(StatRes res){
		successful=false;
		double sum=0.0;
		for(String trace:res.getObservedUnits().keySet()){
			try{
			sum+=this.realDist.getTraceProb(trace);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		res.setC(sum);
		res.setN(-1);
		res.setW(sum);
		res.setU(-1);
		res.setL(sum);
		successful=true;
	}
	
}
