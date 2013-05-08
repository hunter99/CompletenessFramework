package howmuch.probability;

/**
 * The percentile of a normal distribution.
 * @author hedong
 *
 */
public class NormalPercentile extends DistributionPercentileBase implements DistributionPercentile {
	public NormalPercentile(){
		percentile.put(0.025,-1.95996398);
		percentile.put(0.050,-1.64485363);
		percentile.put(0.075,-1.43953147);
		percentile.put(0.100,-1.28155157);
		percentile.put(0.125,-1.15034938);
		percentile.put(0.150,-1.03643339);
		percentile.put(0.175,-0.93458929);
		percentile.put(0.200,-0.84162123);
		percentile.put(0.225,-0.75541503);
		percentile.put(0.250,-0.67448975);
		percentile.put(0.275,-0.59776013);
		percentile.put(0.300,-0.52440051);
		percentile.put(0.325,-0.45376219);
		percentile.put(0.350,-0.38532047);
		percentile.put(0.375,-0.31863936);
		percentile.put(0.400,-0.25334710);
		percentile.put(0.425,-0.18911843);
		percentile.put(0.450,-0.12566135);
		percentile.put(0.475,-0.06270678);
		percentile.put(0.500,0.00000000);
		percentile.put(0.525,0.06270678);
		percentile.put(0.550,0.12566135);
		percentile.put(0.575,0.18911843);
		percentile.put(0.600,0.25334710);
		percentile.put(0.625,0.31863936);
		percentile.put(0.650,0.38532047);
		percentile.put(0.675,0.45376219);
		percentile.put(0.700,0.52440051);
		percentile.put(0.725,0.59776013);
		percentile.put(0.750,0.67448975);
		percentile.put(0.775,0.75541503);
		percentile.put(0.800,0.84162123);
		percentile.put(0.825,0.93458929);
		percentile.put(0.850,1.03643339);
		percentile.put(0.875,1.15034938);
		percentile.put(0.900,1.28155157);
		percentile.put(0.925,1.43953147);
		percentile.put(0.950,1.64485363);
		percentile.put(0.975,1.95996398);
	}
	public String name() {
		return "normal";
	}

}
