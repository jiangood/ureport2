package com.bstek.ureport.expression.function;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.bstek.ureport.build.BindData;
import com.bstek.ureport.expression.model.data.BindDataListExpressionData;
import com.bstek.ureport.expression.model.data.ExpressionData;
import com.bstek.ureport.expression.model.data.ObjectExpressionData;
import com.bstek.ureport.expression.model.data.ObjectListExpressionData;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class StandaloneFunctionTest {

    /* ===== CountFunction ===== */
    @Test
    public void testCountNullAndEmpty() {
        CountFunction f = new CountFunction();
        assertEquals("count", f.name());
        assertNull(f.execute(null, null, null));
        assertNull(f.execute(new ArrayList<>(), null, null));
    }

    @Test
    public void testCountObjectExpression() {
        CountFunction f = new CountFunction();
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectExpressionData("a"));
        assertEquals(1, f.execute(list, null, null));
    }

    @Test
    public void testCountObjectList() {
        CountFunction f = new CountFunction();
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectListExpressionData(Arrays.asList(1, 2, 3)));
        assertEquals(3, f.execute(list, null, null));
    }

    @Test
    public void testCountBindDataList() {
        CountFunction f = new CountFunction();
        List<BindData> bindDataList = new ArrayList<>();
        bindDataList.add(new BindData(1));
        bindDataList.add(new BindData(2));
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new BindDataListExpressionData(bindDataList));
        assertEquals(2, f.execute(list, null, null));
    }

    @Test
    public void testCountMixed() {
        CountFunction f = new CountFunction();
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectExpressionData("a"));
        list.add(new ObjectListExpressionData(Arrays.asList(1, 2)));
        list.add(new BindDataListExpressionData(Arrays.asList(new BindData(10), new BindData(20))));
        assertEquals(5, f.execute(list, null, null));
    }

    /* ===== MaxFunction ===== */
    @Test
    public void testMaxNullAndEmpty() {
        MaxFunction f = new MaxFunction();
        assertEquals("max", f.name());
        assertNull(f.execute(null, null, null));
        assertNull(f.execute(new ArrayList<>(), null, null));
    }

    @Test
    public void testMaxObjectExpression() {
        MaxFunction f = new MaxFunction();
        assertEquals(new BigDecimal(10), f.execute(singleData(10), null, null));
    }

    @Test
    public void testMaxObjectList() {
        MaxFunction f = new MaxFunction();
        assertEquals(new BigDecimal(5), f.execute(listData(1, 3, 5, 2, 4), null, null));
    }

    @Test
    public void testMaxBindDataList() {
        MaxFunction f = new MaxFunction();
        List<BindData> bindDataList = new ArrayList<>();
        bindDataList.add(new BindData(10));
        bindDataList.add(new BindData(20));
        bindDataList.add(new BindData(5));
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new BindDataListExpressionData(bindDataList));
        assertEquals(new BigDecimal(20), f.execute(list, null, null));
    }

    /* ===== MinFunction ===== */
    @Test
    public void testMinNullAndEmpty() {
        MinFunction f = new MinFunction();
        assertEquals("min", f.name());
        assertNull(f.execute(null, null, null));
        assertNull(f.execute(new ArrayList<>(), null, null));
    }

    @Test
    public void testMinObjectList() {
        MinFunction f = new MinFunction();
        assertEquals(new BigDecimal(1), f.execute(listData(3, 1, 5, 2, 4), null, null));
    }

    /* ===== AvgFunction ===== */
    @Test
    public void testAvgNullAndEmpty() {
        AvgFunction f = new AvgFunction();
        assertEquals("avg", f.name());
        assertNull(f.execute(null, null, null));
        assertNull(f.execute(new ArrayList<>(), null, null));
    }

    @Test
    public void testAvgObjectExpression() {
        AvgFunction f = new AvgFunction();
        assertEquals(new BigDecimal(10), f.execute(singleData(10), null, null));
    }

    @Test
    public void testAvgObjectList() {
        AvgFunction f = new AvgFunction();
        BigDecimal result = (BigDecimal) f.execute(listData(1, 2, 3, 4, 5), null, null);
        assertEquals(0, result.compareTo(new BigDecimal(3)));
    }

    @Test
    public void testAvgWithNullValues() {
        AvgFunction f = new AvgFunction();
        BigDecimal result = (BigDecimal) f.execute(listData(1, null, 2, null, 3), null, null);
        assertEquals(0, result.compareTo(new BigDecimal(2)));
    }

    /* ===== ListFunction ===== */
    @Test
    public void testListBasic() {
        ListFunction f = new ListFunction();
        assertEquals("list", f.name());
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectExpressionData("a"));
        list.add(new ObjectExpressionData("b"));
        Object result = f.execute(list, null, null);
        assertTrue(result instanceof List);
        assertEquals(Arrays.asList("a", "b"), result);
    }

    @Test
    public void testListWithObjectList() {
        ListFunction f = new ListFunction();
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectExpressionData("a"));
        list.add(new ObjectListExpressionData(Arrays.asList(1, 2, 3)));
        Object result = f.execute(list, null, null);
        List<?> resultList = (List<?>) result;
        assertEquals(4, resultList.size());
        assertEquals("a", resultList.get(0));
        assertEquals(1, resultList.get(1));
    }

    /* ===== GetFunction ===== */
    @Test
    public void testGetBasic() {
        GetFunction f = new GetFunction();
        assertEquals("get", f.name());
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectListExpressionData(Arrays.asList("a", "b", "c")));
        assertEquals("a", f.execute(list, null, null));
    }

    @Test
    public void testGetWithIndex() {
        GetFunction f = new GetFunction();
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectListExpressionData(Arrays.asList("a", "b", "c")));
        list.add(new ObjectExpressionData(2));
        assertEquals("b", f.execute(list, null, null));
    }

    @Test
    public void testGetIndexOutOfBounds() {
        GetFunction f = new GetFunction();
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectListExpressionData(Arrays.asList("a", "b")));
        list.add(new ObjectExpressionData(10));
        assertEquals("b", f.execute(list, null, null));
    }

    /* ===== OrderFunction ===== */
    @Test
    public void testOrderAsc() {
        OrderFunction f = new OrderFunction();
        assertEquals("order", f.name());
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectListExpressionData(Arrays.asList(3, 1, 2)));
        list.add(new ObjectExpressionData(true));
        Object result = f.execute(list, null, null);
        assertEquals(Arrays.asList(1, 2, 3), result);
    }

    @Test
    public void testOrderDesc() {
        OrderFunction f = new OrderFunction();
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectListExpressionData(Arrays.asList(1, 3, 2)));
        list.add(new ObjectExpressionData(false));
        Object result = f.execute(list, null, null);
        assertEquals(Arrays.asList(3, 2, 1), result);
    }

    @Test
    public void testOrderStringAsc() {
        OrderFunction f = new OrderFunction();
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectListExpressionData(Arrays.asList("c", "a", "b")));
        list.add(new ObjectExpressionData(true));
        Object result = f.execute(list, null, null);
        assertEquals(Arrays.asList("a", "b", "c"), result);
    }

    /* ===== FormatNumberFunction ===== */
    @Test
    public void testFormatNumberDefault() {
        FormatNumberFunction f = new FormatNumberFunction();
        assertEquals("formatnumber", f.name());
        assertEquals("1235", f.execute(singleData(1234.5678), null, null));
    }

    @Test
    public void testFormatNumberWithPattern() {
        FormatNumberFunction f = new FormatNumberFunction();
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectExpressionData(1234.5678));
        list.add(new ObjectExpressionData("#,###.00"));
        assertEquals("1,234.57", f.execute(list, null, null));
    }

    /* ===== FormatDateFunction ===== */
    @Test
    public void testFormatDateDefault() {
        FormatDateFunction f = new FormatDateFunction();
        assertEquals("formatdate", f.name());
        assertThrows(Exception.class, () -> f.execute(new ArrayList<>(), null, null));
    }

    @Test
    public void testFormatDateWithPattern() throws Exception {
        FormatDateFunction f = new FormatDateFunction();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse("2024-01-15 10:30:00");
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectExpressionData(date));
        assertEquals("2024-01-15 10:30:00", f.execute(list, null, null));
    }

    @Test
    public void testFormatDateCustomPattern() throws Exception {
        FormatDateFunction f = new FormatDateFunction();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse("2024-01-15");
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectListExpressionData(Arrays.asList(date, "yyyy/MM/dd")));
        assertEquals("2024/01/15", f.execute(list, null, null));
    }

    /* ===== JsonFunction ===== */
    @Test
    public void testJsonNullWhenWrongArgCount() {
        JsonFunction f = new JsonFunction();
        assertEquals("json", f.name());
        assertNull(f.execute(singleData("{}"), null, null));
    }

    @Test
    public void testJsonBasic() {
        JsonFunction f = new JsonFunction();
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectExpressionData("{\"name\":\"Alice\",\"age\":30}"));
        list.add(new ObjectExpressionData("name"));
        assertEquals("Alice", f.execute(list, null, null));
    }

    @Test
    public void testJsonNestedProperty() {
        JsonFunction f = new JsonFunction();
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectExpressionData("{\"user\":{\"name\":\"Bob\"}}"));
        list.add(new ObjectExpressionData("user"));
        Object result = f.execute(list, null, null);
        assertNotNull(result);
    }

    /* ===== helpers ===== */
    private List<ExpressionData<?>> singleData(Object value) {
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectExpressionData(value));
        return list;
    }

    private List<ExpressionData<?>> listData(Object... values) {
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectListExpressionData(Arrays.asList(values)));
        return list;
    }
}
