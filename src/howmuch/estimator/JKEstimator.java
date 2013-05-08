package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.StatRes;

/**
 * Jackknife Estimator.
 * <p> refer to [CandolfiSastri2004]
 * 
 * p(m,n)=p*(p-1)**(p-m+1)=m!/(m-n)!
 * c(m,n)=p(m,n)/n!=m!/((m-n)!n!)
 * @author hedong
 *
 */
public class JKEstimator extends BaseEstimator{
	public  JKEstimator(){
		super("JK");
	}

	private int k=2;
	public void setK(int k){
		this.k=k;
	}
	public void estimate(StatRes res){
		successful=false;
		double sum=0;
		int coef=-1;
		double powerj=1;
		double pkj=1;
		for(int j=1;j<=k;j++){
			coef=coef*(-1);
			powerj=powerj*j;
			pkj=pkj*(k-j+1);
			sum+=coef*pkj/powerj*res.getFreqFreq(j);
		}
		res.setW(Math.round(sum));
		res.setU(res.getW()-res.getNumOfObservedUnits());
		
		/**
		 * The coverage and the probability of new unit class are the consequent results of W/U.
		 */
		res.setC(1.0*res.getNumOfObservedUnits()/res.getW());
		res.setN(1.0-res.getC());

		successful=true;
	}
}
