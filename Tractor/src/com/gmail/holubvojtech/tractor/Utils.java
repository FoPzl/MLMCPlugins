package com.gmail.holubvojtech.tractor;

import java.util.Map;

public class Utils {
	public static <K, V> V getOrDefault(Map<K, V> map, K key, V def) {
		V value = map.get(key);
		if (value == null) {
			return def;
		}
		return value;
	}
}
