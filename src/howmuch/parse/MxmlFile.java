package howmuch.parse;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 *  mxml file processor.
 *  <UL>
 *  <LI> Parse a mxml file </LI>
 *  </UL>
 *  The typical order of calling methods to parse a mxml file is as following.
 *  <OL>
 *  <LI> mxml=new MxmlFile(filename);
 *  <LI> root=mxml.read();
 *  <LI> mxml.parse();
 *  <LI> mxml.close();
 *  </OL>
 *  <UL>
 *  <LI> Generate a mxml file</LI>
 *  </UL>
 *  The typical order of calling methods to create a mxml file is as following.
 *  <OL>
 *  <LI> mxml=new MxmlFile(writer);
 *  <LI> mxml.create();
 *  <LI> mxml.startProcess();
 *  <LI> mxml.startProcessInstance()
 *  <LI> mxml.addEvent()
 *  <LI> mxml.stopProcessInstance()
 *  <LI> mxml.stopProcess();
 *  <LI> mxml.close();
 *  </OL>
 * @author hedong
 *
 */
public class MxmlFile {
	static Log log=LogFactory.getLog(MxmlFile.class);
	//file name for reading/parsing a mxml file
	String fileName;
	//reader and doc are used to parse the mxml file.
	SAXReader reader ;
	Document doc ;
	//writer for creating a mxml file 
	private PrintWriter out=null;

