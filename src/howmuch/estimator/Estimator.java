package howmuch.estimator;

import howmuch.parse.StatRes;

/**
 * Interface for all estimators.
 * @author hedong
 *
 */
public interface Estimator{
	public boolean succeed();
	public void estimate(StatRes res);
	public String name();
	public String[] variants();
}
