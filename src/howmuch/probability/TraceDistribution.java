/**
 * 
 */
package howmuch.probability;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


/**
 * The occurrence probability distribution of traces. 
 * @author hedong
 *
 */
public class TraceDistribution {
	static Log log=LogFactory.getLog(TraceDistribution.class);
	/**
	 * the file containing information of the distribution.
	 */
	private String filename;
	/**
	 * traces
	 */
	private ArrayList<String> traces;
	/**
	 * traces and their occurrence probability
	 */
	private Hashtable<String,Double>distribution;
	public TraceDistribution()throws Exception{
		this(null);
	}
	/**
	 * Constructor.
	 * @param distfile occurrence distribution file
	 * @throws Exception
	 */
	public TraceDistribution(String distfile)throws Exception{
		traces=new ArrayList<String>();
		distribution=new Hashtable<String,Double>();
		filename=distfile;
	}
	/**
	 * Clear the distribution information.
	 */
	public void clear(){
		traces.clear();
		distribution.clear();
	}
	/**
	 * Get the number of traces.
	 * @return number of traces
	 */
	public int getDistSize(){
		return this.distribution.size();
	}
	/**
	 * Get the occurrence probability of the specified trace.
	 * @param trace trace
	 * @return occurrence probability
	 * @throws Exception
	 */
	public double getTraceProb(String trace)throws Exception{
		if(!distribution.containsKey(trace))
			throw new Exception("invalid trace:"+trace);
		return distribution.get(trace);
	}
	/**
	 * Get the number index trace according to distribution file.
	 * @param ind index number
	 * @return trace
	 */
	public String getTraceByIndex(int ind){
		if(ind<0 || ind>=traces.size()) return null;
		return traces.get(ind);
	}
	/**
	 * Get all traces
	 * @return list of all traces
	 */
	public ArrayList<String> getTraces(){
		return traces;
	}
	/**
	 * Output a line.
	 * @param writer output writer
	 * @param line	string line
	 * @throws Exception
	 */
	private void writeLine(BufferedWriter writer,String line) throws Exception{
		writer.write(line);
		writer.newLine();
	}
	/**
	 * Store the generated occurrence probability distribution of traces.
	 * @throws Exception
	 */
	public void store() throws Exception{
		FileOutputStream fout=null;
		try {
			fout = new FileOutputStream(filename);
			store(fout);
			fout.flush();
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			if (fout != null) {
				fout.close();
			}
		}
	}
	/**
	 * Store the information of occurrence distribution into the specified output stream.
	 * @param out output stream
	 * @throws Exception
	 */
	public void store(OutputStream out)throws Exception{
		BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(out));
		writeLine(writer,"<traces>");
		for (int i=0;i<traces.size();i++){
			String seq=traces.get(i);
			double prob=distribution.get(seq);
			writeLine(writer,String.format("\t<trace id=\"%d\" seq=\"%s\" prob=\"%f\" />",i,seq,prob));
		}
		writeLine(writer,"</traces>");
		writer.flush();
		writer.close();
	}
	/**
	 * Load the distribution information from a file.
	 * @throws Exception
	 */
	public void load() throws Exception{
		if(this.filename==null)
			return;
		FileInputStream fin=null;
		try {
			fin = new FileInputStream(filename);
			load(fin);
		}catch(Exception e){
			log.warn(e.toString());
		} finally {
			if (fin != null)
				fin.close();
		}
	}
	/**
	 * parse the occurrence probability distribution stored in the input stream.
	 * The structure of the distribution information.
	 * <traces>
	 * 	<trace id="" seq="" prob="" />
	 *  ...
	 * </traces>
	 * @throws Exception
	 */
	public void load(InputStream in)throws Exception{
		SAXReader reader = new SAXReader();
		Document doc = reader.read(in);// .read(this.filename);
		Element root = doc.getRootElement();
		Element foo;
		if(!"traces".equalsIgnoreCase(root.getName())){
			throw new Exception("wrong file format.");
		}
		for (Iterator<?> i = root.elementIterator("trace"); i.hasNext();) {
			foo = (Element) i.next();
			String id=foo.attributeValue("id");
			String seq=foo.attributeValue("seq");
			String sprob=foo.attributeValue("prob");
			if(id==null||seq==null||sprob==null){
				log.warn("Attribute missing: id="+id+" seq="+seq+" prob="+sprob);
				continue;
			}
			double prob=Double.parseDouble(sprob);
			addTrace(seq,prob);
		}
		double sum=0;
		for(String trace:traces){
			sum+=distribution.get(trace);
		}
		if(Math.abs(sum-1.0)>1e-10){
			log.warn("error distribution with summation of probabilities equal to "+sum);
		}
	}
	/**
	 * Add a trace as well as its occurrence probability
	 * @param seq trace
	 * @param prob occurrence probability
	 */
	public void addTrace(String seq, double prob){
		if(distribution.containsKey(seq)){
			log.warn("duplicated sequence "+seq+" is ignored");
			return;
		}
		distribution.put(seq, prob);
		traces.add(seq);

	}
	/**
	 * display traces and their occurrence probability respectively.
	 */
	public void display()throws Exception{
		for(String trace:traces){
			System.out.println(trace+":"+this.getTraceProb(trace));
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		
		TraceDistribution td=new TraceDistribution("./conf/uniform.xml");
		td.load();
		td.display();
	}

}
