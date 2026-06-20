package com.bstek.ureport.definition.searchform;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.*;

public class SearchformComponentsTest {

    private RenderContext createContext() {
        Map<String, Object> params = new HashMap<>();
        return new RenderContext(new HashMap<>(), params);
    }

    @Test
    public void testRenderContextBuildComponentId() {
        RenderContext ctx = createContext();
        Component c = new Component() {
            @Override public String toHtml(RenderContext context) { return ""; }
            @Override public String initJs(RenderContext context) { return ""; }
            @Override public String getType() { return "test"; }
        };
        String id1 = ctx.buildComponentId(c);
        assertTrue(id1.startsWith("__c_"));
        String id2 = ctx.buildComponentId(c);
        assertEquals(id1, id2);
    }

    @Test
    public void testRenderContextIncrementingIds() {
        RenderContext ctx = createContext();
        Component c1 = new Component() {
            @Override public String toHtml(RenderContext context) { return ""; }
            @Override public String initJs(RenderContext context) { return ""; }
            @Override public String getType() { return "a"; }
        };
        Component c2 = new Component() {
            @Override public String toHtml(RenderContext context) { return ""; }
            @Override public String initJs(RenderContext context) { return ""; }
            @Override public String getType() { return "b"; }
        };
        assertNotEquals(ctx.buildComponentId(c1), ctx.buildComponentId(c2));
    }

    @Test
    public void testRenderContextGetDataset() {
        RenderContext ctx = createContext();
        assertNull(ctx.getDataset("nonexistent"));
    }

    @Test
    public void testRenderContextGetParameter() {
        Map<String, Object> params = new HashMap<>();
        params.put("key", "value");
        RenderContext ctx = new RenderContext(new HashMap<>(), params);
        assertEquals("value", ctx.getParameter("key"));
        assertNull(ctx.getParameter("missing"));
    }

    @Test
    public void testRenderContextMetadata() {
        RenderContext ctx = createContext();
        assertNull(ctx.getMetadata("key"));
        ctx.putMetadata("key", "val");
        assertEquals("val", ctx.getMetadata("key"));
    }

    @Test
    public void testInputComponentToHtmlTop() {
        InputComponent ic = new InputComponent() {
            @Override String inputHtml(RenderContext context) {
                return "<input>";
            }
            @Override public String initJs(RenderContext context) { return ""; }
            @Override public String getType() { return "test"; }
        };
        ic.setLabel("MyLabel");
        ic.setLabelPosition(LabelPosition.top);
        String html = ic.toHtml(createContext());
        assertTrue(html.contains("MyLabel"));
        assertTrue(html.contains("<input>"));
        assertFalse(html.contains("form-horizontal"));
    }

    @Test
    public void testInputComponentToHtmlLeft() {
        InputComponent ic = new InputComponent() {
            @Override String inputHtml(RenderContext context) {
                return "<input>";
            }
            @Override public String initJs(RenderContext context) { return ""; }
            @Override public String getType() { return "test"; }
        };
        ic.setLabel("MyLabel");
        ic.setLabelPosition(LabelPosition.left);
        String html = ic.toHtml(createContext());
        assertTrue(html.contains("MyLabel"));
        assertTrue(html.contains("form-horizontal"));
        assertTrue(html.contains("col-md-3"));
        assertTrue(html.contains("col-md-9"));
    }

    @Test
    public void testButtonComponentToHtmlLeftAlign() {
        ButtonComponent bc = new ButtonComponent() {
            @Override public String initJs(RenderContext context) { return ""; }
            @Override public String getType() { return "button"; }
        };
        bc.setLabel("Click");
        bc.setStyle("btn-primary");
        bc.setAlign(Align.left);
        String html = bc.toHtml(createContext());
        assertTrue(html.contains("text-align:left"));
        assertTrue(html.contains("Click"));
        assertTrue(html.contains("btn-primary"));
    }

    @Test
    public void testButtonComponentToHtmlRightAlign() {
        ButtonComponent bc = new ButtonComponent() {
            @Override public String initJs(RenderContext context) { return ""; }
            @Override public String getType() { return "button"; }
        };
        bc.setLabel("Go");
        bc.setStyle("btn-danger");
        bc.setAlign(Align.right);
        String html = bc.toHtml(createContext());
        assertTrue(html.contains("text-align:right"));
        assertTrue(html.contains("btn-danger"));
    }

    @Test
    public void testTextInputComponentHtml() {
        TextInputComponent tc = new TextInputComponent();
        tc.setBindParameter("name");
        RenderContext ctx = createContext();
        String html = tc.toHtml(ctx);
        assertTrue(html.contains("type='text'"));
        assertTrue(html.contains("name='name'"));
    }

    @Test
    public void testTextInputComponentInitJs() {
        TextInputComponent tc = new TextInputComponent();
        tc.setBindParameter("name");
        String js = tc.initJs(createContext());
        assertTrue(js.contains("formElements.push"));
        assertTrue(js.contains("\"name\""));
    }

