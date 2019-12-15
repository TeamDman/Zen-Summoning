package ca.teamdman.zensummoning.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class WeightedRandomBag<T> {

	private double      accumulatedWeight;
	private List<Entry> entries = new ArrayList<>();
	private Random      rand    = new Random();

	public void addEntry(T object, double weight) {
		accumulatedWeight += weight;
		Entry e = new Entry();
		e.object = object;
		e.accumulatedWeight = accumulatedWeight;
		entries.add(e);
	}

	public T getRandom() {
		return getRandom(null);
	}

	public T getRandom(Predicate<T> condition) {
		double r = rand.nextDouble() * accumulatedWeight;

		for (Entry entry : entries) {
			if (condition == null || condition.test(entry.object)) {
				if (entry.accumulatedWeight >= r) {
					return entry.object;
				}
			}
		}
		return null; //should only happen when there are no entries
	}

	private class Entry {
		double accumulatedWeight;
		T      object;
	}
}