package com.bstek.ureport.expression.function.date;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.bstek.ureport.expression.model.data.ExpressionData;
import com.bstek.ureport.expression.model.data.ObjectExpressionData;
import com.bstek.ureport.expression.model.data.ObjectListExpressionData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateFunctionTest {

    /* ===== YearFunction ===== */
    @Test
    public void testYear() {
        YearFunction f = new YearFunction();
        assertEquals("year", f.name());
        Calendar c = Calendar.getInstance();
        c.set(2024, Calendar.JANUARY, 15);
        assertEquals(2024, f.execute(singleData(c.getTime()), null, null));
    }

    @Test
    public void testYearDefault() {
        YearFunction f = new YearFunction();
        int expectedYear = Calendar.getInstance().get(Calendar.YEAR);
        assertEquals(expectedYear, f.execute(new ArrayList<>(), null, null));
    }

    /* ===== MonthFunction ===== */
    @Test
    public void testMonth() {
        MonthFunction f = new MonthFunction();
        assertEquals("month", f.name());
        Calendar c = Calendar.getInstance();
        c.set(2024, Calendar.JANUARY, 15);
        assertEquals(1, f.execute(singleData(c.getTime()), null, null));
    }

    @Test
    public void testMonthDefault() {
        MonthFunction f = new MonthFunction();
        int expectedMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        assertEquals(expectedMonth, f.execute(new ArrayList<>(), null, null));
    }

    @Test
    public void testMonthDecember() {
        MonthFunction f = new MonthFunction();
        Calendar c = Calendar.getInstance();
        c.set(2024, Calendar.DECEMBER, 15);
        assertEquals(12, f.execute(singleData(c.getTime()), null, null));
    }

    /* ===== DayFunction ===== */
    @Test
    public void testDay() {
        DayFunction f = new DayFunction();
        assertEquals("day", f.name());
        Calendar c = Calendar.getInstance();
        c.set(2024, Calendar.JANUARY, 15);
        assertEquals(15, f.execute(singleData(c.getTime()), null, null));
    }

    @Test
    public void testDayDefault() {
        DayFunction f = new DayFunction();
        int expectedDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        assertEquals(expectedDay, f.execute(new ArrayList<>(), null, null));
    }

    /* ===== WeekFunction ===== */
    @Test
    public void testWeek() {
        WeekFunction f = new WeekFunction();
        assertEquals("week", f.name());
        Calendar c = Calendar.getInstance();
        c.set(2024, Calendar.JANUARY, 1);
        String result = (String) f.execute(singleData(c.getTime()), null, null);
        assertNotNull(result);
        assertTrue(result.contains("星期"));
    }

    @Test
    public void testWeekDefault() {
        WeekFunction f = new WeekFunction();
        String result = (String) f.execute(new ArrayList<>(), null, null);
        assertNotNull(result);
        assertTrue(result.contains("星期"));
    }

    /* ===== DateFunction ===== */
    @Test
    public void testDateFormatDefault() {
        DateFunction f = new DateFunction();
        assertEquals("date", f.name());
        String result = (String) f.execute(new ArrayList<>(), null, null);
        assertNotNull(result);
        assertTrue(result.contains("-"));
    }

    @Test
    public void testDateFormatWithPattern() {
        DateFunction f = new DateFunction();
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectExpressionData("yyyy/MM/dd"));
        String result = (String) f.execute(list, null, null);
        assertNotNull(result);
        assertTrue(result.contains("/"));
    }

    @Test
    public void testDateFormatWithDateAndPattern() {
        DateFunction f = new DateFunction();
        Calendar c = Calendar.getInstance();
        c.set(2024, Calendar.JANUARY, 15, 10, 30, 0);
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectExpressionData(c.getTime()));
        list.add(new ObjectExpressionData("yyyy-MM-dd"));
        assertEquals("2024-01-15", f.execute(list, null, null));
    }

    /* ===== helpers ===== */
    private List<ExpressionData<?>> singleData(Object value) {
        List<ExpressionData<?>> list = new ArrayList<>();
        list.add(new ObjectExpressionData(value));
        return list;
    }
}
