package com.bstek.ureport.utils;

public class UStringEscapeUtils {
    public static String escapeHtml4(String input) {
        if (input == null) {
            return null;
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '&':  out.append("&amp;"); break;
                case '<':  out.append("&lt;"); break;
                case '>':  out.append("&gt;"); break;
                case '"':  out.append("&quot;"); break;
                case '\'': out.append("&#x27;"); break;
                case '/':  out.append("&#x2F;"); break;
                default:   out.append(c);
            }
        }
        return out.toString();
    }
}
