package ca.teamdman.zensummoning.util;

import java.util.*;
import java.util.stream.Collectors;

public class WeightedRandomBag<T> implements Iterable<T> {

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
		int index = getRandomIndex();
		if (index == -1)
			return null;
		return entries.get(index).object;
	}

	private int getRandomIndex() {
		double r = rand.nextDouble() * accumulatedWeight;
		for (int i = 0; i < entries.size(); i++) {
			if (entries.get(i).accumulatedWeight >= r) {
				return i;
			}
		}
		return -1;
	}

	public List<T> getList() {
		return entries.stream().map(e -> e.object).collect(Collectors.toList());
	}

	@Override
	public Iterator<T> iterator() {
		ArrayList<T>     items = new ArrayList<>();
		HashSet<Integer> used  = new HashSet<>();
		while (used.size() < entries.size()) {
			int index = getRandomIndex();
			if (!used.contains(index)) {
				used.add(index);
				items.add(entries.get(index).object);
			}
		}
		return items.iterator();
	}

	private class Entry {
		double accumulatedWeight;
		T      object;
	}
}