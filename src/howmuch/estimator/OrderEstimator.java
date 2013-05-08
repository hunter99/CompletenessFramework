/**
 * 
 */
package howmuch.estimator;

import java.util.ArrayList;
import java.util.Hashtable;

import howmuch.annotations.AnEstimator;
import howmuch.parse.EstimatorConfigure;
import howmuch.parse.StatRes;

/**
 *  首先，我们假设观察到了战场上面的k=4个坦克，编号为2,6,7,14，则观察到的最大编号为m=14，问总共有多少坦克（N）？
 *  其频率分布表明：N=m(1+1/k)-1=16.5上述公式是点估计中最小方差无偏估计量，具体推导过程摘自wikipeidia
 *  
 *  一个历史时期内，世界上发生的战争的数目是遵循珀松分布的, 而战争的伤亡数据是遵循幂律分布，所以是长尾分布，这个分布搞互联网的人都熟悉。
 *  注意: 此方法中,每个unit的ID就是整数编号.
 *  注意: 此方法中,e,c并没有用.
 * @author hedong
 *
 */
@AnEstimator
public class OrderEstimator extends BaseEstimatorWithParameters {
	public OrderEstimator(EstimatorConfigure config){
		super(config,"Order");
	}

	/* (non-Javadoc)
	 * @see howmuch.estimator.Estimator#estimate(howmuch.parse.StatRes)
	 */
	public void estimate(StatRes res) {
		successful=false;
		int k=res.getNumOfObservedUnits();
		if(k<1)
			return;
		Hashtable<String, ArrayList<String>> units=res.getObservedUnits();
		String max="";
		for(String u:units.keySet()){
			for(String key:units.get(u)){
				if(max.length()<key.length()){
					max=key;
					continue;
				}
				if(max.compareTo(key)<0){
					max=key;
				}
			}
		}
		int m=Integer.parseInt(max);
		int t=(int)Math.round(1.0*m*(1.0+1.0/k)-1.0);
		res.setU(t-k);
		res.setW(t);
		
		/**
		 * The coverage and the probability of new unit class are the consequent results of W/U.
		 */
		res.setC(1.0*res.getNumOfObservedUnits()/res.getW());
		res.setN(1.0-res.getC());

		successful=true;


	}
}
