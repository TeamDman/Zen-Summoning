import ca.teamdman.zensummoning.util.WeightedRandomBag;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class WeightedBagTest {
	@Test
	public void ensureWeightedProperly() {
		HashMap<String, Double> entries = new HashMap<>();
		entries.put("Diamond", 1 / 8192d);
		entries.put("Gold", 1 / 2048d);
		entries.put("Iron", 1 / 256d);
		entries.put("Redstone", 1 / 64d);
		entries.put("Cobble", 1d);
		WeightedRandomBag<String> bag = new WeightedRandomBag<>();
		entries.forEach(bag::addEntry);
		HashMap<String, Integer> results = new HashMap<>();
		final int                total   = (int) 1e8;
		for (int i = 0; i < total; i++)
			results.compute(bag.getRandom(), (item, count) -> count == null ? 1 : count + 1);
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
}
