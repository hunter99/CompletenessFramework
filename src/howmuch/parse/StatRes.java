package howmuch.parse;

import howmuch.PopEstimation;
import howmuch.estimator.Estimator;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * A data structure contains statistical information of a given log and the estimation results.
 * 
 * <P> The so-called information unit, which is used to evaluate the information completeness of a log, is
 * defined in the implementation of interface. 
 * <P>All estimators for a log will share the object of the classes, which implies that result of a latter-executed estimator
 * will overwrite those of previous estimators.
 * 
 * @author hedong
 *
 */
public interface StatRes{
	/**
	 * Get the corresponding estimation task.
	 * @return estimation task.
	 */
	public PopEstimation getEstimation();
	/**
	 * Get the configuration of the estimation task.
	 * @return configuration
	 */
	public EstimatorConfigure getConfig();
	/**
	 * Get the log file name of these estimation/statistical results.
	 * @return file name
	 */
	public String getFileName();
	/**
	 * Calculate the statstical information.
	 */
	public void calcStat();
	/**
	 * Store the content of a trace.
	 * 
	 * <p>
	 * When the process instance ID of an event is different from that of
	 * previous one, we know that a new instance starts and the old instances
	 * finished after the previous event.
	 * <p>
	 * If it is a new trace class, just store it. Otherwise, add the instance ID
	 * to the list of IDs of the same class.
	 * <P>typically the method is called in the MxmlFile.parse() repeatedly at each trace. The concrete definition
	 * of the method is presented in the implementations of the interface.
	 * @param instanceid
	 *            old process instance ID
	 * @param newinstanceid
	 *            new process instance ID
	 * @param tasks
	 *            list of tasks of the old process instance.
	 */
	public void counttrace(String instanceid, String newinstanceid, StringBuffer tasks);
	/**
	 * display the estimation result
	 */
	public void displayEstimation();
	/**
	 * output information of statistics.
	 */
	public void displayStat();
	/**
	 * Preparation for a new estimation.
	 * <P> It will be called before each estimator.
	 */
	public void initial4Estimating();
	/**
	 * set the estimators used to evaluate the log completeness.
	 * @param es list of estimators
	 */
	public void addEstimators(ArrayList<Estimator>es);
//	public void addEstimator(String name);
//	public ArrayList<String> getEstimators();
	/**
	 * the probability coverage of observed units.
	 * @return
	 */
	public double getC();
	/**
	 * set the estimated probability coverage of observed units.
	 * @param c
	 */
	public void setC(double c);
	/**
	 * the probability of a new (unknown) unit class.
	 * @return
	 */
	public double getN();
	/**
	 * set the probability of a new (unknown) unit class.
	 * @param n
	 */
	public void setN(double n);
	/**
	 * number of all possible unit classes.
	 * @return
	 */
	public double getW();
	/**
	 * set the estimated number of all possible unit classes.
	 * @param w
	 */
	public void setW(double w);
	/**
	 * the number of the unobserved unit classes.
	 * @return
	 */
	public double getU();
	/**
	 * set the estimated number of unoberved unti classes.
	 * @param u
	 */
	public void setU(double u);
	/**
	 * the expected log length given specified parameters( e.g. confidence level, error rate)
	 * @return
	 */
	public double getL();
	/**
	 * set the estimated expected log length given specified parameters( e.g. confidence level, error rate)
	 * @param l
	 */
	public void setL(double l);
	/**
	 * the real log length, i.e, the number of traces in the log.
	 * @return
	 */
	public int getLogLength();
	/**
	 * set the real log length, i.e, the number of traces in the log.
	 * @param loglen
	 */
	public void setLogLength(int loglen);
	/**
	 * The number of different units in the given log. 
	 * <P>The unit may be a trace, a ds relation.
	 * @return the number of observed units
	 */
	public int getNumOfObservedUnits();
	/**
	 * Set the number of different units in the given log.
	 * @param units number of observed units
	 */
	public void setNumOfObservedUnits(int units);
	/**
	 * Get the number of different traces in the given log. 
	 * <P>For StatTrace, the numberOfObservedUnits equals the numberOfObservedTraceClasses.
	 * @return number of observed trace classes
	 */
	public int getNumOfObservedTraceClasses();
	/**
	 * Set the number of different traces in the given log.
	 * @param classes the number of observed trace classes
	 */
	public void setNumOfObservedTraceClasses(int classes);
	/**
	 * Get the real probability coverage of observed units.
	 * <P> It is possible only in controlled experiments, where the occurrence distribution
	 * of units are known
	 * <P> Note the returned value is just the summation of probabilities of observed units.
	 * 
	 * @return
	 */
//	public double getCV();
	/**
	 * Encode a trace to get a tag.
	 * <P> Note different trace classes may have the same tag.
	 * @param tasks trace
	 * @return tage of a trace
	 */
	public StringBuffer tracetag(StringBuffer tasks);
	/**
	 * Check the number of observed trace classes that appearing $freq$ times
	 * exactly.
	 * 
	 * @param freq
	 *            the appearing time of an observed trace class.
	 * @return the number of observed trace classes.
	 */
	public int getFreqFreq(int freq);
	/**
	 * get all observed unit classes, as well as trace ids of each class. 
	 * @return observed unit classes and their occurrence trace ids.
	 */
	public Hashtable<String, ArrayList<String>> getObservedUnits();
	/**
	 * Return the minimum ordinal number of the trace, which is the last observed trace class.
	 */
	public int getTraceNumOfLatestUnit();
}
