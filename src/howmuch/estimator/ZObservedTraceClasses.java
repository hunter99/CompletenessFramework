package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.EstimatorConfigure;
import howmuch.parse.StatRes;

/**
 * The number of observed trace classes so far in the log.
 * <P>Actually this is not an estimator, but a calculator.
 * @author hedong
 *
 */
@AnEstimator
public class ZObservedTraceClasses extends BaseEstimatorWithParameters{
	/**
	 * Constructor.
	 * @param config configuration
	 */
	public ZObservedTraceClasses(EstimatorConfigure config) throws Exception{
		super(config,"Observed number of trace classes");
		
	}
	public void estimate(StatRes res){
		successful=false;
		res.setC(res.getNumOfObservedTraceClasses());
		res.setN(-1);
		res.setW(res.getNumOfObservedTraceClasses());
		res.setU(-1);
		res.setL(res.getNumOfObservedTraceClasses());
		successful=true;
	}
	
}
