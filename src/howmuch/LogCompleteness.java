package howmuch;


import java.io.FileOutputStream;
import java.io.PrintStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reflections.util.ClasspathHelper;

import howmuch.parse.EstimatorConfigure;
import howmuch.parse.StatRes;
/**
 * The framework for evaluating the information completeness of event logs for process mining. 
 * All estimators, an algorithm to estimate the log completeness, can be implemented as plugins of the framework.
 * 
 * @author hedong
 *
 */
public class LogCompleteness extends PopEstimation {
	static Log log=LogFactory.getLog(LogCompleteness.class);
	/**
	 * The name of the class which deals with the statistical result as well as the estimation result of a given event log.
	 */
	String statResClass;
	/**
	 * set the statistical and estimation class name.
	 * @param cls
	 */
	public void setStatResClass(String cls){
		this.statResClass=cls;
	}
	/**
	 * The constructor.
	 * 
	 * @param config the configuration 
	 */
	public LogCompleteness(EstimatorConfigure config){
		super(config);
	}
	@Override
	public StatRes initialStatTrace(EstimatorConfigure config) {
		StatRes res = null;
		try {
			res = (StatRes)ClasspathHelper.contextClassLoader()
					.loadClass(this.statResClass)
					.getConstructor(new Class<?>[] { EstimatorConfigure.class })
					.newInstance(new Object[] { config });
			
		} catch (Exception e) {
			log.fatal(e.toString());
			System.exit(1);
		}
		if(getEstimators().size()<1){
			log.fatal("no estimators available for estimating the log completeness");
			System.exit(1);
		}
		//Set the estimators interested in the result class.
		res.addEstimators(getEstimators());
		
		return res;
	}
	/**
	 * Parse the command line options and the configuration file.
	 * The required command line option is "--config <file>" that specified the configuration file.
	 * And the required command line parameter is the mxml file(s).
	 * 
	 * Note the command line options will overwrite those corresponding settings in the configuration file.
	 * @param args the command line arguments.
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static EstimatorConfigure getConfig(String args[]){
		String syntax="java howmuch.LogCompleteness [-h] [--confidence level] [--epsilon value] [--tracedistribution file] [--unitdistribution file] [--realUnits number] [--realTraceClasses number] <--config configfile> <mxml files>";
		EstimatorConfigure.setCmdLineSyntax(syntax);
		//describe each ocommand line ptions.
		Options options = new Options();
		try{
			//help
			Option help = new Option( "h", "help",false, "print this message" );
			//confidence level
			Option confidence = OptionBuilder.withLongOpt("confidence")
				.withDescription("the confidence level").hasArg()
				.withArgName("level").create("l");
			//epsilon, the error rate.
			Option epsilon = OptionBuilder.withLongOpt("epsilon")
				.withDescription("the error ratio").hasArg()
				.withArgName("value").create("e");
			//configuration file
			Option configfile = OptionBuilder.isRequired().withLongOpt("config")
				.withArgName( "file" )
                .hasArg()
                .withDescription(  "the configuration file" )
                .create( "c");
			//output file
			Option outputfile = OptionBuilder.withLongOpt("output")
				.withArgName( "file" )
                .hasArg()
                .withDescription(  "the output file" )
                .create( "o");
			//occurrence probability distributions of traces of the underline process model.
			Option tracedistribution=OptionBuilder.withLongOpt("tracedistribution")
				.withArgName( "file" )
                .hasArg()
                .withDescription(  "the occurrence distribution of traces" )
                .create( "T");
			Option unitdistribution=OptionBuilder.withLongOpt("unitdistribution")
					.withArgName( "file" )
	                .hasArg()
	                .withDescription(  "the occurrence distribution of units" )
	                .create( "U");
			Option realunits = OptionBuilder.withLongOpt("realUnits")
					.withDescription("the number of real units").hasArg()
					.withArgName("number").create("u");
			Option realclasses = OptionBuilder.withLongOpt("realTraceClasses")
					.withDescription("the number of real trace classes").hasArg()
					.withArgName("number").create("t");
			options.addOption(help);
			options.addOption(confidence);
			options.addOption(epsilon);
			options.addOption(configfile);
			options.addOption(outputfile);
			options.addOption(tracedistribution);
			options.addOption(unitdistribution);
			options.addOption(realunits);
			options.addOption(realclasses);
		
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse( options, args);
			if (cmd.hasOption("h")) {
				EstimatorConfigure.Usage(options);
			}
			
			EstimatorConfigure ec=new EstimatorConfigure();
			//the default values, only being valid when both the command line options and the configuration file do not contain
			//corresponding values.
			double ep=0.1;
			double cf=0.9;

			if(cmd.hasOption("config")){
				String conf=cmd.getOptionValue("config");
				ec.parse(conf);
			}else{
				EstimatorConfigure.errCmd(options,"an configuration file is required.");
			}
			if(cmd.hasOption("output")){
				String outputfilename=cmd.getOptionValue("output");
				ec.set("resultFileName", outputfilename);
			}
			if(cmd.hasOption("l")){
				cf=Double.parseDouble(cmd.getOptionValue("confidence"));
			}else{
				if(ec.containsKey("confidence"))
					cf=ec.getConfidenceLevel();
				else
					log.warn("confidence level with default value : "+cf);
			}
			ec.set("confidence", cf);
			if(cmd.hasOption("e")){
				ep=Double.parseDouble(cmd.getOptionValue("epsilon"));
			}else{
				if(ec.containsKey("epsilon"))
					ep=ec.getEpsilon();
				else
					log.warn("epsilon with default value : "+ep);
			}
			ec.set("epsilon", ep);

			if(cmd.hasOption("u")){
				ec.set("realUnits", Integer.parseInt(cmd.getOptionValue("u")));
			}
			if(cmd.hasOption("t")){
				ec.set("realTraceClasses", Integer.parseInt(cmd.getOptionValue("t")));
			}
			if(cmd.hasOption("U")){
				ec.set("unitdistribution", cmd.getOptionValue("unitdistribution"));
			}
			
			if(cmd.hasOption("T")){
				ec.set("tracedistribution", cmd.getOptionValue("tracedistribution"));
			}
			
			String[] params = cmd.getArgs();
	        if(params.length!=1){
	        	EstimatorConfigure.errCmd(options,"Mxml files required.");
	        }
	        ec.set("logfiles", params[0]);
	        
			if(ec.statResClass()==null){
				log.fatal("Please set the statresclass in the configuration file.\n"+
						" e.g.\n for global completeness:  <statres>howmuch.parse.StatTrace</statres>\n"+
						"for local completeness:<statres>howmuch.parse.StatDSTrace</statres>");
				System.exit(1);
			}
	        
			String tracedistributionxml=ec.getString("tracedistribution");
			if(tracedistributionxml!=null){
				ec.genDistribution(tracedistributionxml);
			}

	        return ec;
		}catch(ParseException e){
			EstimatorConfigure.errCmd(options,e.getLocalizedMessage());
		}catch(Exception e){
			log.warn(e.toString());
		}
		return null;
	}
	/**
	 * Evaluate the completeness of logs with specified arguments.
	 * <P>
	 * @param args
	 */
	public static void evaluateWithArgs(String[]args){
		EstimatorConfigure ec=null;
		try{
			//get the parameters and the configuration for the program.
			ec=getConfig(args);

			//set the name of the task.
			ec.set("task","LogCompleteness");
	        if(ec.containsKey("resultFileName")){
	        	ec.set("outputStream", new PrintStream(new FileOutputStream(ec.getString("resultFileName"))));
	        }
			LogCompleteness pe=new LogCompleteness(ec);
			//automatically registered all estimators (algorithms that estimating the log completenss) available.
			pe.registerEstimators(ec);
			
			//remove estimators that are not considered in the task.
			pe.align(ec.uniqueEstimators());
			
			//set the result class, which determinates the type of the information unit.
			pe.setStatResClass(ec.statResClass());
			
			//estimating completeness of logs one by one.
			pe.processLogs(ec);
		}catch(Exception e){
			log.warn(e.toString());
		}finally{
			if(ec!=null && ec.containsKey("outputStream")){
				PrintStream output=(PrintStream)ec.getObject("outputStream");
				output.flush();
				output.close();
				
			}
		}
		
	}
	/**
	 * Main program of estimating the information completeness of an event log for process mining.
	 * 
	 * @param args	arguments.
	 */
	public static void main(String[] args) {
		evaluateWithArgs(args);
	}

}