    @Test
    public void testSubmitButtonComponent() {
        SubmitButtonComponent sc = new SubmitButtonComponent();
        sc.setLabel("Search");
        String html = sc.toHtml(createContext());
        assertTrue(html.contains("Search"));
        String js = sc.initJs(createContext());
        assertTrue(js.contains("doSearch()"));
    }

    @Test
    public void testResetButtonComponent() {
        ResetButtonComponent rc = new ResetButtonComponent();
        rc.setLabel("Reset");
        rc.setStyle("btn-warning");
        String html = rc.toHtml(createContext());
        assertTrue(html.contains("type=\"reset\""));
        assertTrue(html.contains("btn-warning"));
        assertTrue(html.contains("Reset"));
        assertEquals("", rc.initJs(createContext()));
    }

    @Test
    public void testDateInputComponentHtml() {
        DateInputComponent dc = new DateInputComponent();
        dc.setBindParameter("date");
        String html = dc.toHtml(createContext());
        assertTrue(html.contains("input-group date"));
        assertTrue(html.contains("glyphicon-calendar"));
        assertTrue(html.contains("name='date'"));
    }

    @Test
    public void testDateInputComponentInitJs() {
        DateInputComponent dc = new DateInputComponent();
        dc.setBindParameter("date");
        dc.setFormat("yyyy-mm-dd");
        String js = dc.initJs(createContext());
        assertTrue(js.contains("datetimepicker"));
        assertTrue(js.contains("minView:2"));
    }

    @Test
    public void testCheckboxInputComponentHtml() {
        CheckboxInputComponent cc = new CheckboxInputComponent();
        cc.setBindParameter("colors");
        List<Option> opts = new ArrayList<>();
        Option o1 = new Option(); o1.setLabel("Red"); o1.setValue("red");
        Option o2 = new Option(); o2.setLabel("Blue"); o2.setValue("blue");
        opts.add(o1); opts.add(o2);
        cc.setOptions(opts);
        String html = cc.toHtml(createContext());
        assertTrue(html.contains("type='checkbox'"));
        assertTrue(html.contains("value='red'"));
        assertTrue(html.contains("value='blue'"));
    }

    @Test
    public void testRadioInputComponentHtml() {
        RadioInputComponent rc = new RadioInputComponent();
        rc.setBindParameter("gender");
        List<Option> opts = new ArrayList<>();
        Option o1 = new Option(); o1.setLabel("Male"); o1.setValue("M");
        Option o2 = new Option(); o2.setLabel("Female"); o2.setValue("F");
        opts.add(o1); opts.add(o2);
        rc.setOptions(opts);
        String html = rc.toHtml(createContext());
        assertTrue(html.contains("type='radio'"));
        assertTrue(html.contains("value='M'"));
        assertTrue(html.contains("value='F'"));
    }

    @Test
    public void testSearchFormToHtml() {
        SearchForm sf = new SearchForm();
        TextInputComponent tc = new TextInputComponent();
        tc.setBindParameter("name");
        sf.setComponents(Arrays.asList(tc));
        String html = sf.toHtml(createContext());
        assertTrue(html.startsWith("<form"));
        assertTrue(html.endsWith("</form>"));
        assertTrue(html.contains("type='text'"));
    }

    @Test
    public void testSearchFormToJs() {
        SearchForm sf = new SearchForm();
        TextInputComponent tc = new TextInputComponent();
        tc.setBindParameter("name");
        sf.setComponents(Arrays.asList(tc));
        String js = sf.toJs(createContext());
        assertTrue(js.contains("formElements.push"));
    }

    @Test
    public void testComponentInterfaceGetType() {
        Component c = new Component() {
            @Override public String toHtml(RenderContext context) { return ""; }
            @Override public String initJs(RenderContext context) { return ""; }
            @Override public String getType() { return "custom"; }
        };
        assertEquals("custom", c.getType());
    }

    @Test
    public void testGridComponentEmptyCols() {
        GridComponent gc = new GridComponent();
        gc.setCols(new ArrayList<>());
        gc.setShowBorder(true);
        gc.setBorderWidth(2);
        gc.setBorderColor("#ccc");
        assertTrue(gc.isShowBorder());
        assertEquals(2, gc.getBorderWidth());
        assertEquals("#ccc", gc.getBorderColor());
        assertTrue(gc.getCols().isEmpty());
    }

    @Test
    public void testGridComponentToHtmlWithoutBorder() {
        GridComponent gc = new GridComponent();
        gc.setCols(new ArrayList<>());
        String html = gc.toHtml(createContext());
        assertTrue(html.contains("class='row'"));
        assertTrue(html.startsWith("<div class='row'"));
    }

    @Test
    public void testGridComponentInitJs() {
        GridComponent gc = new GridComponent();
        gc.setCols(new ArrayList<>());
        assertEquals("", gc.initJs(createContext()));
    }

