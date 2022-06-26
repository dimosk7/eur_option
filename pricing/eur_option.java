import org.apache.commons.math3.distribution.NormalDistribution; 
import java.util.*;
import options.Sql;


public class Option {
	
	double spot;
	double exer;
	double rate;
	double vol;
	double matur;
	
	public Option(double s, double e, double r, double v, double m) {
    
		spot = s;
		exer = e;
		rate = r;
		vol = v;
		matur = m;
	}
	
	public void getInfo() {
		
		System.out.println("Spot price : " + spot);
		System.out.println("Exercise price : " + exer);
		System.out.println("Risk-free rate : " + rate);
		System.out.println("Volatility : " + vol);
		System.out.println("Months to maturity : " + matur);
	}
	
	public double getBlackPrice() {
		
		NormalDistribution dist = new NormalDistribution();
		double d1 = (Math.log(spot/exer) + (rate + Math.pow(vol, 2)/2)*matur/12)/vol/Math.sqrt(matur/12);
		double d2 = d1 - vol*Math.sqrt(matur/12);
		double value =  spot*dist.cumulativeProbability(d1)  - exer*Math.exp(-rate*matur/12)*dist.cumulativeProbability(d2) ;
		return value;
	}
	
	public double getMontePrice() {
			
		Random rand = new Random();
		int num_sim = 25000;
		double dt = 1/252.0;
		int days = (int) Math.round(matur/12*252);
		double sum = 0.0;
		for (int i = 0; i < num_sim; i++) {
			double pr = Math.log(spot);
			for (int j = 0; j < days; j++) {
				pr += (rate - Math.pow(vol, 2)/2)*dt + vol*rand.nextGaussian()*Math.sqrt(dt);
			}
			double final_price = Math.max(Math.exp(pr) - exer, 0);
			sum += final_price;
		}
		double value =  sum/num_sim*Math.exp(-rate*matur);	
		return value;
	}
		
	public static void main(String[] args) {
		
		System.out.println("Enter values for spot price, exercise price, risk-free rate and months to maturity :");
		Scanner scan = new Scanner(System.in);
		double[] val = new double[4];
		for (int i = 0; i < val.length; i++) {
			val[i] = scan.nextDouble();
		}
		scan.nextLine();
		System.out.println("To compute historical volatility, enter company's ticker : ");
		String ticker = scan.nextLine();
		Sql sql = new Sql();
		double vol = sql.getVol(ticker);
		if (vol == 0) {
			System.out.println("Enter a value for volatility : ");
			vol = scan.nextDouble();
		}
		scan.close();
		Option option = new Option(val[0], val[1], val[2], vol, val[3]);
		option.getInfo();
		System.out.println("Black–Scholes price is : " + option.getBlackPrice());
		System.out.println("Monte Carlo price is : " + option.getMontePrice());	
	}

}