	/**
	 * The state of the mxml file.
	 * <UL>
	 * <LI> state 0: (the default state) cannot read from nor write into
	 * <LI> state 1: write only
	 * <LI> state 2: read only
	 * </UL>
	 * <P> Check the state before reading or writing.
	 */
	private int fileState=0;
	/**
	 * Check whether the mxml file is writable.
	 * @return	true/false
	 * @throws Exception 
	 */
	private boolean mustBeWritable() throws Exception{
		if(fileState==1)return true;
		throw new Exception("Create first.");
	}
	/**
	 * Constructor.
	 * @param fileName a mxml file name
	 */
	public MxmlFile(String fileName){
		this.fileName=fileName;
	}
	/**
	 * Constructor.
	 * @param output the ouptut writer
	 */
	public MxmlFile(PrintWriter output){
		out=output;
	}
	/**
	 * Open a mxml file and get the root element.
	 * <P> Set the state of the mxml file readable only.
	 * @return	root element
	 * @throws Exception
	 */
	public Element read() throws Exception{
		if(fileState==1) throw new Exception("Cannot read before writing finished.");
		reader= new SAXReader();
		if(fileName.endsWith(".gz"))
			doc = reader.read(new BufferedInputStream(new GZIPInputStream(new FileInputStream(fileName))));
		else
			doc= reader.read(new BufferedInputStream(new FileInputStream(this.fileName)));
		Element root = doc.getRootElement();
		this.fileState=2;
		return root;
	}
	/**
	 * Parse the mxml file line by line.
	 * <P> Only four types of element are considered.
	 * <UL>
	 * <LI>Process, 
	 * <LI>ProcessInstance, 
	 * <LI>AuditTrailEntry,
	 * <LI>WorkflowModelElement
	 * </UL>
	 * <P>The StatRes.counttrace() will be called at each parsed process instance.
	 * <P>NOTE only the first process and its instances are parsed.
	 * 
	 * @param root	the root element
	 * @param res	the data structure for result.
	 * @return	the parse result, including the occurrence numbers of traces.
	 * @throws Exception
	 */
	public StatRes parse(Element root,StatRes res) throws Exception{
		//find the first process
		Element firstProcess=null;
		for (Iterator<?> i = root.elementIterator("Process"); i.hasNext();) {
			firstProcess = (Element) i.next();
			break;
		}
		if(firstProcess==null)throw new Exception("No process in log file");
		
		//parse all instances of the first process
		StringBuffer tasks = new StringBuffer(1024); //trace
		int count=0;
		long parsecost=0,countcost=0;//time costs for parsing and estimating(counting)
		for (Iterator<?> i = firstProcess.elementIterator("ProcessInstance"); i.hasNext();) {
			long parsestart=System.nanoTime();
			Element processInstance = (Element) i.next();
			String instanceID=processInstance.attributeValue("id");
			for (Iterator<?> j = processInstance.elementIterator("AuditTrailEntry"); j.hasNext();) {
				Element event = (Element) j.next();
				String eventName=event.elementText("WorkflowModelElement");
				if(tasks.length()>0)tasks.append(",");
				tasks.append(eventName);
			}
			long countstart=System.nanoTime();
			res.counttrace(instanceID, null, tasks);
			long countstop=System.nanoTime();
			parsecost+=countstart-parsestart;
			countcost+=countstop-countstart;
			count++;
			if(count%2000==0){
				log.info(String.format("%d process instances processed!",count));
				//count=0;
				//parsecost=0;countcost=0;
			}
		}
		log.info(String.format("total %d process instances processed!Average time cost:parse=%d,count&estimation=%d",count,parsecost/count,countcost/count));
		return res;
	}
	/**
	 * To create a mxml file.
	 * <P> The header/title of a mxml file will be printed out.
	 * @throws Exception
	 */
	public void create() throws Exception{
		if(fileState!=0)throw new Exception("Other reading/writing not finished yet.");
		if(out==null){

		if(fileName.endsWith(".gz")){
			out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(fileName)))));
		}else
			out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName))));
		}
		this.fileState=1;
		StringBuffer sb=new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<WorkflowLog description=\"Artificial workflow event log.\">\n");
		sb.append("\t<Source program=\"LogGenerator, Tsinghua, ISE, Hedong\">\n");
		sb.append("\t\t<Data />\n");
		sb.append("\t</Source>\n");
		out.print(sb.toString());

	}
	/**
	 * stop read or write a mxml file.
	 * @throws Exception
	 */
	public void close() throws Exception{
		if (fileState == 1) {
			out.println("</WorkflowLog>");
			out.flush();
			out.close();
		}else{
			if(doc!=null)
				doc.clearContent();
			doc=null;
		}
		fileState=0;
	}
	/**
	 * start a process during creating a mxml file.
	 * @param id	process id
	 * @param desc  process description 
	 * @throws Exception
	 */
	public void startProcess(String id,String desc) throws Exception{
		mustBeWritable();
		if(id==null) id="";
		if(desc==null)desc="";
		out.println("\t<Process id=\""+id+"\" description=\""+desc+"\">");
	}
	/**
	 * stop a process during creating a mxml file.
	 * @throws Exception
	 */
	public void endProcess() throws Exception{
		mustBeWritable();
		out.println("\t</Process>");
//		out.flush();
	}
	/**
	 * create a process instance during creating a mxml file.
	 * @param id	process instance id
	 * @param seq	list of events.
	 * @throws Exception
	 */
	public void newProcessInstance(String id,String[] seq) throws Exception{
		mustBeWritable();
		startProcessInstance(id,null);
		for(String evt:seq){
			addEvent(evt,null,null,null);
		}
		endProcessInstance();
	}
	
	/**
	 * start a process instance during creating a mxml file.
	 * @param id	process instance id
	 * @param desc	process instance description
	 * @throws Exception
	 */
	public void startProcessInstance(String id, String desc) throws Exception{
		mustBeWritable();
		if(id==null)throw new Exception("Process instance id is required.");
		if(desc==null)desc="";
		out.println("\t\t<ProcessInstance id=\""+id+"\" description=\""+desc+"\">");
	}
	/**
	 * stop a process instance during creating a mxml file.
	 * @throws Exception
	 */
	public void endProcessInstance() throws Exception{
		mustBeWritable();
		out.println("\t\t</ProcessInstance>");
//		out.flush();
	}
	/**
	 * adding an event during creating a mxml file.
	 * @param event		event name
	 * @param eventType	event type, e.g. complete or incomplete
	 * @param timeStamp	the happened time of an event
	 * @param operator	event performer
	 * @throws Exception
	 */
	public void addEvent(String event,String eventType,String timeStamp, String operator) throws Exception{
		mustBeWritable();
		if(event==null)throw new Exception("Event is required.");
		if(eventType==null)eventType="Completed";
		if(timeStamp==null)timeStamp="";
		if(operator==null)operator="";
		StringBuffer sb=new StringBuffer();
		sb.append("\t\t\t<AuditTrailEntry>\n");
		sb.append("\t\t\t\t<Data />\n");
		sb.append("\t\t\t\t<WorkflowModelElement>").append(event).append("</WorkflowModelElement>\n");
		sb.append("\t\t\t\t<EventType>").append(eventType).append("</EventType>\n");
		sb.append("\t\t\t\t<Timestamp>").append(timeStamp).append("</Timestamp>\n");
		sb.append("\t\t\t\t<Originator>").append(operator).append("</Originator>\n");
		sb.append("\t\t\t</AuditTrailEntry>\n");
		out.print(sb.toString());
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		String filename	="/data/tracedist.mxml";
		String filename2="/data/logs_B/log-1.mxml.gz";
		filename=filename2;
		EstimatorConfigure config=new EstimatorConfigure();
		StatRes sr=new StatTrace(config);
		MxmlFile mxml=new MxmlFile(filename);
		Element root=mxml.read();
		sr=mxml.parse(root, sr);
		sr.calcStat();
		sr.displayStat();
		sr.displayEstimation();
	}

}
