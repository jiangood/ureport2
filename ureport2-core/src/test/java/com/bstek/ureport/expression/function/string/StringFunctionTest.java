package com.bstek.ureport.expression.function.string;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.bstek.ureport.expression.model.data.ExpressionData;
import com.bstek.ureport.expression.model.data.ObjectExpressionData;
import com.bstek.ureport.expression.model.data.ObjectListExpressionData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringFunctionTest {

    /* ===== UpperFunction ===== */
    @Test
    public void testUpper() {
        UpperFunction f = new UpperFunction();
        assertEquals("upper", f.name());
        assertEquals("HELLO", f.execute(singleData("hello"), null, null));
        assertEquals("ABC", f.execute(singleData("abc"), null, null));
        assertEquals("123", f.execute(singleData("123"), null, null));
    }

    /* ===== LowerFunction ===== */
    @Test
    public void testLower() {
        LowerFunction f = new LowerFunction();
        assertEquals("lower", f.name());
        assertEquals("hello", f.execute(singleData("HELLO"), null, null));
        assertEquals("abc", f.execute(singleData("ABC"), null, null));
    }

    /* ===== TrimFunction ===== */
    @Test
    public void testTrim() {
        TrimFunction f = new TrimFunction();
        assertEquals("trim", f.name());
        assertEquals("hello", f.execute(singleData("  hello  "), null, null));
        assertEquals("a b", f.execute(singleData("  a b  "), null, null));
        assertEquals("", f.execute(singleData("   "), null, null));
    }

    /* ===== LengthFunction ===== */
    @Test
    public void testLength() {
        LengthFunction f = new LengthFunction();
        assertEquals("length", f.name());
        assertEquals(5, f.execute(singleData("hello"), null, null));
        assertEquals(0, f.execute(singleData(""), null, null));
    }

    /* ===== IndexOfFunction ===== */
    @Test
    public void testIndexOfBasic() {
        IndexOfFunction f = new IndexOfFunction();
        assertEquals("indexof", f.name());
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectExpressionData("hello world"));
        list.add(new ObjectExpressionData("world"));
        assertEquals(6, f.execute(list, null, null));
    }

    @Test
    public void testIndexOfNotFound() {
        IndexOfFunction f = new IndexOfFunction();
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectExpressionData("hello"));
        list.add(new ObjectExpressionData("x"));
        assertEquals(-1, f.execute(list, null, null));
    }

    @Test
    public void testIndexOfWithStart() {
        IndexOfFunction f = new IndexOfFunction();
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectExpressionData("aaaa"));
        list.add(new ObjectExpressionData("a"));
        list.add(new ObjectExpressionData(2));
        assertEquals(2, f.execute(list, null, null));
    }

    /* ===== SubstringFunction ===== */
    @Test
    public void testSubstringBasic() {
        SubstringFunction f = new SubstringFunction();
        assertEquals("substring", f.name());
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectExpressionData("hello"));
        list.add(new ObjectExpressionData(1));
        list.add(new ObjectExpressionData(4));
        assertEquals("ell", f.execute(list, null, null));
    }

    @Test
    public void testSubstringFromStart() {
        SubstringFunction f = new SubstringFunction();
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectExpressionData("hello"));
        list.add(new ObjectExpressionData(0));
        list.add(new ObjectExpressionData(2));
        assertEquals("he", f.execute(list, null, null));
    }

    /* ===== ReplaceFunction ===== */
    @Test
    public void testReplace() {
        ReplaceFunction f = new ReplaceFunction();
        assertEquals("replace", f.name());
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectExpressionData("hello world"));
        list.add(new ObjectExpressionData("world"));
        list.add(new ObjectExpressionData("java"));
        assertEquals("hello java", f.execute(list, null, null));
    }

    @Test
    public void testReplaceRegex() {
        ReplaceFunction f = new ReplaceFunction();
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectExpressionData("a1b2c3"));
        list.add(new ObjectExpressionData("\\d"));
        list.add(new ObjectExpressionData(""));
        assertEquals("abc", f.execute(list, null, null));
    }

    /* ===== helpers ===== */
    private List<ExpressionData<?>> singleData(Object value) {
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectExpressionData(value));
        return list;
    }
}
