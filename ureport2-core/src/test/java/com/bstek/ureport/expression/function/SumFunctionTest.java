package com.bstek.ureport.expression.function;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.bstek.ureport.build.BindData;
import com.bstek.ureport.expression.model.data.BindDataListExpressionData;
import com.bstek.ureport.expression.model.data.ExpressionData;
import com.bstek.ureport.expression.model.data.ObjectExpressionData;
import com.bstek.ureport.expression.model.data.ObjectListExpressionData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SumFunctionTest {

    @Test
    public void testNullAndEmpty() {
        SumFunction sum = new SumFunction();
        assertNull(sum.execute(null, null, null));
        assertEquals("sum", sum.name());

        List<ExpressionData<?>> emptyList = new ArrayList<>();
        assertNull(sum.execute(emptyList, null, null));
    }

    @Test
    public void testObjectExpressionData() {
        SumFunction sum = new SumFunction();
        List<ExpressionData<?>> dataList = new ArrayList<>();
        dataList.add(new ObjectExpressionData(10));
        assertEquals(new BigDecimal(10), sum.execute(dataList, null, null));
    }

    @Test
    public void testObjectExpressionDataWithMultiple() {
        SumFunction sum = new SumFunction();
        List<ExpressionData<?>> dataList = new ArrayList<>();
        dataList.add(new ObjectExpressionData(10));
        dataList.add(new ObjectExpressionData(20));
        assertEquals(new BigDecimal(30), sum.execute(dataList, null, null));
    }

    @Test
    public void testObjectListExpressionData() {
        SumFunction sum = new SumFunction();
        List<ExpressionData<?>> dataList = new ArrayList<>();
        dataList.add(new ObjectListExpressionData(Arrays.asList(1, 2, 3, 4, 5)));
        assertEquals(new BigDecimal(15), sum.execute(dataList, null, null));
    }

    @Test
    public void testBindDataListExpressionData() {
        SumFunction sum = new SumFunction();
        List<BindData> bindDataList = new ArrayList<>();
        bindDataList.add(new BindData(1));
        bindDataList.add(new BindData(2));
        bindDataList.add(new BindData(3));
        List<ExpressionData<?>> dataList = new ArrayList<>();
        dataList.add(new BindDataListExpressionData(bindDataList));
        assertEquals(new BigDecimal(6), sum.execute(dataList, null, null));
    }

    @Test
    public void testWithNullValues() {
        SumFunction sum = new SumFunction();
        List<ExpressionData<?>> dataList = new ArrayList<>();
        dataList.add(new ObjectListExpressionData(Arrays.asList(1, null, 2, null, 3)));
        assertEquals(new BigDecimal(6), sum.execute(dataList, null, null));
    }

    @Test
    public void testWithBlankStringValues() {
        SumFunction sum = new SumFunction();
        List<ExpressionData<?>> dataList = new ArrayList<>();
        dataList.add(new ObjectListExpressionData(Arrays.asList(1, "", 2, " ", 3)));
        assertEquals(new BigDecimal(6), sum.execute(dataList, null, null));
    }

    @Test
    public void testSingleNullDataReturnsEmptyString() {
        SumFunction sum = new SumFunction();
        List<ExpressionData<?>> dataList = new ArrayList<>();
        dataList.add(new ObjectExpressionData(null));
        assertEquals("", sum.execute(dataList, null, null));
    }

    @Test
    public void testSingleEmptyStringDataReturnsEmptyString() {
        SumFunction sum = new SumFunction();
        List<ExpressionData<?>> dataList = new ArrayList<>();
        dataList.add(new ObjectExpressionData(""));
        assertEquals("", sum.execute(dataList, null, null));
    }

    @Test
    public void testMixedTypes() {
        SumFunction sum = new SumFunction();
        List<ExpressionData<?>> dataList = new ArrayList<>();
        dataList.add(new ObjectExpressionData(10));
        dataList.add(new ObjectListExpressionData(Arrays.asList(1, 2, 3)));
        assertEquals(new BigDecimal(16), sum.execute(dataList, null, null));
    }
}
