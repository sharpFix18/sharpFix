package tom.java.week03;

import java.util.ArrayList;
import java.util.List;

public class RestoreIPs {

	public static void main(String[] args) {
		RestoreIPs restoreIPs = new RestoreIPs();
		// List<String> results = restoreIPs.restore("25525511135", 2);
		List<String> results = restoreIPs.restoreIpAddresses("25525511135");
		System.out.println(results);
	}

	public List<String> restoreIpAddresses(String s) {
		if (s.length() > 12)
			return new ArrayList<>();
		return restore(s, 4);
	}

	
	public List<String> restore(String s, int count) { // 3 or 2 or 1
		List<String> results = new ArrayList<>();

		switch (count) {
		case 1:
			if (s.length() == 1){
				results.add(s);
				return results;
			}
			try {
				Integer value = Integer.parseInt(s);
				if (value < 256 && s.charAt(0) != '0'){
					results.add(s);
				}
			} catch (Exception e) {
			}
			return results;
		case 2:
			for (int end = 1; end <= 3; end++) {
				if (end >= s.length())
					continue;
				List<String> rs1List = restore(s.substring(0, end), 1);
				List<String> rs2List = restore(s.substring(end), 1);
				for (int i = 0; i < rs1List.size(); i++) {
					for (int j = 0; j < rs2List.size(); j++) {
						results.add(rs1List.get(i) + "." + rs2List.get(j));
					}
				}
			}
			return results;
		case 4:
			for (int end = 2; end <= 6; end++) {
				if (end >= s.length())
					continue;
				List<String> firstHalf = restore(s.substring(0, end), 2);
				List<String> secondHalf = restore(s.substring(end), 2);
				for (int i = 0; i < firstHalf.size(); i++) {
					for (int j = 0; j < secondHalf.size(); j++) {
						results.add(firstHalf.get(i) + "." + secondHalf.get(j));
					}
				}
			}
		default:
			break;
		}
		return results;
	}

}
