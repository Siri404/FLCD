import model.Pair;
import model.Parser;

import java.util.Map;

public class Main {

    public static void main(String[] args) {
		Parser parser = new Parser();
		int i = 0;
		for (Pair<String, Map<String, Integer>> row : parser.getParseTable()) {
			System.out.print(i);
			i++;
			System.out.print(" ");
			System.out.print(row.getKey());
			System.out.print(" ");
			row.getValue().forEach((key,value) -> {
				System.out.print(key + "=" + value + " ");
			});
			System.out.println();
		}
    }
}
