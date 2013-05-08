package howmuch.parse;

import howmuch.annotations.AnStatRes;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
/**
 * DS is the informaiton unit.
 * 
 * @author hedong
 *
 */
@AnStatRes
public class StatDSFreqTrace extends StatResImpl implements StatRes {
	long count=0;
	long parsecost=0,countcost=0,estimatecost=0;
	public StatDSFreqTrace(EstimatorConfigure config){
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
	 * Get the observed DS relations and their corresponding traces.
	 * @return observed DS relations
	 */
	public Hashtable<String, ArrayList<String>> getDSes() {
		return this.dses;
	}

	@Override
	public void counttrace(String instanceid, String newinstanceid, StringBuffer tasks) {
		long parsestart=System.nanoTime();
		String trace=tasks.toString();
		String[] tsks = trace.split(",");
		
		//ArrayList<String> dsintrace = new ArrayList<String>();
		for (int i = 0; i < tsks.length - 1; i++) {
			String ds = tsks[i] + "," + tsks[i + 1];
//			dsintrace.add(ds);
//		}
//		for (String ds : dsintrace) {
			if (dses.containsKey(ds)) {
				dses.get(ds).add(instanceid);
			} else {
				ArrayList<String> ids = new ArrayList<String>();
				ids.add(instanceid);
				dses.put(ds, ids);
			}
		}
		super.counttrace(instanceid, newinstanceid, tasks);
		long countstart=System.nanoTime();
		parsecost+=countstart-parsestart;
		if(this.esinstances.size()>0){
			calcStatDelta(trace);
			long estimatestart=System.nanoTime();
			estimates(this);
			long estimatestop=System.nanoTime();
			countcost+=estimatestart-countstart;
			estimatecost+=estimatestop-estimatestart;
		}
		count++;
		if(count%2000==0){
			System.err.println(String.format("count:%d, calc:%d, estimate:%d",parsecost/2000,countcost/2000,estimatecost/2000));
			//count=0;
			parsecost=0;countcost=0;estimatecost=0;
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
