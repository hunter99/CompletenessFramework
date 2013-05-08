package howmuch.parse;

import howmuch.PopEstimation;
import howmuch.probability.TraceDistribution;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EstimatorConfigure {
	static Log log=LogFactory.getLog(EstimatorConfigure.class);
	public String[] lenEstimators = new String[] { "Log",
			" number of traces considered", "Observed number of classes",
			"MSE Observed number of classes",
			"Observed number of trace classes",
			"MSE Observed number of trace classes", "CPL1", "MSE CPL1", "CPL2",
			"MSE CPL2", "KZN1", "MSE KZN1", "KZN2", "MSE KZN2", "Lin",
			"MSE Lin", "Order", "MSE Order", "CPL3.01", "MSE CPL3.01",
			"CPL3.unit", "MSE CPL3.unit", "CPL3.avg", "MSE CPL3.avg",
			"CPL3.min", "MSE CPL3.min", "CPL3.max", "MSE CPL3.max", "CPL4.01",
			"MSE CPL4.01", "CPL4.unit", "MSE CPL4.unit", "CPL4.avg",
			"MSE CPL4.avg", "CPL4.min", "MSE CPL4.min", "CPL4.max",
			"MSE CPL4.max", "CPL5.01", "MSE CPL5.01", "CPL5.unit",
			"MSE CPL5.unit", "CPL5.avg", "MSE CPL5.avg", "CPL5.min",
			"MSE CPL5.min", "CPL5.max", "MSE CPL5.max",
			"Actual number of classes" };
	public String[] clsEstimators = new String[] { "Log",
			" number of traces considered", "Observed number of classes",
			"MSE Observed number of classes",
			"Observed number of trace classes",
			"MSE Observed number of trace classes", "ACE", "MSE ACE", "BRK",
			"MSE BRK", "CL1", "MSE CL1", "CL2", "MSE CL2", "CPL", "MSE CPLV2",
			"Chao1", "MSE Chao1", "Chao2", "MSE Chao2", "GS2", "MSE GS2",
			"JK1", "MSE JK1", "JK2", "MSE JK2", "JK3", "MSE JK3", "JK4",
			"MSE JK4", "JK5", "MSE JK5", "MLE", "MSE MLE", "TG", "MSE TG",
			"Actual number of classes", "CPL1", "MSE CPLV1" };
	public String[] cvgEstimators = new String[] { "Log",
			" number of traces considered", "Observed number of classes",
			"MSE Observed number of classes",
			"Observed number of trace classes",
			"MSE Observed number of trace classes", "Log",
			" number of traces considered", "Observed number of classes",
			"Actual OTC",
			"Actual Trace Coverage",
			"Actual Trace OTC",
			"Actual Coverage",
			"Observed coverage",
			"MSE Observed number of classes",
			"ACE",
			"MSE ACE",
			"BRK",
			"MSE BRK",
			// "CPL","MSE CPLV2","MLE","MSE MLE","TG","MSE TG","CPL1","MSE CPLV1"
			"CPL3.01", "MSE CPL3.01", "CPL3.unit", "MSE CPL3.unit", "CPL3.avg",
			"MSE CPL3.avg", "CPL3.min", "MSE CPL3.min", "CPL3.max",
			"MSE CPL3.max", "CPL4.01", "MSE CPL4.01", "CPL4.unit",
			"MSE CPL4.unit", "CPL4.avg", "MSE CPL4.avg", "CPL4.min",
			"MSE CPL4.min", "CPL4.max", "MSE CPL4.max", "CPL5.01",
			"MSE CPL5.01", "CPL5.unit", "MSE CPL5.unit", "CPL5.avg",
			"MSE CPL5.avg", "CPL5.min", "MSE CPL5.min", "CPL5.max",
			"MSE CPL5.max", "MLE", "MSE MLE", "TG", "MSE TG" };
	String cfg;
	Set<String> estimatorSet;
	XMLConfiguration config =null;
	PopEstimation estimation;
	public EstimatorConfigure() {
		
	}
	/**
	 * Set the corresponding estimation task.
	 * <P> typically the method is called in the constructor of the PopEstimation.
	 * @param estimation the estimation task.
	 */
	public void setEstimation(PopEstimation estimation){
		this.estimation=estimation;
	}
	/**
	 * Get the corresponding estimation task.
	 * @return the estimation task
	 */
	public PopEstimation getEstimation(){
		return (PopEstimation)estimation;
	}
	public boolean containsKey(String key){
		return config.containsKey(key);
	}

	public Double getDouble(String key){
		return config.getDouble(key);
	}
	public double getConfidenceLevel(){
		return getDouble("confidence");
	}
	public double getEpsilon(){
		return getDouble("epsilon");
	}

	public String getString(String key){
		return config.getString(key);
	}
	public String getLogFiles(){
		return getString("logfiles");
	}
	public String statResClass(){
		return getString("statres");
	}

	public int getInt(String key){
		return config.getInt(key);
	}
	
	public Object getObject(String key){
		return config.getProperty(key);
	}
	public Boolean getBoolean(String key){
		return config.getBoolean(key);
	}
	public ArrayList<String> uniqueEstimators(){
		ArrayList<String> ests=new ArrayList<String>();
		for(String e:estimatorSet){
			ests.add(e);
		}
		return ests;
	}
	private String[] list2array(List<Object> es){
		String[]res=new String[es.size()];
		for(int i=0;i<es.size();i++){
			String e=(String)es.get(i);
			if(e.startsWith("\"") && e.endsWith("\"")){
				e=e.substring(1,e.length()-1);
			}
			res[i]=e;
			estimatorSet.add(e);
		}
		return res;
	}
	public void set(String key,Object value){
		config.setProperty(key, value);
	}
	public void load(String file) throws Exception{
		parse(file);
	}
	public void parse(String file) throws Exception{
		cfg=file;
		config= new XMLConfiguration(file);
		List<Object> les = config.getList("length.es");
		List<Object> ves = config.getList("coverage.es");
		List<Object> ces = config.getList("class.es");
		estimatorSet = new HashSet<String>();
		lenEstimators=list2array(les);
		clsEstimators=list2array(ces);
		cvgEstimators=list2array(ves);
		//log.info("length:" + lenEstimators.length + "\nclass:" + clsEstimators.length	+ "\ncoverage:" + cvgEstimators.length);
	}
	Map<String,Double> dist;//生成的分布(trace, probability)
	boolean aligneddist=false;
	double[]BP,HOTELS,TI,CI;
	/**
	 * 设置各类分布下的概率
	 * balanced 
-----------------------
bus 0.5, plane 0.5
h1 0.5, h2 0.25, h3 0.25
t 0.5, T 0.5
c 0.5, C 0.5

unbalanced
b 0.6, p 0.4
h1 0.3, h2 0.21, h3 0.49
t 0.7, T 0.3
c 0.4, C 0.6

ex-unbalanced
b 0.9, p 0.1
h1 .05, h2 .0475, h3 0.9025
t 0.9, T 0.1
c 0.05, C 0.95

	 * @param logtype
	 */
	public void setProbs(String logtype){
		if("B".equalsIgnoreCase(logtype)){
			BP=new double[]{0.5,0.5};
			HOTELS=new double[]{0.5,0.25,0.25};
			TI=new double []{0.5,0.5};
			CI=new double[]{0.5,0.5};
		} else if("U".equalsIgnoreCase(logtype)){
			BP=new double[]{0.6,0.4};
			HOTELS=new double[]{0.3,0.21,0.49};
			TI=new double []{0.7,0.3};
			CI=new double[]{0.4,0.6};
			
		} else if("E".equalsIgnoreCase(logtype)){
			BP=new double[]{0.9,0.1};
			HOTELS=new double[]{0.05,0.0475,0.9025};
			TI=new double []{0.9,0.1};
			CI=new double[]{0.05,0.95};			
		} else{
			System.exit(0);
		}
	}
	public Map<String,Double> genDistributionOfBUE(String logtype){
		log.info("gendistribution of BUE");
		setProbs(logtype);
		return genDistribution(BP,HOTELS,TI,CI);
	}
	public Map<String,Double> getTraceDistribution(){
		return this.dist;
	}
	public Map<String,Double> genDistribution(double[]BP,double[] HOTELS,double[]TI,double[]CI){
		dist=new HashMap<String,Double>();
//		String [] regs=new String[]{"Register"}; 
//		String [] hots=new String[]{"Hotel"};
//		String [] tcs=new String[]{"Trip Cost"};
		String [] bps=new String[]{"Bus","Plane"};
		String [] pays=new String[]{"Insurance Cost,Payment"};
		String [] tis=new String[]{"Trip Insurance",""};
		String [] cis=new String[]{"Trip Cancellation Insurance",""};
		int count=0;
		double countprob=0.0;
		for(int i=1;i<=3;i++){//hotel可能数
			for (int j=0;j<=i;j++){//bus或plane的位置
				if(j==3) continue;
				for (int bpi = 0; bpi < bps.length; bpi++) {//bus或plane
					String bp = bps[bpi];
					for (int tii = 0; tii < tis.length; tii++) {//trip insurance or not
						String ti = tis[tii];
						for (int cii = 0; cii < cis.length; cii++) {//trip cancellation insurance or not
							String ci = cis[cii];
							for (int kk = 0; kk <2; kk++) {//trip insurance与 cancellation insurance 的可能排列数

								StringBuffer sb = new StringBuffer("Register");

								double probs = 1.0;

								// hotel || bus or plane
								if (j == 0) {
									sb.append(",").append(bp);
									for (int t = j; t < i; t++) {
										sb.append(",").append("Hotel");
									}
								} else{
									for (int t = 0; t < j; t++)
										sb.append(",").append("Hotel");
									sb.append(",").append(bp);
									for (int t = j; t < i; t++) {
										sb.append(",").append("Hotel");
									}
								}
								probs *= BP[bpi];
								probs *= HOTELS[i - 1];
								if (i == 3)
									probs /= 3;
								else
									probs = probs / (i + 1);

								sb.append(",").append("Trip Cost");

								// trip || cancellation
								if (kk == 0) { // trip first
									if (ti.length() > 0)
										sb.append(",").append(ti);
									if (ci.length() > 0)
										sb.append(",").append(ci);
								} else { // cancellation first
									if (ci.length() > 0)
										sb.append(",").append(ci);
									if (ti.length() > 0)
										sb.append(",").append(ti);
								}

								probs *= 0.5;
								probs *= TI[tii];
								probs *= CI[cii];

								sb.append(",").append(pays[0]);
								String traceclass = sb.toString();
								if (dist.containsKey(traceclass)) {
									probs += dist.get(traceclass);
//									System.out.println("!duplicated!");
//									System.out.println(String.format("%3d.%1.6f %s i=%d,j=%d,bpi=%d,tii=%d,cii%d,kk=%d",
//											count, probs, traceclass,i,j,bpi,tii,cii,kk));
								}
								dist.put(traceclass, probs);
//								System.out.println(String.format("%3d.%1.6f %s i=%d,j=%d,bpi=%d,tii=%d,cii%d,kk=%d",
//										count, probs, traceclass,i,j,bpi,tii,cii,kk));
//								count++;
							}
						}
					}
				}
			}
		}
		return dist;
	}
	/**
	 * 对轨迹进行编码,然后据编码分组汇总出现概率。
	 * @param res
	 */
	public void alignDist(StatRes res){
		if(aligneddist)
			return;
		if(dist!=null){
			Set<String> keys=new TreeSet<String>();
			keys.addAll(dist.keySet());
			for(String key:keys){
				double prob=dist.remove(key);
				String code=res.tracetag(new StringBuffer().append(key)).toString();
				if(keys.contains(code)){
//					System.err.println("trace prob. distribution: key="+key+"==>code="+code+"!!DUPLICATED!");
//					System.exit(1);
				}
				double cum=0.0;
				if(dist.containsKey(code))
					cum=dist.remove(code);
				dist.put(code, prob+cum);
				//log.info(key+">>>"+code+":"+(prob+cum));
			}
			log.info("distribution aligned");
			aligneddist=true;
		}
	}
	public Map<String,Double> genDistribution(String distxml){
		dist=new HashMap<String,Double>();
		try{
			//log.info("before class initialization");
			TraceDistribution td=new TraceDistribution(distxml);
			//log.info("distribution xml before loading");
			td.load();
			//log.info("distribution xml after loading");
			ArrayList<String> traces=td.getTraces();
			int i=0;
			for(String trace:traces){
				i++;
				dist.put(trace, td.getTraceProb(trace));
				System.out.println(""+i+"."+td.getTraceProb(trace)+" "+trace);
			}
		}catch(Exception e){
			log.warn(e.toString());
		}
		log.info("distribution loaded."+distxml);
		return dist;
	}
	static String cmdline="";
	public static void setCmdLineSyntax(String cmd){
		cmdline=cmd;
	}
	public static void errCmd(Options options,String reason){
		System.err.println("Error:"+reason);
		Usage(options,1);
	}
	public static void Usage(Options options){
		Usage(options,0);
	}
	public static void Usage(Options options,int retnum){
		// automatically generate the help statement
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( cmdline, options );
		System.exit(retnum);
	}
}
