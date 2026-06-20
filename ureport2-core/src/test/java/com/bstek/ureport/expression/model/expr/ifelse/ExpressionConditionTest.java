package com.bstek.ureport.expression.model.expr.ifelse;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.bstek.ureport.build.BindData;
import com.bstek.ureport.expression.model.data.BindDataListExpressionData;
import com.bstek.ureport.expression.model.data.ExpressionData;
import com.bstek.ureport.expression.model.data.NoneExpressionData;
import com.bstek.ureport.expression.model.data.ObjectExpressionData;
import com.bstek.ureport.expression.model.data.ObjectListExpressionData;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExpressionConditionTest {

    private Object invokeGetData(ExpressionData<?> data) throws Exception {
        ExpressionCondition c = new ExpressionCondition(null, null, null);
        Method m = ExpressionCondition.class.getDeclaredMethod("getData", ExpressionData.class);
        m.setAccessible(true);
        return m.invoke(c, data);
    }

    @Test
    public void testGetDataObjectExpression() throws Exception {
        assertEquals("hello", invokeGetData(new ObjectExpressionData("hello")));
        assertEquals(42, invokeGetData(new ObjectExpressionData(42)));
        assertNull(invokeGetData(new ObjectExpressionData(null)));
    }

    @Test
    public void testGetDataObjectListExpression() throws Exception {
        List<Integer> list = Arrays.asList(1, 2, 3);
        assertEquals("1,2,3", invokeGetData(new ObjectListExpressionData(list)));
    }

    @Test
    public void testGetDataObjectListExpressionSingle() throws Exception {
        List<Integer> list = Arrays.asList(42);
        assertEquals("42", invokeGetData(new ObjectListExpressionData(list)));
    }

    @Test
    public void testGetDataNoneExpression() throws Exception {
        assertNull(invokeGetData(new NoneExpressionData()));
    }

    @Test
    public void testGetDataBindDataListSingle() throws Exception {
        List<BindData> bindDataList = new ArrayList<>();
        bindDataList.add(new BindData("singleValue"));
        assertEquals("singleValue", invokeGetData(new BindDataListExpressionData(bindDataList)));
    }

    @Test
    public void testGetDataBindDataListMultiple() throws Exception {
        List<BindData> bindDataList = new ArrayList<>();
        bindDataList.add(new BindData("v1"));
        bindDataList.add(new BindData("v2"));
        assertEquals("v1,v2", invokeGetData(new BindDataListExpressionData(bindDataList)));
    }

    @Test
    public void testGetDataBindDataListWithNullValue() throws Exception {
        List<BindData> bindDataList = new ArrayList<>();
        bindDataList.add(new BindData("v1"));
        bindDataList.add(new BindData(null));
        bindDataList.add(new BindData("v3"));
        assertEquals("v1,null,v3", invokeGetData(new BindDataListExpressionData(bindDataList)));
    }

    @Test
    public void testGetDataObjectListExpressionWithNull() throws Exception {
        List<Object> list = Arrays.asList("a", null, "b");
        assertEquals("a,null,b", invokeGetData(new ObjectListExpressionData(list)));
    }
}
