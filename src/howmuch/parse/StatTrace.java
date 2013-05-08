package howmuch.parse;
import howmuch.annotations.AnStatRes;
import java.util.ArrayList;
import java.util.Hashtable;
/**
 * A trace is the information unit.
 * @author hedong
 *
 */
@AnStatRes
public class StatTrace extends StatResImpl implements StatRes {
	/**
	 * Constructor
	 * @param config configuration
	 */
	public StatTrace(EstimatorConfigure config) {
		super(config);
	}
	@Override
	public int getNumOfObservedUnits() {
		return this.observedclasses;
	}
	@Override
	public void setNumOfObservedUnits(int units) {
		this.observedclasses=units;
		
	}
	@Override
	public Hashtable<String, ArrayList<String>> getObservedUnits() {
		return this.traces;
	}
	/**
	 * Check the number of observed trace classes that appearing $freq$ times
	 * exactly.
	 * 
	 * @param freq
	 *            the appearing time of an observed trace class.
	 * @return the number of observed trace classes.
	 */
	@Override
	public int getFreqFreq(int freq) {
		if (freqfreqs.containsKey(freq))
			return freqfreqs.get(freq);
		return 0;
	}
	@Override
	public void counttrace(String instanceid, String newinstanceid, StringBuffer tasks) {
		String trace=tasks.toString();
		super.counttrace(instanceid, newinstanceid, tasks);
		if(this.esinstances.size()>0){
			calcStatDelta(trace);
			estimates(this);
		}
	}
}
