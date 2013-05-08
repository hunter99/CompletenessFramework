package howmuch.parse;
/**
 * A trace will be tagged as a set of task, and the task set is the information unit for the evaluation of the log completeness.
 * @author hedong
 *
 */
public class StatTaskSet  extends StatClassifierCoverage implements StatRes {
	public StatTaskSet(EstimatorConfigure config) {
		super(config);
	}
	@Override
	public StringBuffer tracetag(StringBuffer tasks) {
		initialSet();
		//take a task from the tail of a string
		int start=tasks.lastIndexOf(",");
		while(start>-1){
			String task=tasks.substring(start+1);
			putItem(task);
			tasks.setLength(start);
			start=tasks.lastIndexOf(",");
		}
		if(tasks.length()>0){
			putItem(tasks.toString());
		}
		tasks.setLength(0);
		return setCoding();
	}
	
}
