package howmuch;
/**
 * Demonstrate program of log analysis: Trace coverage probability/Informative completeness of event logs.
 * 
 * <P> The demo will parse the given logs, calculate the occurrence frequencies of traces, the estimate 
 * coverage probability of observed trace classes by means of different approaches.
 * 
 * <P> Coverage probability estimator: MLE, GT, ACE, BRKBayesian.
 *  
 * <p> NOTE: Logs in mxml format are supported only till now.
 * 
 * <p>Written by Hedong Yang.
 * <p>March 29, 2011
 */


import howmuch.annotations.AnEstimator;
import howmuch.estimator.Estimator;
import howmuch.parse.EstimatorConfigure;
import howmuch.parse.LogAnalysis;
import howmuch.parse.StatRes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Set;
import java.io.File;
import java.lang.reflect.Constructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 * An estimation task.
 * 
 * <P> It will parse the file names with wild character, e.g. '*' and '?', call the log parser with a file iteratively, and 
 * call estimators as specified.
 * 
 * @author hedong
 * 
 */

public abstract class PopEstimation implements PopEstimators{
	static Log log=LogFactory.getLog(PopEstimation.class);
	//the configuration of the estimation.
	EstimatorConfigure config;
	//Estimators for the estimation task.
	ArrayList<Estimator> estimators;
	//Query an estimator by its name based on the hashtable.
	Hashtable<String,Estimator> hashedEstimators;
	//the log file(s) for the estimation task. 
	String logfile;
	/**
	 * constructor.
	 * @param config configuration
	 */
	public PopEstimation(EstimatorConfigure config){
		this.config=config;
		
		estimators=new ArrayList<Estimator>();
		hashedEstimators=new Hashtable<String,Estimator>();
		if(config!=null){
			this.logfile= config.getLogFiles();
			config.setEstimation(this);
		}
	}
	/**
	 * register an estimator.
	 * @param estmtr an estimator instance
	 */
	public void registerEstimator(Estimator estmtr){
		if(hashedEstimators.containsKey(estmtr.name())){
			log.warn("Duplicated estimator registration !"+estmtr.name());
		}else{
			estimators.add(estmtr);
			hashedEstimators.put(estmtr.name(),estmtr);
		}
	}
	/**
	 * cancel the registration of an estimator.
	 * @param estmtr the estimator instance.
	 * @return successful/false
	 */
	public boolean unregisterEstimator(Estimator estmtr){
		hashedEstimators.remove(estmtr.name());
		return estimators.remove(estmtr);
	}
	/**
	 * Register an estimator by means of its constructor.
	 * @param cstr the constructor of the estimator
	 * @param paraValues the parameter values of the constructor
	 * @return the estimator instance.
	 * @throws Exception
	 */
	private Estimator registerEstimatorWithConstructor(Constructor<?>cstr,Object[]paraValues) throws Exception{
		Estimator estr= (Estimator) cstr.newInstance(paraValues);
		registerEstimator(estr);
		return estr;
	}
	/**
	 * Get the correct constructor of an estimator, then register the estimator and its variants if available.
	 * @param cls	an estimator class
	 * @param paraTypes	the sequence of parameter types for the constructor of the estimator.
	 * @param paraValues the sequence of parameter values for the constructor of the estimato.
	 * @return an estimator instance
	 * @throws Exception
	 */
	private Estimator registerEstimatorInstance(Class<?> cls,Class<?>[] paraTypes,Object[] paraValues) throws Exception{
		Constructor<?> cstr = null;
		try {
			cstr = cls.getDeclaredConstructor(paraTypes);
		} catch (NoSuchMethodException e) {
			//log.warn(e.toString());
			return null;
		}

		Estimator estr= registerEstimatorWithConstructor(cstr,paraValues);
		if(estr.variants()==null || estr.variants().length<1)
			return estr;
		
		//the variants with a name string as the last element of the sequence of parameters for the constructor. 
		Class<?>[] typesWithName=new Class[paraTypes.length+1];
		Object[] valuesWithName=new Object[paraValues.length+1];
		for(int i=0;i<paraTypes.length;i++){
			typesWithName[i]=paraTypes[i];
			valuesWithName[i]=paraValues[i];
		}
		typesWithName[paraTypes.length]=java.lang.String.class;
		cstr = cls.getDeclaredConstructor(typesWithName);
		for(String variant: estr.variants()){
			valuesWithName[paraValues.length]=variant;
			registerEstimatorWithConstructor( cstr,valuesWithName);
		}
		return estr;
	}
	/**
	 * Register the list of estimator class one by one.
	 * <P> By means of reflection, the constructor with a parameter "EstimatorConfigure" or without parameters
	 * are looked for to initialize a estimator. 
	 * @param estimatorClasses
	 * @param config
	 */
	private void annotatedClasses(Set<Class<?>> estimatorClasses,EstimatorConfigure config){
		for (Class<?> clazz : estimatorClasses) {
			try {
				Estimator estr = null;
				//log.info("try to initialize estimator "+clazz.getName());
				estr=registerEstimatorInstance(clazz,new Class[] {
						EstimatorConfigure.class}, new Object[] {config});
				if(estr==null){
					estr=registerEstimatorInstance(clazz,new Class[] {
							}, new Object[] {});
				}
				if(estr==null){
					log.error("no constructor for the estimator "+clazz.getName());
				}
			}catch(Exception e){
				log.warn(e.toString());
			}
		}
	}
	/**
	 * Register all estimators available automatically.
	 * <P> All these estimators should be annotated with "@AnEstimator". 
	 * @param config configuration
	 */
	public void registerEstimators(EstimatorConfigure config){
		ConfigurationBuilder confbuild=new ConfigurationBuilder().setUrls(ClasspathHelper.forJavaClassPath());
		Reflections reflections = new Reflections(confbuild);
		Set<Class<?>> estimatorClasses = reflections.getTypesAnnotatedWith(AnEstimator.class);
		annotatedClasses(estimatorClasses,config);
		Collections.sort(estimators, new Comparator<Estimator>() {
			public int compare(Estimator o1, Estimator o2) {
				return o1.name().compareTo(o2.name());
			}
		});

	}

