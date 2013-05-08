package howmuch.parse;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A log file parser.
 * 
 * <P> Only MXML format log files supported.
 * 
 * @author hedong
 *
 */

public class LogAnalysis {
	static Log log=LogFactory.getLog(LogAnalysis.class);
	/**
	 * The analysis and statistical result.
	 */
	StatRes res;

	/**
	 * Initialization of an instance of the LogAnalysis
	 * 
	 * @param res the object to contain the analysis result
	 */
	public LogAnalysis(StatRes res) {
		this.res=res;
	}

	/**
	 * The main procedure of estimating the informative completeness of an event log.
	 * 
	 * <P> ATTENTION: only logs mxml format are supported.
	 * <ul>
	 * <li>Parse the log file and do some statistics
	 * <li>Estimate the informative completeness of logs and output the results.
	 * </ul>
	 */
	public StatRes execut() {
		if (res.getFileName().endsWith(".xml") || res.getFileName().endsWith(".mxml")) {
			parseLog2();
			res.calcStat();
		}
		return res;
	}
	/**
	 * Parse a mxml file in another way.
	 */
	public void parseLog2(){
		try{
			MxmlFile eventlog=new MxmlFile(res.getFileName());
			//log.info("processing "+res.getFileName());
			res=eventlog.parse(eventlog.read(), res);
			eventlog.close();
		}catch(Exception e){
			log.warn(e.toString());
		}
	}
}
