package howmuch.parse;

import howmuch.PopEstimation;
import howmuch.estimator.Estimator;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * Some general functions for the interface of StatRes
 * @author hedong
 *
 */
public abstract class StatResImpl {
	static Log log=LogFactory.getLog(StatResImpl.class);
	/**
	 * Output something each interval of traces in a very long log, 
	 * to tell programmer the progam is running correctly.
	 */
	final int TRACEINTERVAL=2000;
	/**
	 * estimators that produce the estimation.
	 */
	ArrayList<Estimator> esinstances;
	/**
	 * the estimator names for what?
	 */
//	ArrayList<String> estimatorss;
	/**
	 * The corresponding estimation task.
	 */
	PopEstimation estimation;
	/**
	 * configuration of the estimation task.
	 */
	EstimatorConfigure config;
	/**
	 * The name of the event log file to be parsed.
	 */
	private String fileName;
	/**
	 * The 'traces' is used to store all traces of an event log.
	 * 
	 * <p>
	 * The key value pair consists of a trace class and a list of process
	 * instance IDs, namely a list of its appearances.
	 */
	Hashtable<String, ArrayList<String>> traces;
	/**
	 * The frequency of frequency.
	 * <P>
	 * The key is the appearance times, and the value is the number of observed
	 * trace classes appearing exactly key times.
	 */
	Hashtable<Integer, Integer> freqfreqs;
	/**
	 * The real length of the log, or the size of the sample.
	 */
	int loglength = 0;
	/**
	 * Number of observed trace classes.
	 */
	int observedclasses = 0;
	/**
	 * W is the number of all possible units(e.g., traces, DSes).
	 */
	double W = -1;
	/**
	 * U is the number of all unobserved units(e.g., traces, DSes).
	 */
	double U = -1;
	/**
	 * C is the probability coverage of the observed units in the log.
	 */
	double C = -0.1;
	/**
	 * N is the probability coverage of the unobserved units.
	 */
	double N = -0.1;
	/**
	 * the expected minimum length of a complete event log.
	 */
	double L = -1;
	/**
	 * A stream used to output the estimation results.
	 */
	PrintStream out=System.out;
	/**
	 * Construct method.
	 * 
	 * @param config configuration
	 */
	public StatResImpl(EstimatorConfigure config) {
		this.estimation=config.getEstimation();
		this.config=config;
		if(config!=null){
			fileName = config.getString("currentLog");
			int lastindex=fileName.lastIndexOf(File.separator);
			if(lastindex>-1){
				shortName=fileName.substring(lastindex+1);
			}else{
				shortName=fileName;
			}
			config.alignDist((StatRes)this);
			if(config.containsKey("resultStream")){
				out=(PrintStream)config.getObject("resultStream");
			}
		}
		traces = new Hashtable<String, ArrayList<String>>();
		freqfreqs = new Hashtable<Integer, Integer>();
		esinstances = new ArrayList<Estimator> ();
	}
	/**
	 * Get the corresponding estimation task.
	 * @return estimation task
	 */
	public PopEstimation getEstimation(){
		return this.estimation;
	}
	/**
	 * Get the configuration of the estimation task.
	 * @return configuration
	 */
	public EstimatorConfigure getConfig(){
		return this.config;
	}
	/**
	 * set estimators for the estimation task.
	 * @param es list of estimators
	 */
	public void addEstimators(ArrayList<Estimator>es){
		for(Estimator e:es){
			esinstances.add(e);
		}
		log.info(esinstances.size()+" estimators in the statres");
	}
	/**
	 * Encode a trace with a tag, and here it does nothing but return the trace.
	 * <P> Different traces may have the same tag. The encoding method is determined by the definition of information unit.
	 * <P>
	 * @param tasks trace
	 * @return tag of the trace
	 */
	public StringBuffer tracetag(StringBuffer tasks){
		return tasks;
	}

