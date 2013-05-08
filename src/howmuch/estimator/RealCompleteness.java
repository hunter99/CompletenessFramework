package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.EstimatorConfigure;
import howmuch.parse.StatRes;
import howmuch.probability.TraceDistribution;

/**
 * 真实的比例与覆盖.
 * 这个函数，主要是用来跟各种估计器作比较的.
 * @author hedong
 *
 */
@AnEstimator
public class RealCompleteness extends BaseEstimatorWithParameters{
	//真实分布，其元素的个数同时即为真实的种类数.
	private  TraceDistribution realDist;
	/**
	 * 构造函数.
	 * @param dist	真实分布
	 */
	public RealCompleteness(EstimatorConfigure config) throws Exception{
		super(config,"Real");
		
		this.realDist=new TraceDistribution(config.getString("tracedistribution"));
		this.realDist.load();
	}
	public void estimate(StatRes res){
		successful=false;
//		int n=res.getLogLength();
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
