package com.bstek.ureport.utils;

import java.util.concurrent.ThreadLocalRandom;

public class URandomUtils {
    public static int nextInt(int n) {
        return ThreadLocalRandom.current().nextInt(n);
    }
}
