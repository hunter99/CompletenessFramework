package howmuch.estimator;

import howmuch.parse.EstimatorConfigure;
/**
 * The base class for estimators requiring parameters.
 * @author hedong
 *
 */
public abstract class BaseEstimatorWithParameters extends BaseEstimator {
	/**
	 * configuration
	 */
	EstimatorConfigure config;
	/**
	 * epsilon, the maximum acceptable error of the estimation. 
	 * <p>The default value is 0.1. It can be reset by calling setEpsilon(ep) at running time.
	 */
	double epsilon=0.1;
	/**
	 * Confidence level.
	 * <p>The default value is 0.95. It can be reset by calling setConfidence(cf) at running time.
	 */
	double confidence=0.95;
	/**
	 * alpha=1-confidence
	 */
	double alpha;
	/**
	 * Constructor.
	 * @param config	configuration
	 * @param name	the name of the estimator.
	 */
	public BaseEstimatorWithParameters(EstimatorConfigure config,String name){
		super(name);
		this.config=config;
		setConfidence(config.getConfidenceLevel());
		setEpsilon(config.getEpsilon());
		setAlpha(1-confidence);
	}
	/**
	 * Set the value of epsilon.
	 * @param ep the value of epsilon.
	 */
	public void setEpsilon(double ep) {
		epsilon = ep;
	}
	/**
	 * Set the value of confidence level.
	 * @param cf the value of confidence level.
	 */
	public void setConfidence(double cf) {
		confidence = cf;
	}
	/**
	 * Set the Alpha.
	 * Typically used by estimators of KZN.
	 * @param a
	 */
	public void setAlpha(double a){
		alpha=a;
	}
}
