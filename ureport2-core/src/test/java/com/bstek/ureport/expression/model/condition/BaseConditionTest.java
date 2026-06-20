package com.bstek.ureport.expression.model.condition;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.bstek.ureport.build.BindData;
import com.bstek.ureport.expression.model.Condition;
import com.bstek.ureport.expression.model.Op;
import com.bstek.ureport.expression.model.data.BindDataListExpressionData;
import com.bstek.ureport.expression.model.data.ExpressionData;
import com.bstek.ureport.expression.model.data.NoneExpressionData;
import com.bstek.ureport.expression.model.data.ObjectExpressionData;
import com.bstek.ureport.expression.model.data.ObjectListExpressionData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseConditionTest {

    /* ===== extractExpressionData via anonymous subclass ===== */
    private static class TestCondition extends BaseCondition {
        @Override
        Object computeLeft(com.bstek.ureport.model.Cell cell, com.bstek.ureport.model.Cell currentCell, Object obj, com.bstek.ureport.build.Context context) {
            return null;
        }
        @Override
        Object computeRight(com.bstek.ureport.model.Cell cell, com.bstek.ureport.model.Cell currentCell, Object obj, com.bstek.ureport.build.Context context) {
            return null;
        }
        @Override
        public ConditionType getType() {
            return ConditionType.property;
        }
        public Object extract(ExpressionData<?> data) {
            return extractExpressionData(data);
        }
    }

    @Test
    public void testExtractObjectExpressionData() {
        TestCondition c = new TestCondition();
        assertEquals("hello", c.extract(new ObjectExpressionData("hello")));
        assertEquals(42, c.extract(new ObjectExpressionData(42)));
        assertNull(c.extract(new ObjectExpressionData(null)));
    }

    @Test
    public void testExtractObjectListExpressionData() {
        TestCondition c = new TestCondition();
        List<String> list = Arrays.asList("a", "b", "c");
        Object result = c.extract(new ObjectListExpressionData(list));
        assertTrue(result instanceof List);
        assertEquals(list, result);
    }

    @Test
    public void testExtractNoneExpressionData() {
        TestCondition c = new TestCondition();
        assertNull(c.extract(new NoneExpressionData()));
    }

    @Test
    public void testExtractBindDataListSingle() {
        TestCondition c = new TestCondition();
        List<BindData> bindDataList = new ArrayList<>();
        bindDataList.add(new BindData("value1"));
        Object result = c.extract(new BindDataListExpressionData(bindDataList));
        assertEquals("value1", result);
    }

    @Test
    public void testExtractBindDataListMultiple() {
        TestCondition c = new TestCondition();
        List<BindData> bindDataList = new ArrayList<>();
        bindDataList.add(new BindData("v1"));
        bindDataList.add(new BindData("v2"));
        Object result = c.extract(new BindDataListExpressionData(bindDataList));
        assertTrue(result instanceof List);
        List<?> list = (List<?>) result;
        assertEquals(2, list.size());
        assertEquals("v1", list.get(0));
        assertEquals("v2", list.get(1));
    }

    @Test
    public void testExtractBindDataListEmpty() {
        TestCondition c = new TestCondition();
        List<BindData> bindDataList = new ArrayList<>();
        assertNull(c.extract(new BindDataListExpressionData(bindDataList)));
    }

    /* ===== filter chaining with conditional subclass ===== */
    private static class FilterTestCondition extends BaseCondition {
        private final Object leftResult;
        private final Object rightResult;

        FilterTestCondition(Object left, Object right, Op op) {
            this.leftResult = left;
            this.rightResult = right;
            this.op = op;
        }

        @Override
        Object computeLeft(com.bstek.ureport.model.Cell cell, com.bstek.ureport.model.Cell currentCell, Object obj, com.bstek.ureport.build.Context context) {
            return leftResult;
        }
        @Override
        Object computeRight(com.bstek.ureport.model.Cell cell, com.bstek.ureport.model.Cell currentCell, Object obj, com.bstek.ureport.build.Context context) {
            return rightResult;
        }
        @Override
        public ConditionType getType() {
            return ConditionType.property;
        }
    }

    @Test
    public void testFilterEqualsTrue() {
        FilterTestCondition c = new FilterTestCondition(5, 5, Op.Equals);
        assertTrue(c.filter(null, null, null, null));
    }

    @Test
    public void testFilterEqualsFalse() {
        FilterTestCondition c = new FilterTestCondition(5, 10, Op.Equals);
        assertFalse(c.filter(null, null, null, null));
    }

    @Test
    public void testFilterGreatThen() {
        FilterTestCondition c = new FilterTestCondition(10, 5, Op.GreatThen);
        assertTrue(c.filter(null, null, null, null));
        c = new FilterTestCondition(5, 10, Op.GreatThen);
        assertFalse(c.filter(null, null, null, null));
    }

    @Test
    public void testFilterChainingAndTrue() {
        FilterTestCondition c1 = new FilterTestCondition(5, 5, Op.Equals);
        FilterTestCondition c2 = new FilterTestCondition(10, 10, Op.Equals);
        c1.setJoin(Join.and);
        c1.setNextCondition(c2);
        assertTrue(c1.filter(null, null, null, null));
    }

    @Test
    public void testFilterChainingAndFalse() {
        FilterTestCondition c1 = new FilterTestCondition(5, 5, Op.Equals);
        FilterTestCondition c2 = new FilterTestCondition(10, 20, Op.Equals);
        c1.setJoin(Join.and);
        c1.setNextCondition(c2);
        assertFalse(c1.filter(null, null, null, null));
    }

    @Test
    public void testFilterChainingOrFirstTrue() {
        FilterTestCondition c1 = new FilterTestCondition(5, 5, Op.Equals);
        FilterTestCondition c2 = new FilterTestCondition(10, 20, Op.Equals);
        c1.setJoin(Join.or);
        c1.setNextCondition(c2);
        assertTrue(c1.filter(null, null, null, null));
    }

    @Test
    public void testFilterChainingOrFirstFalse() {
        FilterTestCondition c1 = new FilterTestCondition(5, 10, Op.Equals);
        FilterTestCondition c2 = new FilterTestCondition(20, 20, Op.Equals);
        c1.setJoin(Join.or);
        c1.setNextCondition(c2);
        assertTrue(c1.filter(null, null, null, null));
    }

    @Test
    public void testFilterChainingOrBothFalse() {
        FilterTestCondition c1 = new FilterTestCondition(5, 10, Op.Equals);
        FilterTestCondition c2 = new FilterTestCondition(15, 20, Op.Equals);
        c1.setJoin(Join.or);
        c1.setNextCondition(c2);
        assertFalse(c1.filter(null, null, null, null));
    }

    @Test
    public void testFilterChainingDeep() {
        FilterTestCondition c1 = new FilterTestCondition(1, 1, Op.Equals);
        FilterTestCondition c2 = new FilterTestCondition(2, 2, Op.Equals);
        FilterTestCondition c3 = new FilterTestCondition(3, 3, Op.Equals);
        c1.setJoin(Join.and);
        c1.setNextCondition(c2);
        c2.setJoin(Join.and);
        c2.setNextCondition(c3);
        assertTrue(c1.filter(null, null, null, null));
    }

    @Test
    public void testFilterChainingDeepFalse() {
        FilterTestCondition c1 = new FilterTestCondition(1, 1, Op.Equals);
        FilterTestCondition c2 = new FilterTestCondition(2, 99, Op.Equals);
        FilterTestCondition c3 = new FilterTestCondition(3, 3, Op.Equals);
        c1.setJoin(Join.and);
        c1.setNextCondition(c2);
        c2.setJoin(Join.and);
        c2.setNextCondition(c3);
        assertFalse(c1.filter(null, null, null, null));
    }

    @Test
    public void testFilterNoChaining() {
        FilterTestCondition c = new FilterTestCondition(5, 5, Op.Equals);
        c.setJoin(Join.and);
        assertTrue(c.filter(null, null, null, null));
    }
}
