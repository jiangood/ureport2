package com.bstek.ureport.definition.mapping;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class MappingItemTest {

    @Test
    public void testMappingItem() {
        MappingItem mi = new MappingItem();
        mi.setValue("1");
        mi.setLabel("One");
        assertEquals("1", mi.getValue());
        assertEquals("One", mi.getLabel());
    }

    @Test
    public void testMappingType() {
        assertEquals("simple", MappingType.simple.name());
        assertEquals("dataset", MappingType.dataset.name());
    }
}