	/**
	 * Get the ratio of observed trace classes versus the total number of units.
	 * <P> the ratio is valid only when the information unit is Trace.
	 * @return ratio
	 */
	public double getRatio() {
		if (W == 0)
			return -1;
		return this.observedclasses * 1.0 / W;
	}
	/**
	 * Get the estimated probability coverage of the log.
	 * @return estimated probability coverage
	 */
	public double getCoverage() {
		return C;
	}
	/**
	 * Get the real probability coverage of the log.
	 * @return real probability coverage
	 */
//	public double getCV(){
//		return -1;
//	}
	/**
	 * Preparation for a new estimation.
	 */
	public void initial4Estimating() {
		W = -1;
		U = -1;
		C = -0.1;
		N = -0.1;
		L = -1;
	}
	/**
	 * Improved statistical computation, where only the affected items of freqfreq are considered. 
	 * @param trace the current parsing trace
	 */
	public void calcStatDelta(String trace){
		loglength++;
		observedclasses=traces.size();
		int curdups=traces.get(trace).size();
		int olddups=curdups-1;
		Integer curfreq=freqfreqs.remove(curdups);
		Integer oldfreq=freqfreqs.remove(olddups);
		if(curfreq==null)
			curfreq=0;
		if(oldfreq==null)
			oldfreq=0;
		curfreq++;
		oldfreq--;
		freqfreqs.put(curdups, curfreq);
		if(oldfreq>0)
			freqfreqs.put(olddups, oldfreq);
	}
	/**
	 * Calculate the statstical information of the log so far during parsing.
	 */
	public void calcStat() {
		freqfreqs.clear();
		loglength=0;
		observedclasses=0;
		for (Iterator<String> itr = traces.keySet().iterator(); itr.hasNext();) {
			String key = itr.next();
			ArrayList<String> ids = traces.get(key);
			int idfreq = ids.size();
			int ff = 1;
			if (freqfreqs.containsKey(idfreq)) {
				ff = ff + freqfreqs.remove(idfreq);
			}
			freqfreqs.put(idfreq, ff);
			loglength += idfreq;
			observedclasses += 1;
		}

	}
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
	 * 
	 * @param instanceid
	 *            old process instance ID
	 * @param newinstanceid
	 *            new process instance ID
	 * @param tasks
	 *            list of tasks of the old process instance.
	 */
	public void counttrace(String instanceid, String newinstanceid, StringBuffer tasks) {
		if (instanceid == null || instanceid.length() < 1)
			return;
		if (instanceid.equals(newinstanceid) && newinstanceid != null)
			return;
		String trace = tasks.toString();
		ArrayList<String> ids;
		if (traces.containsKey(trace)) {// if the trace classes has appeared
										// before
			ids = traces.remove(trace);
		} else {
			ids = new ArrayList<String>();
		}
		ids.add(instanceid);// add the instance id to the list.
		traces.put(trace, ids);
		tasks.setLength(0);
	}
	/**
	 * Output a string with a line feed.
	 * @param str a string
	 */
	private void println(String str){
		print(str);
		println();
	}
	/**
	 * Output a line feed.
	 */
	private void println(){
		print("\n");
	}
	/**
	 * Output a string without a line feed.
	 * @param str a string
	 */
	private void print(String str){
		out.print(str);
	}
	/**
	 * output the statistical information of the log.
	 */
	public void displayStat() {
		print("file: " + fileName);
		for (Iterator<String> itr = traces.keySet().iterator(); (0 > 1)
				&& (itr.hasNext());) {
			String key = itr.next();
			ArrayList<String> ids = traces.get(key);
			println("" + ids.size() + "\t" + key);
		}
		int j = 0;
		int count = 0;
		for (int i = 1; i < this.loglength; i++) {
			if (!freqfreqs.containsKey(i))
				continue;
			print(i + ":" + freqfreqs.get(i));
			count += i * freqfreqs.get(i);
			if (count >= this.observedclasses) {
				println();
				break;
			}
			if (j > 5) {
				println();
				j = 0;
			} else
				print("\t");
			j++;
		}

		print("\ttotal:" + loglength);
		print("\tclasses:" + observedclasses + "\t");
	}
	//count the number of processed traces in the log
	long count=0;
	//count the computational time cost of each estimator every 2000 traces.
	long[]estimatecosts=null;
	//count the output time cost of estimation results every 2000 traces.
	long outputcost=0;
	//three buffers for outputting results efficiently 
	StringBuffer sb=new StringBuffer(1024);
	StringBuffer sb1=new StringBuffer(3096);
	StringBuffer sb2=new StringBuffer(1024).append("\n");
	String shortName=null;
	/**
	 * Perform estimation on a given log.
	 * @param res result
	 */
	void estimates(StatRes res){
		if(estimatecosts==null){
			estimatecosts=new long[esinstances.size()];
			for(int i=0;i<estimatecosts.length;i++)
				estimatecosts[i]=0;
		}
		int i=0;
		for(Estimator e:esinstances){
			long estimatestart=System.nanoTime();
			initial4Estimating();
			e.estimate(res);
			long outputstart=System.nanoTime();
			sb.setLength(0);
			sb1.setLength(0);
//			System.out.println(res.getFileName()+",L"+res.getLogLength()+",units="+res.getNumOfObservedUnits()+",estmtr="+e.name()+",coverage"+res.getC()+",observedtraceclasses="+res.getNumOfObservedTraceClasses()+",CV="+res.getCV());
//			System.out.println(res.getFileName()+",L"+res.getLogLength()+",units="+res.getNumOfObservedUnits()+",estmtr="+e.name()+",classes"+res.getW() +",observedtraceclasses="+res.getNumOfObservedTraceClasses()+",CV="+res.getCV());
//			System.out.println(res.getFileName()+",L"+res.getLogLength()+",units="+res.getNumOfObservedUnits()+",estmtr="+e.name()+",length"+res.getL()  +",observedtraceclasses="+res.getNumOfObservedTraceClasses()+",CV="+res.getCV());
			sb.append(this.shortName).append(",L").append(res.getLogLength()).append(",estmtr=").append(e.name());
			sb1.append(sb).append(",coverage").append(res.getC()).append(sb2);
			sb1.append(sb).append(",classes" ).append(res.getW()).append(sb2);
			sb1.append(sb).append(",length"  ).append(res.getL()).append(sb2);
			println(sb1.toString());
			long outputstop=System.nanoTime();
			estimatecosts[i]+=outputstart-estimatestart;
			outputcost+=outputstop-outputstart;
			i++;
		}
		count++;
		if(count%TRACEINTERVAL==0){
			i=0;
			sb1.setLength(0);
			sb1.append("Average TimeCost of past").append(TRACEINTERVAL).append(" traces");
			for(i=0;i<estimatecosts.length;i++){
				sb1.append(esinstances.get(i).name()).append(":").append(estimatecosts[i]/TRACEINTERVAL).append(",");
				estimatecosts[i]=0;
			}
			sb1.append("output:").append(outputcost/(TRACEINTERVAL*esinstances.size()));
			log.info(sb1.toString());
			outputcost=0;
		}

	}
	/**
	 * display the estimation result
	 */
	public void displayEstimation() {
		println("Log length:" + this.loglength
				+ "\tObserved trace classes:" + this.observedclasses);
		println("Unobserved classes:" + this.U
				+ "\tTotal trace classes:" + this.W);
		print("New Trace probability:" + this.N
				+ "\tCoverbility of observed:" + this.C + "\t By: ");
		for (int i = 0; i < esinstances.size(); i++) {
			if (i > 0)
				print(",");
			print(esinstances.get(i).name());
		}
		println();
	}
	/**
	 * get the log file name.
	 * @return log file name
	 */
	public String getFileName() {
		return this.fileName;
	}
	/**
	 * get the estimated probability coverage of the observed units in the given log.
	 * @return coverage
	 */
	public double getC() {
		return C;
	}
	/**
	 * Set the estimated probability coverage of the observed units in the log.
	 * @param c coverage
	 */
	public void setC(double c) {
		C=c;
	}
	/**
	 * Get the estimated probability of a new (unkown) unit appearing in the next trace. 
	 * @return occurrence probability of unknown units
	 */
	public double getN() {
		return N;
	}
	/**
	 * set the estimated occurrence probability of unknown units.
	 * @param n occurrence probability of unknown units
	 */
	public void setN(double n) {
		N=n;
	}
	/**
	 * Get the estimated number of all units.
	 * @return total number of units
	 */
	public double getW() {
		return W;
	}
	/**
	 * Set the estimated number of all units.
	 * @param w estimated total number of units
	 */
	public void setW(double w) {
		W=w;
		
	}
	/**
	 * Get the estimated number of unknown units.
	 * @return estimated number of unknown units
	 */
	public double getU() {
		return U;
	}
	/**
	 * Set the estimated number of unknown units.
	 * @param u estimated number of unknown units
	 */
	public void setU(double u) {
		U=u;
	}
	/**
	 * Get the estimated minimum length of a complete log.
	 * @return log length
	 */
	public double getL() {
		return L;
	}
	/**
	 * Set the estimated minimum length of a complete log 
	 * @param l log length
	 */
	public void setL(double l) {
		L=l;
	}
	/**
	 * Get the real length of a log
	 * @return log length
	 */
	public int getLogLength() {
		return this.loglength;
	}
	/**
	 * Set the real length of a log
	 * @param loglen log length
	 */
	public void setLogLength(int loglen) {
		this.loglength=loglen;
		
	}
	/**
	 * Get the number of observed trace classes in the log.
	 * @return number of observed trace classes 
	 */
	public int getNumOfObservedTraceClasses() {
		return this.observedclasses;
	}
	/**
	 * Set the number of observed trace classes in the log.
	 * @param ocs number of trace classes
	 */
	public void setNumOfObservedTraceClasses(int ocs) {
		this.observedclasses=ocs;
	}
	/**
	 * Get the minimal ordinal number of a trace in the log,
	 * which is the latest observed trace classes. 
	 * @return ordinal number of a trace in the log
	 */
	public int getTraceNumOfLatestUnit(){
		return -1;
	}
}
