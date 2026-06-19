package com.bstek.ureport.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Registry<T> {
	private final List<T> items = new ArrayList<T>();
	private final Map<String, T> byName = new LinkedHashMap<String, T>();

	public void register(T item) {
		items.add(item);
	}

	public void register(String name, T item) {
		byName.put(name, item);
		items.add(item);
	}

	public List<T> all() {
		return Collections.unmodifiableList(items);
	}

	public T getByName(String name) {
		return byName.get(name);
	}
}
