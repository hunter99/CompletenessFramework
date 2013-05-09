package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.EstimatorConfigure;
import howmuch.parse.StatRes;
import howmuch.probability.TraceDistribution;

/**
 * The actual probability coverage of observed trace classes.
 * <P>Actually this is not an estimator, but a calculator.
 * @author hedong
 *
 */
@AnEstimator
public class RealCompleteness extends BaseEstimatorWithParameters{
	//The occurrence probability distribution of trace classes.
	private  TraceDistribution realDist;
	/**
	 * Constructor.
	 * @param config configuration
	 */
	public RealCompleteness(EstimatorConfigure config) throws Exception{
		super(config,"Actual Trace Coverage");
		
		this.realDist=new TraceDistribution(config.getString("tracedistribution"));
		this.realDist.load();
	}
	public void estimate(StatRes res){
		successful=false;
		int w=res.getNumOfObservedUnits();
		res.setW(this.realDist.getDistSize());
		res.setU(res.getW()-w);
		double sum=0.0;
		for(String trace:res.getObservedUnits().keySet()){
			try{
			sum+=this.realDist.getTraceProb(trace);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if(sum>0.0){
			res.setC(sum);
			res.setN(1-res.getC());
		}
		successful=true;
	}
	
}
