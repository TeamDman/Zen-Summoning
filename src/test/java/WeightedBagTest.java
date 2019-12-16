import ca.teamdman.zensummoning.util.WeightedRandomBag;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class WeightedBagTest {
	public static WeightedRandomBag<String> bag;
	public static HashMap<String, Double> entries;

	@BeforeAll
	public static void init() {
		entries = new HashMap<>();
		entries.put("Diamond", 1 / 8192d);
		entries.put("Gold", 1 / 2048d);
		entries.put("Iron", 1 / 256d);
		entries.put("Redstone", 1 / 64d);
		entries.put("Cobble", 1d);
		bag = new WeightedRandomBag<>();
		entries.forEach(bag::addEntry);
		System.out.println("Initialized bag.");
	}

	@Test
	public void randomPoll() {
		HashMap<String, Integer> results = new HashMap<>();
		final int                total   = (int) 1e8;
		for (int i = 0; i < total; i++)
			results.compute(bag.getRandom(), (__, count) -> count == null ? 1 : count + 1);
		System.out.printf("%10s | %10s | %10s | %20s\n___________|____________|____________|_____________________\n", "Item", "Total", "Chance", "Actual");
		results.entrySet().stream()
				.sorted(Comparator.comparingInt(Map.Entry::getValue))
				.forEach((e) -> System.out.printf(
						"%10s | %10s | %10s | %20s\n___________|____________|____________|_____________________\n",
						e.getKey(),
						e.getValue(),
						"1/" + (1d / entries.get(e.getKey())),
						"1/" + Math.floor((1d / (e.getValue() * 1d / total)) * 10) / 10d));
		System.out.printf("\nTotal iterations: %d", total);
	}

	@RepeatedTest(10)
	public void iterate() {
		HashMap<String, HashMap<Integer, Integer>> results = new HashMap<>();
		final int                                  total   = (int) 1e3;
		for (int z = 0; z < total; z++) {
			int i = 0;
			for (String s : bag) {
				if (!results.containsKey(s))
					results.put(s, new HashMap<>());
				if (!results.get(s).containsKey(i))
					results.get(s).put(i, 1);
				else
					results.get(s).compute(i, (__, count) -> count == null ? 1 : count + 1);
				i++;
			}
		}
		System.out.printf("%10s\t", "Name");
		for (int i = 0; i<bag.size(); i++)
			System.out.printf("%10s\t", "Position " + i);
		System.out.println();
		results.forEach((item, data) -> {
			System.out.printf("%10s\t", item);
			for (int i=0; i<bag.size(); i++) {
				int count = 0;
				if (data.containsKey(i))
					count = data.get(i);
				System.out.printf("%10.0f\t", count/1f);
			}
			System.out.println();
		});

		System.out.printf("\nTotal iterations: %d", total);
	}
}
