package com.bstek.ureport.definition.value;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ValueTest {

    @Test
    public void testSimpleValue() {
        SimpleValue sv = new SimpleValue("hello");
        assertEquals("hello", sv.getValue());
        assertEquals(ValueType.simple, sv.getType());
    }

    @Test
    public void testExpressionValue() {
        ExpressionValue ev = new ExpressionValue("1+2");
        assertEquals("1+2", ev.getValue());
        assertEquals(ValueType.expression, ev.getType());
        assertNotNull(ev.getExpression());
    }

    @Test
    public void testImageValue() {
        ImageValue iv = new ImageValue();
        iv.setPath("/images/test.png");
        iv.setSource(Source.text);
        iv.setWidth(100);
        iv.setHeight(200);
        assertEquals(ValueType.image, iv.getType());
        assertEquals("/images/test.png", iv.getValue());
        assertEquals(Source.text, iv.getSource());
        assertEquals(100, iv.getWidth());
        assertEquals(200, iv.getHeight());

        iv.setSource(Source.expression);
        iv.setExpr("${img}");
        assertEquals("${img}", iv.getValue());
    }

    @Test
    public void testSlashValue() {
        SlashValue sv = new SlashValue();
        assertEquals(ValueType.slash, sv.getType());
        assertNull(sv.getValue());

        sv.setSvg("<svg>...</svg>");
        sv.setBase64Data("base64data");
        assertEquals("<svg>...</svg>", sv.getSvg());
        assertEquals("base64data", sv.getBase64Data());

        List<Slash> slashes = new ArrayList<>();
        slashes.add(new Slash());
        sv.setSlashes(slashes);
        assertEquals(1, sv.getSlashes().size());
    }

    @Test
    public void testSlash() {
        Slash slash = new Slash();
        slash.setX(10);
        slash.setY(20);
        slash.setDegree(45);
        slash.setText("label");
        assertEquals(10, slash.getX());
        assertEquals(20, slash.getY());
        assertEquals(45, slash.getDegree());
        assertEquals("label", slash.getText());
    }

    @Test
    public void testChartValue() {
        ChartValue cv = new ChartValue();
        assertEquals(ValueType.chart, cv.getType());
        assertNull(cv.getValue());
        assertNull(cv.getChart());
    }

    @Test
    public void testZxingValue() {
        ZxingValue zv = new ZxingValue();
        zv.setWidth(200);
        zv.setHeight(200);
        zv.setSource(Source.text);
        zv.setText("123456");
        zv.setFormat("CODE128");
        zv.setCategory(ZxingCategory.barcode);
        zv.setCodeDisplay(true);

        assertEquals(ValueType.zxing, zv.getType());
        assertEquals(200, zv.getWidth());
        assertEquals(200, zv.getHeight());
        assertEquals("123456", zv.getValue());
        assertEquals(ZxingCategory.barcode, zv.getCategory());
        assertTrue(zv.isCodeDisplay());

        zv.setSource(Source.expression);
        zv.setExpr("${code}");
        assertEquals("${code}", zv.getValue());
    }

    @Test
    public void testZxingCategory() {
        assertEquals("barcode", ZxingCategory.barcode.name());
        assertEquals("qrcode", ZxingCategory.qrcode.name());
    }

    @Test
    public void testGroupItem() {
        GroupItem gi = new GroupItem();
        gi.setName("group1");
        assertEquals("group1", gi.getName());
        assertNull(gi.getCondition());
        assertNull(gi.getConditions());
    }

}
