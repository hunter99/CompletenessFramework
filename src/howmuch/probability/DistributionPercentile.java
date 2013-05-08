package howmuch.probability;

public interface DistributionPercentile {
	void put(double percent,double z);
	double z(double percent);
	String name();
}
