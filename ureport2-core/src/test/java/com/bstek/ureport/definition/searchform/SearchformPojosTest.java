package com.bstek.ureport.definition.searchform;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class SearchformPojosTest {

    @Test
    public void testAlignEnum() {
        assertEquals("left", Align.left.name());
        assertEquals("right", Align.right.name());
    }

    @Test
    public void testLabelPositionEnum() {
        assertEquals("top", LabelPosition.top.name());
        assertEquals("left", LabelPosition.left.name());
    }

    @Test
    public void testFormPositionEnum() {
        assertEquals("up", FormPosition.up.name());
        assertEquals("down", FormPosition.down.name());
    }

    @Test
    public void testOptionDefaults() {
        Option opt = new Option();
        assertNull(opt.getLabel());
        assertNull(opt.getValue());
    }

    @Test
    public void testOptionSetters() {
        Option opt = new Option();
        opt.setLabel("Yes");
        opt.setValue("1");
        assertEquals("Yes", opt.getLabel());
        assertEquals("1", opt.getValue());
    }

    @Test
    public void testContainerComponent() {
        ContainerComponent cc = new ContainerComponent() {
            @Override public String toHtml(RenderContext context) { return ""; }
            @Override public String initJs(RenderContext context) { return ""; }
            @Override public String getType() { return "test"; }
        };
        assertNull(cc.getChildren());
        List<Component> children = new ArrayList<>();
        cc.setChildren(children);
        assertSame(children, cc.getChildren());
    }

    @Test
    public void testSearchFormDefaults() {
        SearchForm sf = new SearchForm();
        assertNull(sf.getComponents());
        assertNull(sf.getFormPosition());
    }

    @Test
    public void testSearchFormSetters() {
        SearchForm sf = new SearchForm();
        sf.setFormPosition(FormPosition.down);
        assertEquals(FormPosition.down, sf.getFormPosition());
    }
}
