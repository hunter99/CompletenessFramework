package howmuch.parse;

import howmuch.annotations.AnStatRes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * A DS relation is the information unit.
 * <P>Note if a DS appearing more than one time in trace, only the first appearance is considered.
 * @author hedong
 *
 */
@AnStatRes
public class StatDSTrace extends StatResImpl implements StatRes {
	static Log log=LogFactory.getLog(StatDSTrace.class);
	public StatDSTrace(EstimatorConfigure config){
		super(config);
		dses = new Hashtable<String, ArrayList<String>>();
		dsfreqfreqs = new Hashtable<Integer, Integer>();
	}

	/**
	 * Set of DS relations in the log.
	 */
	Hashtable<String, ArrayList<String>> dses;
	/**
	 * The frequency of occurrence frequency of DS relations.
	 */
	Hashtable<Integer, Integer> dsfreqfreqs;
	/**
	 * Number of observed DS relations.
	 */
	int observeddses;
	/**
	 * Get the observed DS relations as well as their corresponding traces.
	 * @return
	 */
	public Hashtable<String, ArrayList<String>> getDSes() {
		return this.dses;
	}
	/**
	 * Encode a DS relation.
	 * @param task1	the first task of a DS relation
	 * @param task2 the second task of a DS relation
	 * @return the DS code
	 */
	public String dscode(String task1,String task2){
		return task1+","+task2;
	}
	@Override
	public void counttrace(String instanceid, String newinstanceid, StringBuffer tasks) {
		String trace=tasks.toString();
		String[] tsks = trace.split("\\s*,\\s*");
		Map<String,Integer> dsintrace = new HashMap<String,Integer>();
		for (int i = 0; i < tsks.length - 1; i++) {
			String ds = dscode(tsks[i] , tsks[i + 1]);
			if (!dsintrace.containsKey(ds))
				dsintrace.put(ds, 1);
			else 
				continue;
			if (dses.containsKey(ds)) {
				dses.get(ds).add(instanceid);
			} else {
				ArrayList<String> ids = new ArrayList<String>();
				ids.add(instanceid);
				dses.put(ds, ids);
			}
		}
		observeddses=dses.size();
		super.counttrace(instanceid, newinstanceid, tasks);
		if(this.esinstances.size()>0){
			calcStatDelta(trace);
			estimates(this);
		}

	}

	@Override
	public void calcStat() {
		dsfreqfreqs.clear();
		observeddses=0;
		for (Iterator<String> itr = dses.keySet().iterator(); itr.hasNext();) {
			String key = itr.next();
			ArrayList<String> ids = dses.get(key);
			// System.out.println(""+ids.size()+"\t"+key);
			int idfreq = ids.size();
			int ff = 1;
			if (dsfreqfreqs.containsKey(idfreq)) {
				ff = ff + dsfreqfreqs.remove(idfreq);
			}
			dsfreqfreqs.put(idfreq, ff);
			observeddses += 1;
		}
		super.calcStat();
	}

	@Override
	public void displayEstimation() {
		System.out.println("total DS:" + this.observeddses);
		super.displayEstimation();
	}

	@Override
	public void displayStat() {
		StringBuffer sb = new StringBuffer();
		sb.append("file=").append(this.getFileName());
		sb.append(",\tloglen=").append(this.loglength);
		sb.append(",\tclasses=").append(this.observedclasses);
		sb.append(",\tdses=").append(this.observeddses);
		sb.append(",\t").append(
				(this.observeddses >= this.observedclasses ? "" : "OK"));

		System.out.println(sb.toString());
	}
	@Override
	public int getNumOfObservedUnits() {
		return this.observeddses;
	}
	@Override
	public void setNumOfObservedUnits(int units) {
		this.observeddses=units;
	}
	@Override
	public Hashtable<String, ArrayList<String>> getObservedUnits() {
		return this.dses;
	}
	/**
	 * Check the number of observed DS relations that appearing $freq$ times
	 * exactly.
	 * 
	 * @param freq
	 *            the appearing time of an observed trace class.
	 * @return the number of observed trace classes.
	 */
	public int getFreqFreq(int freq) {
		if (dsfreqfreqs.containsKey(freq))
			return dsfreqfreqs.get(freq);
		return 0;
	}
}
