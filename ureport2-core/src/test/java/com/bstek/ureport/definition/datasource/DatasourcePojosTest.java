package com.bstek.ureport.definition.datasource;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import com.bstek.ureport.definition.dataset.DatasetDefinition;
import com.bstek.ureport.definition.dataset.Field;
import com.bstek.ureport.definition.dataset.Parameter;
import com.bstek.ureport.definition.dataset.SqlDatasetDefinition;

public class DatasourcePojosTest {

    @Test
    public void testMethodInfo() {
        ArrayList<Class<?>> paramTypes = new ArrayList<>();
        paramTypes.add(String.class);
        MethodInfo mi = new MethodInfo("execute", String.class, paramTypes);
        assertEquals("execute", mi.getName());
        assertEquals(String.class, mi.getReturnType());
        assertSame(paramTypes, mi.getParameterTypes());
    }

    @Test
    public void testBuildinDatasourceDefinitionDefaults() {
        BuildinDatasourceDefinition def = new BuildinDatasourceDefinition();
        assertNull(def.getName());
        assertNull(def.getDatasets());
        assertEquals(DatasourceType.buildin, def.getType());
    }

    @Test
    public void testBuildinDatasourceDefinitionSetters() {
        BuildinDatasourceDefinition def = new BuildinDatasourceDefinition();
        def.setName("buildin1");
        assertEquals("buildin1", def.getName());
    }

    @Test
    public void testJdbcDatasourceDefinitionDefaults() {
        JdbcDatasourceDefinition def = new JdbcDatasourceDefinition();
        assertNull(def.getName());
        assertNull(def.getDriver());
        assertNull(def.getUrl());
        assertNull(def.getUsername());
        assertNull(def.getPassword());
        assertNull(def.getDatasets());
        assertEquals(DatasourceType.jdbc, def.getType());
    }

    @Test
    public void testJdbcDatasourceDefinitionSetters() {
        JdbcDatasourceDefinition def = new JdbcDatasourceDefinition();
        def.setName("jdbc1");
        def.setDriver("com.mysql.jdbc.Driver");
        def.setUrl("jdbc:mysql://localhost/test");
        def.setUsername("root");
        def.setPassword("secret");
        assertEquals("jdbc1", def.getName());
        assertEquals("com.mysql.jdbc.Driver", def.getDriver());
        assertEquals("jdbc:mysql://localhost/test", def.getUrl());
        assertEquals("root", def.getUsername());
        assertEquals("secret", def.getPassword());
    }

    @Test
    public void testSpringBeanDatasourceDefinitionDefaults() {
        SpringBeanDatasourceDefinition def = new SpringBeanDatasourceDefinition();
        assertNull(def.getName());
        assertNull(def.getBeanId());
        assertNull(def.getDatasets());
        assertEquals(DatasourceType.spring, def.getType());
    }

    @Test
    public void testSpringBeanDatasourceDefinitionSetters() {
        SpringBeanDatasourceDefinition def = new SpringBeanDatasourceDefinition();
        def.setName("spring1");
        def.setBeanId("myBean");
        assertEquals("spring1", def.getName());
        assertEquals("myBean", def.getBeanId());
    }

    @Test
    public void testDatasourceTypeEnum() {
        assertEquals("buildin", DatasourceType.buildin.name());
        assertEquals("jdbc", DatasourceType.jdbc.name());
        assertEquals("spring", DatasourceType.spring.name());
    }

    @Test
    public void testDataTypeParse() {
        assertEquals("hello", DataType.String.parse("hello"));
        assertEquals(42, DataType.Integer.parse("42"));
        assertNull(DataType.String.parse(null));
    }
}
