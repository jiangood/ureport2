package com.bstek.ureport.registry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class RegistryTest {

	@Test
	public void emptyRegistry_returnsEmptyList() {
		Registry<String> registry = new Registry<String>();
		assertTrue(registry.all().isEmpty());
		assertNull(registry.getByName("anything"));
	}

	@Test
	public void register_addsItemToAll() {
		Registry<String> registry = new Registry<String>();
		registry.register("hello");
		List<String> items = registry.all();
		assertEquals(1, items.size());
		assertEquals("hello", items.get(0));
	}

	@Test
	public void registerWithName_addsItemToAllAndByName() {
		Registry<String> registry = new Registry<String>();
		registry.register("key1", "value1");
		assertEquals("value1", registry.getByName("key1"));
		assertEquals(1, registry.all().size());
		assertEquals("value1", registry.all().get(0));
	}

	@Test
	public void registerMultiple_itemsRetainOrder() {
		Registry<String> registry = new Registry<String>();
		registry.register("first");
		registry.register("second");
		registry.register("third");
		List<String> items = registry.all();
		assertEquals(3, items.size());
		assertEquals("first", items.get(0));
		assertEquals("second", items.get(1));
		assertEquals("third", items.get(2));
	}

	@Test
	public void registerDuplicateName_lastOneWins() {
		Registry<String> registry = new Registry<String>();
		registry.register("key", "original");
		registry.register("key", "replaced");
		assertEquals("replaced", registry.getByName("key"));
		assertEquals(2, registry.all().size());
	}

	@Test
	public void all_returnsUnmodifiableList() {
		Registry<String> registry = new Registry<String>();
		registry.register("item");
		List<String> items = registry.all();
		assertThrows(UnsupportedOperationException.class, () -> items.add("another"));
	}

	@Test
	public void getByName_returnsNullForUnknownName() {
		Registry<String> registry = new Registry<String>();
		registry.register("known", "value");
		assertNull(registry.getByName("unknown"));
	}

	@Test
	public void mixedNamedAndUnnamed_entriesAllVisible() {
		Registry<String> registry = new Registry<String>();
		registry.register("unnamed1");
		registry.register("key2", "named2");
		registry.register("unnamed3");
		assertEquals(3, registry.all().size());
		assertEquals("named2", registry.getByName("key2"));
		assertNull(registry.getByName("unnamed1"));
	}

	@Test
	public void registryWorksWithDifferentTypes() {
		Registry<Integer> registry = new Registry<Integer>();
		registry.register(42);
		registry.register("answer", 42);
		assertEquals(42, (int) registry.all().get(0));
		assertEquals(42, (int) registry.getByName("answer"));
	}
}
