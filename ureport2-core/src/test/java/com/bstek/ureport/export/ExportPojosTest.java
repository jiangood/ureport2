package com.bstek.ureport.export;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.bstek.ureport.build.paging.Page;

import java.io.ByteArrayOutputStream;
import java.util.*;

public class ExportPojosTest {

    @Test
    public void testFullPageData() {
        List<List<Page>> pageList = new ArrayList<>();
        pageList.add(new ArrayList<>());

        FullPageData data = new FullPageData(10, 5, pageList);
        assertEquals(10, data.getTotalPages());
        assertEquals(5, data.getColumnMargin());
        assertSame(pageList, data.getPageList());
    }

    @Test
    public void testSinglePageDataWithPageIndex() {
        List<Page> pages = new ArrayList<>();
        SinglePageData data = new SinglePageData(20, 3, 8, pages);
        assertEquals(20, data.getTotalPages());
        assertEquals(3, data.getPageIndex());
        assertEquals(8, data.getColumnMargin());
        assertSame(pages, data.getPages());
    }

    @Test
    public void testSinglePageDataWithoutPageIndex() {
        List<Page> pages = new ArrayList<>();
        SinglePageData data = new SinglePageData(5, 2, pages);
        assertEquals(5, data.getTotalPages());
        assertEquals(0, data.getPageIndex());
        assertEquals(2, data.getColumnMargin());
        assertSame(pages, data.getPages());
    }

    @Test
    public void testExportConfigureImpl() {
        Map<String, Object> params = new HashMap<>();
        params.put("key", "value");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ExportConfigureImpl config = new ExportConfigureImpl("test.ureport.xml", params, baos);
        assertEquals("test.ureport.xml", config.getFile());
        assertSame(params, config.getParameters());
        assertSame(baos, config.getOutputStream());
    }
}
