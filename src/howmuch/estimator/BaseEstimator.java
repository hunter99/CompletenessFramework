package howmuch.estimator;


/**
 * The basic class for the estimators.
 * 
 * @author hedong
 *
 */
public abstract class BaseEstimator implements Estimator{
	public boolean successful=false;
	public String name=null;
	public BaseEstimator(String name){
		setName(name);
	}
	public String name(){
		return name;
	}
	public void setName(String name){
		this.name=name;
	}
	public boolean succeed(){
		return successful;
	}
	public String[] variants(){
		return null;
	}
}
