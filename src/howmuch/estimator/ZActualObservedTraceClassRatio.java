package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.EstimatorConfigure;
import howmuch.parse.StatRes;
/**
 * The actual ratio of the number of observed trace classes versus the total number of trace classes.
 * <P>Actually this is not an estimator, but a calculator.
 * <P>The estimator can only be used in controlled experiments where the process model is known.
 * <P> The OTR metric is also referred to as "OTC" in some articles.
 * @author hedong
 *
 */
@AnEstimator
public class ZActualObservedTraceClassRatio extends BaseEstimator{
	//The actual number of trace classes.
	private  int realTraceClasses=-1;
	
	/**
	 * Constructor.
	 * @param config configuration
	 */
	public ZActualObservedTraceClassRatio(EstimatorConfigure config) throws Exception{
		super("Actual OTR");
		realTraceClasses=config.getInt("realTraceClasses");
	}
	public void estimate(StatRes res){
		successful=false;
		if(realTraceClasses<=0)
			return;
		double otr=1.0*res.getNumOfObservedTraceClasses()/realTraceClasses;
		res.setC(otr);
		res.setN(-1);
		res.setW(otr);
		res.setU(-1);
		res.setL(otr);
		successful=true;
	}
	
}
