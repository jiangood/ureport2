package com.bstek.ureport.cache;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class CacheTest {

    @Test
    public void testResourceCache() {
        ResourceCache.putObject("key1", "value1");
        assertEquals("value1", ResourceCache.getObject("key1"));

        ResourceCache.putObject("key1", "value2");
        assertEquals("value2", ResourceCache.getObject("key1"));

        assertNull(ResourceCache.getObject("nonexistent"));
    }

    @Test
    public void testDefaultMemoryReportDefinitionCache() {
        com.bstek.ureport.definition.ReportDefinition def = new com.bstek.ureport.definition.ReportDefinition();
        DefaultMemoryReportDefinitionCache cache = new DefaultMemoryReportDefinitionCache();

        assertNull(cache.getReportDefinition("test"));

        cache.cacheReportDefinition("test", def);
        assertSame(def, cache.getReportDefinition("test"));

        com.bstek.ureport.definition.ReportDefinition def2 = new com.bstek.ureport.definition.ReportDefinition();
        cache.cacheReportDefinition("test", def2);
        assertSame(def2, cache.getReportDefinition("test"));
    }
}
