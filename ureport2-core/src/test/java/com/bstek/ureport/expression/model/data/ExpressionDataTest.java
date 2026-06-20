package com.bstek.ureport.expression.model.data;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import com.bstek.ureport.build.BindData;

public class ExpressionDataTest {

    @Test
    public void testObjectExpressionData() {
        ObjectExpressionData data = new ObjectExpressionData("hello");
        assertEquals("hello", data.getData());
    }

    @Test
    public void testObjectListExpressionData() {
        List<String> list = Arrays.asList("a", "b", "c");
        ObjectListExpressionData data = new ObjectListExpressionData(list);
        assertEquals(list, data.getData());
        assertEquals(3, data.getData().size());
    }

    @Test
    public void testNoneExpressionData() {
        NoneExpressionData data = new NoneExpressionData();
        assertNull(data.getData());
    }

    @Test
    public void testBindDataListExpressionData() {
        List<BindData> bindDataList = Arrays.asList(new BindData(1), new BindData(2));
        BindDataListExpressionData data = new BindDataListExpressionData(bindDataList);
        assertEquals(bindDataList, data.getData());
        assertEquals(2, data.getData().size());
    }

    @Test
    public void testExpressionDataInterface() {
        ExpressionData<Object> exprData = new ObjectExpressionData("test");
        assertEquals("test", exprData.getData());
    }
}