    @Test
    public void testColComponentDefaults() {
        ColComponent cc = new ColComponent();
        assertEquals(0, cc.getSize());
        assertNull(cc.getChildren());
        assertNull(cc.getType());
    }

    @Test
    public void testColComponentToHtml() {
        ColComponent cc = new ColComponent();
        cc.setSize(6);
        cc.setChildren(new ArrayList<>());
        String html = cc.toHtml(createContext());
        assertTrue(html.contains("class='col-md-6'"));
    }

    @Test
    public void testColComponentWithChildren() {
        ColComponent cc = new ColComponent();
        cc.setSize(12);
        TextInputComponent tc = new TextInputComponent();
        tc.setBindParameter("x");
        cc.setChildren(Arrays.asList(tc));
        String html = cc.toHtml(createContext());
        assertTrue(html.contains("col-md-12"));
        assertTrue(html.contains("type='text'"));
    }

    @Test
    public void testCheckboxInputComponentInline() {
        CheckboxInputComponent cc = new CheckboxInputComponent();
        cc.setBindParameter("colors");
        cc.setOptionsInline(true);
        List<Option> opts = new ArrayList<>();
        Option o1 = new Option(); o1.setLabel("Red"); o1.setValue("red");
        opts.add(o1);
        cc.setOptions(opts);
        String html = cc.toHtml(createContext());
        assertTrue(html.contains("checkbox-inline"));
        assertFalse(html.contains("class='checkbox'"));
    }

    @Test
    public void testCheckboxInputComponentInitJs() {
        CheckboxInputComponent cc = new CheckboxInputComponent();
        cc.setBindParameter("colors");
        List<Option> opts = new ArrayList<>();
        opts.add(new Option());
        cc.setOptions(opts);
        String js = cc.initJs(createContext());
        assertTrue(js.contains("formElements.push"));
        assertTrue(js.contains("input[name='colors']:checked"));
    }

    @Test
    public void testRadioInputComponentInline() {
        RadioInputComponent rc = new RadioInputComponent();
        rc.setBindParameter("gender");
        rc.setOptionsInline(true);
        List<Option> opts = new ArrayList<>();
        Option o1 = new Option(); o1.setLabel("Male"); o1.setValue("M");
        opts.add(o1);
        rc.setOptions(opts);
        String html = rc.toHtml(createContext());
        assertTrue(html.contains("checkbox-inline"));
    }

    @Test
    public void testRadioInputComponentInitJs() {
        RadioInputComponent rc = new RadioInputComponent();
        rc.setBindParameter("gender");
        List<Option> opts = new ArrayList<>();
        opts.add(new Option());
        rc.setOptions(opts);
        String js = rc.initJs(createContext());
        assertTrue(js.contains("formElements.push"));
        assertTrue(js.contains("input[name='gender']:checked"));
    }

    @Test
    public void testSelectInputComponentOptionsMode() {
        SelectInputComponent sc = new SelectInputComponent();
        sc.setBindParameter("city");
        sc.setUseDataset(false);
        List<Option> opts = new ArrayList<>();
        Option o1 = new Option(); o1.setLabel("NYC"); o1.setValue("nyc");
        Option o2 = new Option(); o2.setLabel("LA"); o2.setValue("la");
        opts.add(o1); opts.add(o2);
        sc.setOptions(opts);
        String html = sc.toHtml(createContext());
        assertTrue(html.contains("<select"));
        assertTrue(html.contains("value='nyc'"));
        assertTrue(html.contains("value='la'"));
        assertTrue(html.contains("</select>"));
    }

    @Test
    public void testSelectInputComponentInitJs() {
        SelectInputComponent sc = new SelectInputComponent();
        sc.setBindParameter("city");
        String js = sc.initJs(createContext());
        assertTrue(js.contains("$('#"));
        assertTrue(js.contains("').val()"));
    }

    @Test
    public void testSelectInputComponentUseDatasetThrows() {
        SelectInputComponent sc = new SelectInputComponent();
        sc.setBindParameter("city");
        sc.setUseDataset(true);
        sc.setDataset("nonexistent");
        assertThrows(com.bstek.ureport.exception.DatasetUndefinitionException.class,
            () -> sc.toHtml(createContext()));
    }

    @Test
    public void testInputComponentGetSet() {
        InputComponent ic = new InputComponent() {
            @Override String inputHtml(RenderContext context) { return ""; }
            @Override public String initJs(RenderContext context) { return ""; }
        };
        ic.setBindParameter("param1");
        assertEquals("param1", ic.getBindParameter());
        assertEquals(LabelPosition.top, ic.getLabelPosition());
        ic.setType("custom");
        assertEquals("custom", ic.getType());
    }

    @Test
    public void testButtonComponentGetSet() {
        ButtonComponent bc = new ButtonComponent() {
            @Override public String initJs(RenderContext context) { return ""; }
            @Override public String getType() { return "btn"; }
        };
        assertEquals(Align.left, bc.getAlign());
        bc.setType("btn");
        assertEquals("btn", bc.getType());
    }
}
