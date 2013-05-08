package howmuch;

import howmuch.estimator.Estimator;
import howmuch.parse.EstimatorConfigure;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * Parse the estimation result file and generate the final estimation values in format 'csv'.
 * <P> Each line of the estimation result file has a format as follows. 
 * <PRE>
 * 	log-18.mxml,L1,units=0,estmtr=ACE,coverageNaN,observedtraceclasses=1,CV=-1.0
 * </PRE>
 * Elements separated by commas are
 * <OL>
 * <LI> log file name,
 * <LI> the trace number,
 * <LI> units observed so far,
 * <LI> estimator,
 * <LI> estimated (probability coverage)/(number of unit classes)/(length) of observed units,
 * <LI> number observed trace classes, 
 * <LI> real probability coverage.
 * </OL>
 * <P> The output file is in the format of "csv", which can be loaded into some other ploting softwares, such as MS Excel or LibreOffice.
 * <P> If the res contains estimation results for more than one log file, the output are average values.
 * @author hedong
 *
 */
public class ParseEstimationResult {
	static Log log=LogFactory.getLog(ParseEstimationResult.class);
	/**
	 * The data structure to contains concerning information about a specific estimation targets.
	 * <P> Typical estimation targets are estimating the length of a complete log, estimating the probability coverage of the 
	 * observed units in a log, and estimating the number of all unit classes.
	 * @author hedong
	 *
	 */
	class EstimationTarget{
		/**
		 * The label of the target.
		 */
		String label;
		/**
		 * The prefix of the target, which is used to tell from other targets.
		 */
		String prefix;
		/**
		 * the id of the target.
		 */
		int id;
		/**
		 * The estimation values.
		 * <P> The structure of the matrix
		 * <Pre>
		 *		col0:units
		 *			col1:traceclasses
		 *				col2:CV
		 *					col3:estimator1
		 *					    col4:estimator2
		 *						....
		 * 		0	1	2	3	4
		 * 0(1st trace)	average=(sum/count)
		 * 1(2nd trace)
		 * 2(3rd trace)	
		 * </pre>
		 */
		double[][] estvals;
		/**
		 * counting times of the corresponding elements in the estvals matrix.
		 */
		int[][] counts;
		/**
		 * Estimator names available for the target.
		 */
		String[] estimators;
		/**
		 * an indicator to show whether the values has been averaged.
		 */
		boolean hasavg=false;
		/**
		 * constructor
		 * @param id	the target id
		 * @param label the label of the target
		 * @param prefix the prefix used for this target
		 * @param rows	row number of the matrix
		 * @param cols	column number of the matrix
		 * @param estmtrs	list of estimator names for the target
		 */
		public EstimationTarget(int id, String label,String prefix,int rows, int cols, String[] estmtrs){
			setID(id);
			setLabel(label);
			setPrefix(prefix);
			allocSpace(rows,cols);
			setEstimators(estmtrs);
		}
		public String prefix(){
			return this.prefix;
		}
		public void setPrefix(String prefix){
			this.prefix=prefix;
		}
		public void setLabel(String label){
			this.label=label;
		}
		public String label(){
			return this.label;
		}
		public int id(){
			return id;
		}
		public void setID(int id){
			this.id=id;
		}
		public String[] estimators(){
			return this.estimators;
		}
		public void setEstimators(String[]estrs){
			this.estimators=estrs;
		}
		public int[][] counts(){
			return this.counts;
		}
		public double[][]vals(){
			return this.estvals;
		}
		public void allocSpace(int rows, int cols){
			estvals=new double[rows][cols];
			counts=new int[rows][cols];
			for(int i=0;i<rows;i++){
				for(int j=0;j<cols;j++){				
					estvals[i][j]=0;
					counts[i][j]=0;
				}
			}

		}
		/**
		 * The maximum number of log length so far.
		 */
		int maxValidRow=0;
		/**
		 * The number of logs processed so far.
		 */
		int maxCount=0;
		/**
		 * The row to obtain the value of maxCount
		 */
		int maxCountI=0;
		/**
		 * The column to obtain the value of maxCount
		 */
		int maxCountJ=0;
		public int[] max(){
			return new int[]{maxValidRow,maxCount,maxCountI,maxCountJ};
		}
		/**
		 * Summarize the estimation results of different logs.
		 * @param i	the row index
		 * @param j the column index
		 * @param v the estimation value for a log
		 */
		public void increase(int i,int j,double v){
			estvals[i][j]+=v;
			counts[i][j]++;
			if(i>maxValidRow)
				maxValidRow=i;
			if(j>3 &&counts[i][j]>maxCount){
				maxCount=counts[i][j];
				maxCountI=i;
				maxCountJ=j;
			}
		}
		/**
		 * Average the estimation values among different logs.
		 */
		public void avg(){
			if(hasavg)
				return;
			hasavg=true;
			for(int i=0;i<estvals.length;i++){
				for(int j=0;j<estvals[0].length;j++){
					if(counts[i][j]!=0){
						estvals[i][j]/=counts[i][j];
					}
				}
			}
		}
	}
	/**
	 * The num of logs been parsed.
	 */
	int realNumOfLogs=0;
	/**
	 * The maximum num of lines in these parsed logs.
	 */
	int realNumOfLines=0;
	/**
	 * All possible estimation targets, here are length, coverage and classes.
	 */
	Map<Integer,EstimationTarget>targets;
	/**
	 * ID of the targets focusing on.
	 */
	final int CLS = 0, CVG = 1, LEN = 2;
	/**
	 * the union of estimators for all different targets.
	 * <P> Its value orginates from the estimation task.
	 */
	String[] estimators;
	/**
	 * mapping the estimator name to its code (the column index of the estimation value matrix).
	 */
	Map<String,Integer> ei=new HashMap<String,Integer>();
	/**
	 * Counting the number of parsed lines.
	 */
	int parsedlines=0;
	/**
	 * The configuration
	 */
	EstimatorConfigure config;
	/**
	 * Constructor.
	 * Get estimators in the configuration file, and initialize the estmation targets. 
	 * @param ec configuration
	 */
	public ParseEstimationResult(EstimatorConfigure ec){
		this.config=ec;
		//get estimators
		LogCompleteness ge=new LogCompleteness(config);
		if(ec.statResClass()==null){
			log.fatal("Please set the statresclass in the configuration file.\n e.g. <statres>howmuch.parse.StatDSTraceUnique</statres>");
			System.exit(1);
		}
		ge.setStatResClass(ec.statResClass());
		ge.registerEstimators(ec);
		ge.align(ec.uniqueEstimators());
		ArrayList<Estimator>es=ge.getEstimators();
		estimators=new String[es.size()];
		for(int i=0;i<es.size();i++){
			ei.put(es.get(i).name(), i+3);
			estimators[i]=es.get(i).name();
		}
		//initialize the estimation targets.
		int totallines=config.getInt("logLength");
		targets=new HashMap<Integer,EstimationTarget>();
		targets.put(CLS,new EstimationTarget(CLS,"Classes", "cls-",totallines,es.size()+3,ec.clsEstimators));
		targets.put(CVG,new EstimationTarget(CVG,"Coverage","cvg-",totallines,es.size()+3,ec.cvgEstimators));
		targets.put(LEN,new EstimationTarget(LEN,"Length",  "len-",totallines,es.size()+3,ec.lenEstimators));
		
	}
	private void outputValue(StringBuffer line,String title,int row,double[][]vals,int realunits,int realtraceclasses){
		if("Log".equals(title)){
			line.append("\"Aggregated "+realNumOfLogs+" logs\"");
		}else if(" number of traces considered".equals(title)){
			line.append(row+1);
		}else if("Observed number of classes".equals(title)){
			line.append(vals[row][0]);
		}else if("Observed number of trace classes".equals(title)){
			line.append(vals[row][1]);
		}else if("Actual number of classes".equals(title)){
				line.append(realunits);
		}else if("Actual number of trace classes".equals(title)){
			line.append(realtraceclasses);
		}else if("Actual OUR".equals(title)){
			line.append(vals[row][0]/realunits);
		}else if("Actual Trace OTC".equals(title)){
			line.append(vals[row][1]/realtraceclasses);
		}else if("Actual CV".equals(title)){
			line.append(vals[row][2]);
		}else if("Actual Trace Coverage".equals(title)){
			line.append(-1);
		}else if("Observed coverage".equals(title)){
			line.append(1);
		}else if("Observed trace coverage".equals(title)){
			line.append(1);
		}else if(title.startsWith("MSE")){
			line.append(-1);
		}else {
			String est = title;
			if (ei.containsKey(est)) {
				double v = vals[row][ei.get(est)];
				if (v < 0)
					line.append("\"NaN\"");
				else
					line.append(String.format("%1.3f",v));
			} else {
				log.warn("Unknown title:" + est);
			}
		}
	}
	/**
	 * Output the average estimation values in a whole file.
	 * @param out	output file
	 * @param realunits	the real number of units in the given log(s)
	 * @param realtraceclasses	the real number of traces class in the given log(s)
	 * @throws Exception
	 */
	public void outputAllEstimation(OutputStream out,int realunits,int realtraceclasses) throws Exception{
		StringBuffer line = new StringBuffer(4096);
		BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(out));
		String[] comontitle=new String[]{
				"Log",
				" number of traces considered",
//				"Actual Coverage",
				"Observed number of classes",
				"Actual number of classes",
//				"Observed coverage",
				"Observed number of trace classes",
				"Actual number of trace classes",
				"Actual CV",
				"Actual Trace Coverage"		
		};
		//output the title line of hte csv file.
		outputTitle(line,comontitle);
		ArrayList<double[][]> data=new ArrayList<double[][]>();
		for(Integer id:targets.keySet()){
			data.add(targets.get(id).vals());
			outputTitle(line,targets.get(id).estimators,targets.get(id).prefix());
		}