	/**
	 * Initial the class for the mid- and final result.
	 * <P> Each log file has a corresponding instance of the class, which is initialized before starting to process the log.
	 * <P> Note the type of log completeness is determined by the type of result class.
	 * @param config configuration
	 * @return the instance of the result class
	 */
	public abstract StatRes initialStatTrace(EstimatorConfigure config);
	/**
	 * return the estimators available.
	 */
	public ArrayList<Estimator> getEstimators(){
		return this.estimators;
	}
	/**
	 * Get an estimator with specified name.
	 * @param est the estimator name.
	 * @return an estimator
	 */
	public Estimator getEstimator(String est){
		return hashedEstimators.get(est);
	}
	/**
	 * Keep those interesting estimators in the task only.
	 * The required estimators required in the task are listed in the configuration file.
	 * @param req a list of required estimators
	 * @return
	 */
	public boolean align(ArrayList<String> req){
		log.info("estimator alignment: require "+req.size()+" and available "+estimators.size()+" estimators");
		ArrayList<String> rem=new ArrayList<String>();
		for(String est:hashedEstimators.keySet()){
			if(!req.contains(est)){
				rem.add(est);
			}
		}
		for(String est:rem){
			unregisterEstimator(getEstimator(est));
		}
		log.info("after estimator alignment, only "+estimators.size()+" estimators left");
		return true;
	}
	/**
	 * Process a log file and Estimate population information based on the log.
	 * 
	 * @param logFile the name of the log file
	 * @param config the configuration
	 */
	public void processLog(String logFile, EstimatorConfigure config){
		//IMPORTANT: change statres of loganalysis, and 
		//IMPORTANT: change the esitmators.
		log.info("parsing "+logFile);
		config.set("currentLog", logFile);
		StatRes res= initialStatTrace(config);
		LogAnalysis ls = new LogAnalysis(res);
		//parse all process instances of process in a log.
		res=ls.execut();

		//display statistical information
		System.out.println(String.format("%s\tlen=%d\tunits=%d\te=%f\tk=%f",
				logFile,res.getLogLength(),res.getNumOfObservedUnits(),config.getEpsilon(),
				config.getConfidenceLevel()));
		//estimate based on final statistical results.
		for(Estimator estmtr:estimators){
			res.initial4Estimating();
			estmtr.estimate(res);
			System.out.println(estmtr.name()
					+ "\tobserved Coverage="
					+ res.getC()
					+ "\tNew class prob="
					+ res.getN()
					+ "\tWall possible classes="
					+ res.getW()
					+ "\tUnobserved  classe="
					+ res.getU()
					+ "\texpected Length=" + res.getL());
		}
	}
	/**
	 * Evaluating the log completeness of event logs.
	 * @param config
	 */
	public void processLogs( EstimatorConfigure config){
		processLogs(config.getLogFiles(),config,config.getString("task"));
	}
	/**
	 * Process a set of event log files iteratively.
	 * <p> The set of event logs may contain one or more files.
	 * If it contains more files, the set can be described as a directory name or a wild-cast name such as bala*.mxml.
	 * @param name	the set of event log files.
	 * @param config	the configuration
	 * @param task	the task name
	 */
	void processLogs(String name, EstimatorConfigure config,String task) {
		//If the set contains some log files rather than one, 
		//we will find all files in the same directory and the
		//filter out those needed.
		int pos = name.lastIndexOf(File.separator);
		String purefile = name.substring(pos + 1);
		String purepath = name.substring(0, pos);
		if (purepath.length() < 1)
			purepath = "." + File.separator;
		String pattern = "*";
		java.util.regex.Pattern p = null;
		if (purefile.contains("*") || purefile.contains("?")) {
			name = purepath;
			pattern = purefile.replace("*", ".*").replace("?", ".?");
			p = java.util.regex.Pattern.compile(pattern);
		}
		
		File f = new File(name);
		if (!f.exists()){
			log.warn("NOT EXISTS:"+name);
			return;
		}
		if (f.isFile()) {//One log file only
			processLog(f.getAbsolutePath(),config);
		} else {
			if (f.isDirectory()) {//Some log files
				File[] fList = f.listFiles();
				for (int j = 0; j < fList.length; j++) {
					if (fList[j].isFile()) {
						java.util.regex.Matcher m = p.matcher(fList[j].getName());
						if (!m.matches())
							continue;
						// To process log files being filtered out.
						log.info(String.format("%s(%d/%d):", task, j+1,fList.length));
						processLog(fList[j].getAbsolutePath(), config);
					} else if (fList[j].isDirectory()) {
						processLogs(fList[j].getAbsolutePath()+File.separator+purefile, config,task);
					}
				}
			}
		}
	}
	/**
	 * Output the usage of the program.
	 */
	public static void Usage(){
		System.out.println("Usage:");
		System.out.println("java PopEstimation <log file(s)> [epsilon]");
		System.out.println("java PopEstimation <log file(s)> <epsilon> [confidence]");
		System.out.println("for example: java LogAnalysis \"/home/hedong/Logs/LogExamplesXML/Logs/*12f0n00*.xml\"");
		System.out.println("NOTE: logs in MXML format are supported only.");
		System.exit(1);
	}
}
