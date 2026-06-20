package com.bstek.ureport.definition.dataset;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import com.bstek.ureport.definition.datasource.DataType;

public class DatasetPojosTest {

    @Test
    public void testFieldConstructor() {
        Field f = new Field("name");
        assertEquals("name", f.getName());
    }

    @Test
    public void testFieldSetName() {
        Field f = new Field("");
        f.setName("age");
        assertEquals("age", f.getName());
    }

    @Test
    public void testParameterDefaults() {
        Parameter p = new Parameter();
        assertNull(p.getName());
        assertNull(p.getType());
        assertNull(p.getDefaultValue());
    }

    @Test
    public void testParameterSetters() {
        Parameter p = new Parameter();
        p.setName("p1");
        p.setType(DataType.String);
        p.setDefaultValue("default");
        assertEquals("p1", p.getName());
        assertEquals(DataType.String, p.getType());
        assertEquals("default", p.getDefaultValue());
    }

    @Test
    public void testBeanDatasetDefinitionDefaults() {
        BeanDatasetDefinition def = new BeanDatasetDefinition();
        assertNull(def.getName());
        assertNull(def.getMethod());
        assertNull(def.getClazz());
        assertNull(def.getFields());
    }

    @Test
    public void testBeanDatasetDefinitionSetters() {
        BeanDatasetDefinition def = new BeanDatasetDefinition();
        def.setName("ds1");
        def.setMethod("getData");
        def.setClazz("com.example.MyBean");
        assertEquals("ds1", def.getName());
        assertEquals("getData", def.getMethod());
        assertEquals("com.example.MyBean", def.getClazz());
    }

    @Test
    public void testBeanDatasetDefinitionFields() {
        BeanDatasetDefinition def = new BeanDatasetDefinition();
        ArrayList<Field> fields = new ArrayList<>();
        fields.add(new Field("id"));
        def.setFields(fields);
        assertSame(fields, def.getFields());
    }

    @Test
    public void testSqlDatasetDefinitionDefaults() {
        SqlDatasetDefinition def = new SqlDatasetDefinition();
        assertNull(def.getName());
        assertNull(def.getSql());
        assertNull(def.getFields());
        assertNull(def.getParameters());
    }

    @Test
    public void testSqlDatasetDefinitionSetters() {
        SqlDatasetDefinition def = new SqlDatasetDefinition();
        def.setName("ds1");
        def.setSql("select * from t");
        assertEquals("ds1", def.getName());
        assertEquals("select * from t", def.getSql());
    }

    @Test
    public void testSqlDatasetDefinitionFields() {
        SqlDatasetDefinition def = new SqlDatasetDefinition();
        ArrayList<Field> fields = new ArrayList<>();
        fields.add(new Field("col1"));
        def.setFields(fields);
        assertSame(fields, def.getFields());
    }

    @Test
    public void testSqlDatasetDefinitionParameters() {
        SqlDatasetDefinition def = new SqlDatasetDefinition();
        ArrayList<Parameter> params = new ArrayList<>();
        Parameter p = new Parameter();
        p.setName("id");
        params.add(p);
        def.setParameters(params);
        assertSame(params, def.getParameters());
    }
}
