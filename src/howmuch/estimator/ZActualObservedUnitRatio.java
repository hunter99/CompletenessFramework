package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.EstimatorConfigure;
import howmuch.parse.StatRes;

/**
 * The actual ratio of the number of observed units versus the total number of units.
 * <P>Actually this is not an estimator, but a calculator.
 * <P>The estimator can only be used in controlled experiments where the process model is known.
 * 
 * @author hedong
 *
 */
@AnEstimator
public class ZActualObservedUnitRatio extends BaseEstimator{
	//The actual number of units.
	private  int realUnits=-1;
	
	/**
	 * Constructor.
	 * @param config configuration
	 */
	public ZActualObservedUnitRatio(EstimatorConfigure config) throws Exception{
		super("Actual OUR");
		realUnits=config.getInt("realUnits");
	}
	public void estimate(StatRes res){
		successful=false;
		if(realUnits<=0)
			return;
		double our=1.0*res.getNumOfObservedUnits()/realUnits;
		res.setC(our);
		res.setN(-1);
		res.setW(our);
		res.setU(-1);
		res.setL(our);
		successful=true;
	}
	
}
