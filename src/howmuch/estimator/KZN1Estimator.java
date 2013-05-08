/**
 * 
 */
package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.EstimatorConfigure;
import howmuch.parse.StatDSFreqTrace;
import howmuch.parse.StatDSTrace;
import howmuch.parse.StatRes;

/**
 * Estimator proposed by K. M. van Hee, Zheng Li, and Nadalia
 * in "Is my event log complete? - A probabilistic approach to process mining" 
 * @author hedong
 *
 */
@AnEstimator
public class KZN1Estimator extends BaseEstimatorWithParameters {
	double []ybar=null;
	
	public KZN1Estimator(EstimatorConfigure config) {
		this(config,"KZN1");
	}
	public KZN1Estimator(EstimatorConfigure config,String name) {
		super(config,name);
		long did=Math.round(10*1.0/alpha);
		alpha=1.0*Math.round(alpha*did)/did;
	}
	void calculateYBar(double alhpa,int numunits,int loglength,StatRes res){
		ybar=new double[numunits];
		int index=0;
		for(String unit:res.getObservedUnits().keySet()){
			double yfreq=1.0*res.getObservedUnits().get(unit).size()/loglength;
			ybar[index]=yfreq;
			index++;
		}
	}
	private double sum(long k){
		double sum = 0;
		for (int i = 0; i < ybar.length; i++) {
			double temp = 1 - ybar[i];
			if (temp >= 1.0)
				temp = 0.999999999;
			double power = Math.pow(temp, k);
			sum += power;
		}
		return sum;
	}
	private void calculate(double alhpa,int numunits,int loglength,StatRes res){
		calculateYBar(alpha,numunits,loglength,res);
		double minybar=1.0;
		double maxybar=0.0;
		if(ybar.length>0){
			minybar=ybar[0];
			maxybar=ybar[0];
			if(Double.isNaN(ybar[0]))
				return;
		}
		for(int i=1;i<ybar.length;i++){
			if(Double.isNaN(ybar[i]))
				return;
			if(minybar>ybar[i])
				minybar=ybar[i];
			else if(maxybar<ybar[i])
				maxybar=ybar[i];
		}
		if(minybar>maxybar){
			System.err.println("minybar="+minybar+",maxybar="+maxybar+".Skipped.!");
			return;
		}
		long maxk=(int)Math.round(Math.log(alpha/ybar.length)/Math.log(1-minybar));
		long mink=(int)Math.round(Math.log(alpha/ybar.length)/Math.log(1-maxybar));
		while(sum(mink)<alpha){
			mink=mink/2;
			System.err.println(name()+":try a smaller mink="+mink+" (till 1)to make sum(mink)>alpha, when loglen="+loglength);
			if(mink<1)break;
		}
		while(sum(maxk)>alpha){
			maxk*=2;
			if(maxk<=0 || maxk>=Long.MAX_VALUE){
				maxk=Long.MAX_VALUE;
				break;
			}
			System.err.println(name()+":try a bigger maxk="+maxk+" (till long.MAX_VALUE)to make sum(maxk)<alpha, when loglen="+loglength);
		}
		long k=mink;
		while(true){
			long newk=(mink+maxk)/2;
			if(newk<=mink){
				break;
			}
			k=newk;
			double sum=sum(k);
			if(Double.isNaN(sum))
				return;
			if(sum<alpha){
				mink=k;
				continue;
			}
			if(sum>alpha){
				maxk=k;
				continue;
			}
			
			if(sum==alpha)
				break;
			if(sum<=alpha||k>maxk)
				break;
			else
				k++;
		}
		res.setL(k);
		successful= true;
		
	}
	public void estimate(StatRes res){
		if(res instanceof StatDSTrace || res instanceof StatDSFreqTrace){
			successful=false;
			//check the validity of epsilon value.
			if (alpha <= 0 || alpha >= 1) {
				System.out.println("alpha should be in (0,1)");
				return ;
			}
			int loglength = 0;
			int dses = 0;
			dses=res.getObservedUnits().size();
			loglength=res.getLogLength();
			calculate(this.alpha,dses,loglength,res);
			
		}else{
			successful=false;
		}
	}
}
