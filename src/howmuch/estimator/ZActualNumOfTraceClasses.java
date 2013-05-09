package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.EstimatorConfigure;
import howmuch.parse.StatRes;

/**
 * The actual number of all possible trace classes.
 * <P>Actually this is not an estimator, but a calculator.
 * <P>The estimator can only be used in controlled experiments where the process model is known.
 * 
 * @author hedong
 *
 */
@AnEstimator
public class ZActualNumOfTraceClasses extends BaseEstimator{
	//The actual number of trace classes.
	private  int realTraceClasses=-1;
	/**
	 * Constructor.
	 * @param config configuration
	 */
	public ZActualNumOfTraceClasses(EstimatorConfigure config) throws Exception{
		super("Actual number of trace classes");
		realTraceClasses=config.getInt("realTraceClasses");
	}
	public void estimate(StatRes res){
		successful=false;
		res.setC(realTraceClasses);
		res.setN(-1);
		res.setW(realTraceClasses);
		res.setU(-1);
		res.setL(realTraceClasses);
		successful=true;
	}
	
}