		writer.write(line.toString());
		writer.newLine();
		
		//output the values line by line. 
		for(int i=0;i<realNumOfLines;i++){
			line.setLength(0);
			//output the common content each line
			for(int j=0;j<comontitle.length;j++){
				if(line.length()>0)line.append(",");
				outputValue(line,comontitle[j],i,data.get(0),realunits, realtraceclasses);
			}				
			//output the estimated values by different estimators.
			for(int d=0;d<data.size();d++){
				double[][]vals=data.get(d);
				for(int j=0;j<estimators.length;j++){
					if(line.length()>0)line.append(",");
					outputValue(line,estimators[j],i,vals,realunits, realtraceclasses);
				}				
			}
			writer.write(line.toString());
			writer.newLine();
		}
		writer.flush();
		
	}
	private void outputTitle(StringBuffer line,String[]title,String prefix){
		for (String t : title) {
			if (line.length() > 0)
				line.append(",");
			line.append("\"");
			if(prefix!=null)
				line.append(prefix);
			line.append(t).append("\"");
		}
		
	}

	private void outputTitle(StringBuffer line,String[]title){
		outputTitle(line,title,null);		
	}
	private void output(int id, OutputStream out,int realunits,int realtraceclasses) throws Exception{
		output(targets.get(id).vals(),out,targets.get(id).estimators,realunits,realtraceclasses);
	}
	private void output(double[][]vals,OutputStream out, String[] title,int realunits,int realtraceclasses)throws Exception{
		StringBuffer line = new StringBuffer(4096);
		BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(out));
		
		outputTitle(line,title);
		writer.write(line.toString());
		writer.newLine();
		
		for(int i=0;i<vals.length;i++){
			if(i>parsedlines)break;
			line.setLength(0);
			for(int j=0;j<title.length;j++){
				if(line.length()>0)line.append(",");
				outputValue(line,title[j],i,vals,realunits, realtraceclasses);
			}
			writer.write(line.toString());
			writer.newLine();
		}
		writer.flush();
	}
	/**
	 * Parse the result file, and fill the matrix ( columns are estimators, and rows are increasing log length.
	 * @param ec configuration
	 * @throws Exception
	 */
	public void parse(EstimatorConfigure ec) throws Exception{
		BufferedReader br=null;
		try {
			br = new BufferedReader(new FileReader(ec.getString("resfile")));
			String line;
			while ((line = br.readLine()) != null) {

				if (line.length() < 1)
					continue;
				log.debug(line);
				//log-18.mxml,L1,units=0,estmtr=ACE,coverageNaN,observedtraceclasses=1,CV=-1.0
				// 0           1   2      3          4            5                      6
				String[] parts = line.split(",");
				if (parts.length < 5) {
					log.warn("error line:" + line);
					continue;
				}
				// log length
				int i = Integer.parseInt(parts[1].substring(1)) - 1;
				if (i > parsedlines)
					parsedlines = i;
				// number of observed unit classes
				int units = Integer.parseInt(parts[2].substring("units="
						.length()));
				// number of observed trace classes
				int otcs = Integer.parseInt(parts[5]
						.substring("observedtraceclasses=".length()));
				// real probability coverage
				double cvs = Double.parseDouble(parts[6].substring("CV="
						.length()));

				// name of the estimator.
				String est = parts[3].substring("estmtr=".length());
				if (est.equals("BK"))
					est = "BRK";
				if (!ei.containsKey(est)) {
					log.warn("estimator:" + est+" in res but be not interested");
					continue;
				}
				// the estimator's corresponding column index for the matrix
				// vals.
				int j = ei.get(est);

				int isclasses = -1;
				String vs = null;
				if (parts[4].startsWith("coverage")) {
					vs = parts[4].substring("coverage".length());
					isclasses = CVG;
				} else if (parts[4].startsWith("classes")) {
					vs = parts[4].substring("classes".length());
					isclasses = CLS;
				} else if (parts[4].startsWith("length")) {
					vs = parts[4].substring("length".length());
					isclasses = LEN;
				} else {
					log.warn("Skip the Unkown estimation result:" + parts[4]);
					continue;
				}

				// the estimated value
				double v = Double.parseDouble(vs);
				if (v < 0)
					continue;
				targets.get(isclasses).increase(i, 0, units);
				targets.get(isclasses).increase(i, 1, otcs);
				targets.get(isclasses).increase(i, 2, cvs);
				targets.get(isclasses).increase(i, j, v);
				
			}
		} finally {
			if(br!=null)
				br.close();
		}
		//average the estimation results when mutilple logs,
		for(Integer id:targets.keySet()){
			EstimationTarget target=targets.get(id);
			target.avg();
			//get the maximum length of logs
			if(realNumOfLines<target.maxValidRow){
				realNumOfLines=target.maxValidRow;
			}
			//get the number of logs.
			if(realNumOfLogs<target.maxCount){
				realNumOfLogs=target.maxCount;
			}
		}
	}
	/**
	 * Output the final average estimation values.
	 * <P> Each estimation target will have one unique csv file. The file is in the same directory as the result file with name format 
	 * like '<target-label>+resfilename+".csv"'.
	 * <P> And also there is a file contains all these estimation values.
	 * @param config configuration.
	 * @throws Exception
	 */
	public void output(EstimatorConfigure config) throws Exception{
		int realunits = config.getInt("realUnits");
		int realtraceclasses= config.getInt("realTraceClasses");
		String resfile=config.getString("resfile");
		File res=new File(resfile);
		String file=res.getName();
		String path=res.getParent()+File.separator;
				
		for(Integer id:targets.keySet()){
			EstimationTarget target=targets.get(id);
			FileOutputStream out=new FileOutputStream(path+target.label()+file+".csv");
			output(id,out,realunits,realtraceclasses);
			out.close();
		}

		FileOutputStream allout=new FileOutputStream(path+"AllInOne"+file+".csv");
		this.outputAllEstimation(allout,realunits,realtraceclasses);
		allout.close();

	}
	/**
	 * @param args
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws Exception{
		EstimatorConfigure.setCmdLineSyntax("java howmuch.ParseEstimationResult");
		log.info("generating option descrition");
		Options options = new Options();

		Option help = new Option( "h", "help",false, "print this message" );
		Option realunits = OptionBuilder.withLongOpt("realUnits")
				.withDescription("the number of real units").hasArg()
				.withArgName("number").create("u");
		Option realclasses = OptionBuilder.withLongOpt("realTraceClasses")
				.withDescription("the number of real trace classes").hasArg()
				.withArgName("number").create("t");
		Option loglength = OptionBuilder.withLongOpt("logLength")
				.withDescription("the log length").hasArg()
				.withArgName("number").create("n");

		Option configfile = OptionBuilder.isRequired().withLongOpt("config")
				.withArgName( "file" )
                .hasArg()
                .withDescription("the configuration file" )
                .create( "c");
		Option tracedistribution=OptionBuilder.withLongOpt("distribution")
				.withArgName( "file" )
                .hasArg()
                .withDescription(  "the occurrence distribution of traces" )
                .create( "d");
		options.addOption(help);
		options.addOption(realunits);
		options.addOption(realclasses);
		options.addOption(loglength);
		options.addOption(configfile);
		options.addOption(tracedistribution);
		
		CommandLineParser parser = new PosixParser();
		try{
			log.info("parse command");
			CommandLine cmd = parser.parse( options, args);
			if (cmd.hasOption("h")) {
				EstimatorConfigure.Usage(options);
			}
			
			EstimatorConfigure ec=new EstimatorConfigure();

			if(cmd.hasOption("config")){
				String conf=cmd.getOptionValue("config");
				ec.parse(conf);
			}else{
				EstimatorConfigure.errCmd(options,"an configuration file is required.");
			}
			if(cmd.hasOption("u")){
				ec.set("realUnits", Integer.parseInt(cmd.getOptionValue("u")));
			}
			if(cmd.hasOption("t")){
				ec.set("realTraceClasses", Integer.parseInt(cmd.getOptionValue("t")));
			}
			int lines=15000;
			if(cmd.hasOption("n")){
				lines= Integer.parseInt(cmd.getOptionValue("n"));
			}
			ec.set("logLength", lines);
			if(cmd.hasOption("d")){
				ec.set("tracedistribution", cmd.getOptionValue("d"));
			}
			
			String[] params = cmd.getArgs();
	        if(params.length!=1){
	        	EstimatorConfigure.errCmd(options,"estimation result file required.");
	        }
	        String resfile=params[0];
	        ec.set("resfile", resfile);
	        log.info("initial parseEstimation result class.");
			ParseEstimationResult per =new ParseEstimationResult(ec);		
	        log.info("parse result file");
			per.parse(ec);
			log.info("output the parse result");
			per.output(ec);
		}catch(ParseException e){
			EstimatorConfigure.errCmd(options,e.getLocalizedMessage());
		}catch(Exception e){
			System.err.println(e.toString());
		}
	}
}
