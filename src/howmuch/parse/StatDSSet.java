package howmuch.parse;

/**
 * A trace is tagged as a set of DS relations, the DS set is the information unit.
 * @author hedong
 *
 */
public class StatDSSet  extends StatClassifierCoverage implements StatRes {
	public StatDSSet(EstimatorConfigure config){
		super(config);
	}
	@Override
	public StringBuffer tracetag(StringBuffer tasks) {
		initialSet();
		//take a task from the tail of a string.
		String lasttask="1";
		int start=tasks.lastIndexOf(",");
		while(start>-1){
			String task=tasks.substring(start+1);
			putItem(task+","+lasttask);
			tasks.setLength(start);
			lasttask=task;
			start=tasks.lastIndexOf(",");
		}
		if(tasks.length()>0){
			putItem(tasks.toString()+","+lasttask);
			putItem("0,"+tasks.toString());
		}
		tasks.setLength(0);
		return setCoding();
	}
	
}
