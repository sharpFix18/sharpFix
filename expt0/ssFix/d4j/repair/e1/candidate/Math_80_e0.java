package page2;

public class Problem71 {

	public static void main(String[] args) {
		
		long d = 1000000;
		long lowest = 1;
		long delta = 0;
		long n;
		long maxn = 0;
		long maxd = 1;
		
		while (d >= lowest) {
			if (d % 7 == 0) {
				d--;
				continue;
			}
			
			// n/d < 3/7
			n = (3*d - 1) / 7;
			
			// maxn/maxd < n/d
			if (maxn*d < n*maxd) {
				maxn = n;
				maxd = d;
				delta = 3*d - 7*n;
				lowest = d/delta + 1;
			}
			
			d--;
		}
		
		System.out.println(maxn + "/" + maxd);
	}
}
