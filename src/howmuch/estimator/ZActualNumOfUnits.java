package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.EstimatorConfigure;
import howmuch.parse.StatRes;

/**
 * The actual number of all possible information units.
 * <P>Actually this is not an estimator, but a calculator.
 * <P>The estimator can only be used in controlled experiments where the process model is known.
 * 
 * @author hedong
 *
 */
@AnEstimator
public class ZActualNumOfUnits extends BaseEstimator{
	//The actual number of units.
	private  int realUnits=-1;
	/**
	 * Constructor.
	 * @param config configuration
	 */
	public ZActualNumOfUnits(EstimatorConfigure config) throws Exception{
		super("Actual number of classes");
		realUnits=config.getInt("realUnits");
	}
	public void estimate(StatRes res){
		successful=false;
		res.setC(realUnits);
		res.setN(-1);
		res.setW(realUnits);
		res.setU(-1);
		res.setL(realUnits);
		successful=true;
	}
	
}
