package com.bstek.ureport.build.assertor;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class AssertorTest {

    @Test
    public void testEqualsAssertorBothNull() {
        EqualsAssertor eq = new EqualsAssertor();
        assertTrue(eq.eval(null, null));
    }

    @Test
    public void testEqualsAssertorOneNull() {
        EqualsAssertor eq = new EqualsAssertor();
        assertFalse(eq.eval(null, "test"));
        assertFalse(eq.eval("test", null));
    }

    @Test
    public void testEqualsAssertorNumbers() {
        EqualsAssertor eq = new EqualsAssertor();
        assertTrue(eq.eval(1, 1));
        assertTrue(eq.eval(1, 1.0));
        assertTrue(eq.eval(1, "1"));
        assertFalse(eq.eval(1, 2));
    }

    @Test
    public void testEqualsAssertorStrings() {
        EqualsAssertor eq = new EqualsAssertor();
        assertTrue(eq.eval("hello", "hello"));
        assertFalse(eq.eval("hello", "world"));
    }

    @Test
    public void testEqualsAssertorNumberVsString() {
        EqualsAssertor eq = new EqualsAssertor();
        assertTrue(eq.eval(1, "1.0"));
        assertFalse(eq.eval(1, "abc"));
    }

    @Test
    public void testEqualsAssertorListUnwrap() {
        EqualsAssertor eq = new EqualsAssertor();
        List<String> list = Arrays.asList("hello");
        assertTrue(eq.eval("hello", list));
    }

    @Test
    public void testGreatThenAssertor() {
        GreatThenAssertor gt = new GreatThenAssertor();
        assertTrue(gt.eval(5, 3));
        assertFalse(gt.eval(3, 5));
        assertFalse(gt.eval(3, 3));
    }

    @Test
    public void testGreatThenAssertorNull() {
        GreatThenAssertor gt = new GreatThenAssertor();
        assertFalse(gt.eval(null, 1));
        assertFalse(gt.eval(1, null));
        assertFalse(gt.eval(null, null));
    }

    @Test
    public void testGreatThenAssertorBlank() {
        GreatThenAssertor gt = new GreatThenAssertor();
        assertFalse(gt.eval("", 1));
        assertFalse(gt.eval(1, ""));
    }

    @Test
    public void testGreatThenAssertorStrings() {
        GreatThenAssertor gt = new GreatThenAssertor();
        assertTrue(gt.eval("5", "3"));
        assertFalse(gt.eval("3", "5"));
    }

    @Test
    public void testLikeAssertor() {
        LikeAssertor like = new LikeAssertor();
        assertTrue(like.eval("hello world", "hello"));
        assertTrue(like.eval("hello", "hello"));
        assertFalse(like.eval("hello", "world"));
    }

    @Test
    public void testLikeAssertorNull() {
        LikeAssertor like = new LikeAssertor();
        assertFalse(like.eval(null, "test"));
        assertFalse(like.eval("test", null));
        assertFalse(like.eval(null, null));
    }

    @Test
    public void testInAssertorWithList() {
        InAssertor in = new InAssertor();
        List<String> list = Arrays.asList("a", "b", "c");
        assertTrue(in.eval("a", list));
        assertFalse(in.eval("d", list));
    }

    @Test
    public void testInAssertorWithArray() {
        InAssertor in = new InAssertor();
        String[] arr = {"a", "b", "c"};
        assertTrue(in.eval("a", arr));
        assertFalse(in.eval("d", arr));
    }

    @Test
    public void testInAssertorWithCommaString() {
        InAssertor in = new InAssertor();
        assertTrue(in.eval("a", "a,b,c"));
        assertFalse(in.eval("d", "a,b,c"));
    }

    @Test
    public void testInAssertorWithSingleValue() {
        InAssertor in = new InAssertor();
        assertTrue(in.eval("hello", "hello"));
        assertFalse(in.eval("hello", "world"));
    }

    @Test
    public void testInAssertorNull() {
        InAssertor in = new InAssertor();
        assertFalse(in.eval(null, "test"));
        assertFalse(in.eval("test", null));
        assertFalse(in.eval(null, null));
    }

    @Test
    public void testNotEqualsAssertor() {
        NotEqualsAssertor ne = new NotEqualsAssertor();
        assertFalse(ne.eval(null, null));
        assertTrue(ne.eval(null, "test"));
        assertTrue(ne.eval("test", null));
        assertTrue(ne.eval("hello", "world"));
        assertFalse(ne.eval("hello", "hello"));
    }

    @Test
    public void testLessThenAssertor() {
        LessThenAssertor lt = new LessThenAssertor();
        assertTrue(lt.eval(3, 5));
        assertFalse(lt.eval(5, 3));
        assertFalse(lt.eval(3, 3));
    }

    @Test
    public void testLessThenAssertorNull() {
        LessThenAssertor lt = new LessThenAssertor();
        assertFalse(lt.eval(null, 1));
        assertFalse(lt.eval(1, null));
    }

    @Test
    public void testLessThenAssertorBlank() {
        LessThenAssertor lt = new LessThenAssertor();
        assertFalse(lt.eval("", 1));
        assertFalse(lt.eval(1, ""));
    }

    @Test
    public void testEqualsGreatThenAssertor() {
        EqualsGreatThenAssertor egt = new EqualsGreatThenAssertor();
        assertTrue(egt.eval(5, 3));
        assertTrue(egt.eval(3, 3));
        assertFalse(egt.eval(3, 5));
    }

    @Test
    public void testEqualsGreatThenAssertorNull() {
        EqualsGreatThenAssertor egt = new EqualsGreatThenAssertor();
        assertFalse(egt.eval(null, 1));
        assertFalse(egt.eval(1, null));
    }

    @Test
    public void testEqualsLessThenAssertor() {
        EqualsLessThenAssertor elt = new EqualsLessThenAssertor();
        assertTrue(elt.eval(3, 5));
        assertTrue(elt.eval(3, 3));
        assertFalse(elt.eval(5, 3));
    }

    @Test
    public void testEqualsLessThenAssertorNull() {
        EqualsLessThenAssertor elt = new EqualsLessThenAssertor();
        assertFalse(elt.eval(null, 1));
        assertFalse(elt.eval(1, null));
    }

    @Test
    public void testNotInAssertorWithList() {
        NotInAssertor ni = new NotInAssertor();
        List<String> list = Arrays.asList("a", "b", "c");
        assertFalse(ni.eval("a", list));
        assertTrue(ni.eval("d", list));
    }

    @Test
    public void testNotInAssertorWithArray() {
        NotInAssertor ni = new NotInAssertor();
        String[] arr = {"a", "b", "c"};
        assertFalse(ni.eval("a", arr));
        assertTrue(ni.eval("d", arr));
    }

    @Test
    public void testNotInAssertorWithCommaString() {
        NotInAssertor ni = new NotInAssertor();
        assertFalse(ni.eval("a", "a,b,c"));
        assertTrue(ni.eval("d", "a,b,c"));
    }

    @Test
    public void testNotInAssertorNull() {
        NotInAssertor ni = new NotInAssertor();
        assertFalse(ni.eval(null, "test"));
        assertFalse(ni.eval("test", null));
    }
}
