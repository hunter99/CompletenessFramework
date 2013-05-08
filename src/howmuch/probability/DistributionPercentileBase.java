package howmuch.probability;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class DistributionPercentileBase {
	static Log log=LogFactory.getLog(DistributionPercentileBase.class);
	Map<Double,Double> percentile=new HashMap<Double,Double>();
	public void put(double percent, double z) {
		percentile.put(percent, z);
		
	}
	public double z(double percent) {
		percent=1.0*Math.round(percent*100000)/100000;
		if(!percentile.containsKey(percent)){
			log.warn("Cannot get the Z_alpha value for "+percent);
			return -1;
		}
		return percentile.get(percent);
	}

}
