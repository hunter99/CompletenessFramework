package howmuch.estimator;

import howmuch.annotations.AnEstimator;
import howmuch.parse.StatRes;

/**
 * ACE estimation.
 * <p> refer to [chao1993ace] and [estimates]
 */
@AnEstimator
public class ACEEstimator extends BaseEstimator{
	public ACEEstimator(){
		super("ACE");
	}

	public void estimate(StatRes res){
		successful=false;
		int Nr=0,nr=0,n3=0;
		for (int i=1;i<11;i++){
			Nr+=res.getFreqFreq(i);
			nr+=i*res.getFreqFreq(i);
			n3+=i*(i-1)*res.getFreqFreq(i);
		}
		int n1=res.getFreqFreq(1);
		if(nr==0 || nr-n1==0 || nr-1==0){
			successful=false;
			return;
		}

		res.setC(1.0-n1*1.0/nr);
		res.setN(1.0-res.getC());
		double gamma2=Nr*1.0*n3/((nr-n1)*nr*(nr-1))-1;
		if(gamma2<0)gamma2=0;
		long nabund=0;
		for(int i=11;i<=res.getLogLength();i++){
			nabund+=res.getFreqFreq(i);
		}
		res.setW(Math.round(nabund+nr*Nr/(nr-n1)+nr*n1/(nr-n1)*gamma2));
		res.setU(res.getW()-res.getNumOfObservedUnits());
		successful=true;
	}
}
