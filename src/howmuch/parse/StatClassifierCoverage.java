package howmuch.parse;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * Base class for estimators which requires the original occurrence probability distribution of units.
 * @author hedong
 *
 */
public abstract class StatClassifierCoverage  extends StatTrace implements StatRes {
	static Log log=LogFactory.getLog(StatClassifierCoverage.class);
	/**
	 * allitems与order，用于对一个集合进行编码，分别对应编码与去重两个任务。
	 */
	/**
	 * The set of items and their corresponding code.
	 */
	static Hashtable<String,Integer> allitems=new Hashtable<String,Integer>();
	/**
	 * The code of a set, which is a list of item codes.
	 */
	static ArrayList<Integer> order=new ArrayList<Integer>();
	private boolean setInitial=false;
	/**
	 * put an item into a set.
	 * @param item item
	 */
	public void putItem(String item){
		if(!setInitial)
			initialSet();
		int id=allitems.size();
		if(allitems.containsKey(item)){
			id=allitems.get(item);
		}else{
			allitems.put(item, id);
			order.add(0);
		}
		order.set(id, 1);
	}
	/**
	 * get the code of the set.
	 * @return
	 */
	public StringBuffer setCoding(){		
		StringBuffer sb=new StringBuffer(1024);
		for(int i = 0;i<order.size();i++){
			if(order.get(i)!=0){
				if(sb.length()>0)
					sb.append(","+i);
				else
					sb.append(""+i);
			}
		}
		setInitial=false;
		return sb;
	}
	/**
	 * initialize the set.
	 */
	public void initialSet(){
		for(int i=0;i<order.size();i++)
			order.set(i, 0);
		setInitial=true;
	}
	/**
	 * The occurrence probability of each unit.
	 */
	Map<String,Double>probs; 
	/**
	 * The probability coverage of each step. The maximum step is the log length.
	 */
	ArrayList<Double> covers; 
	/**
	 * The observed units and their occurrence times.
	 */
	Map<String,Integer> otcs; 
	@Override
	public double getCV(){
		if(covers.size()<1)
			return -1;
		return covers.get(covers.size()-1);
	}
	/**
	 * 求日志的真实coverage.
	 * @param logFile 日志文件
	 * @param probs  各个trace class的概率
	 * @param covers 返回的整个日志的每行的覆盖
	 */
	@SuppressWarnings("unchecked")
	public StatClassifierCoverage(EstimatorConfigure config) {
		super(config);
		this.probs=config.getTraceDistribution();
		setCovers((ArrayList<Double> )config.getObject("covers"));
		setObservedTraceClasses((Map<String,Integer>)config.getObject("otcs"));
	}
	/**
	 * Set the list used to store the coverage values for steps.
	 * @param covers the list for coverage values.
	 */
	public void setCovers(ArrayList<Double> covers){
		this.covers=covers;
		if(covers.size()!=0){
			log.warn("Covers length is "+covers.size()+". It should be zero.");
		}else{
			covers.add(0.0);
		}
	}
	/**
	 * Set the map used to store the observed traces.
	 * @param otcs
	 */
	public void setObservedTraceClasses(Map<String,Integer> otcs){
		this.otcs=otcs;
	}
	@Override
	public void counttrace(String instanceid, String newinstanceid, StringBuffer tsks) {
		StringBuffer tasks=tracetag(tsks);
		String trace=tasks.toString();
		double last=covers.get(covers.size()-1);
		
		if(!otcs.containsKey(trace)){
			if(probs.containsKey(trace)){
				last+=probs.get(trace);
				otcs.put(trace, 1);
			}else{
				log.warn("there is no trace prob for "+trace);
			}
		}else{
			otcs.put(trace,1+otcs.get(trace));
		}
		covers.add(last);
		
		super.counttrace(instanceid, newinstanceid, tasks);
	}
}
