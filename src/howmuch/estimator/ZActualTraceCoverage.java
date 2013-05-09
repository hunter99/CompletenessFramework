package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.EstimatorConfigure;
import howmuch.parse.StatRes;
import howmuch.probability.TraceDistribution;

/**
 * The actual probability coverage of observed trace classes.
 * <P>Actually this is not an estimator, but a calculator.
 * <P>The estimator can only be used in controlled experiments where the trace occurrence distribution is known.
 * @author hedong
 *
 */
@AnEstimator
public class ZActualTraceCoverage extends BaseEstimatorWithParameters{
	//The occurrence probability distribution of trace classes.
	private  TraceDistribution realDist;
	/**
	 * Constructor.
	 * @param config configuration
	 */
	public ZActualTraceCoverage(EstimatorConfigure config) throws Exception{
		super(config,"Actual Trace Coverage");
		
		this.realDist=new TraceDistribution(config.getString("tracedistribution"));
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
		res.setN(1 - res.getC());
		res.setW(sum);
		res.setU(-1);
		res.setL(sum);
		successful=true;
	}
	
}
