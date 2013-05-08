package howmuch.probability;
/**
 * Calculate the percentile of a uniform distribution.
 * @author hedong
 *
 */
public class UniformPercentile  implements DistributionPercentile {
	double low,high;
	/**
	 * Constructor.
	 * @param a lower value
	 * @param b upper value
	 */
	public UniformPercentile(double a,double b){
		this.low=a;
		this.high=b;
	}
	public String name() {
		return "uniform";
	}
	public void put(double percent, double z) {
		
	}
	public double z(double percent) {
		return low+(high-low)*percent;
	}

}
