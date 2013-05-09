package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.EstimatorConfigure;
import howmuch.parse.StatRes;

/**
 * The number of observed unit classes so far in the log.
 * <P>Actually this is not an estimator, but a calculator.
 * @author hedong
 *
 */
@AnEstimator
public class ZObservedUnitClasses extends BaseEstimatorWithParameters{
	/**
	 * Constructor.
	 * @param config configuration
	 */
	public ZObservedUnitClasses(EstimatorConfigure config) throws Exception{
		super(config,"Observed number of classes");
		
	}
	public void estimate(StatRes res){
		successful=false;
		res.setC(res.getNumOfObservedUnits());
		res.setN(-1);
		res.setW(res.getNumOfObservedUnits());
		res.setU(-1);
		res.setL(res.getNumOfObservedUnits());
		successful=true;
	}
	
}
