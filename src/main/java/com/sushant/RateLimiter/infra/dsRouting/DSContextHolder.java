package com.sushant.RateLimiter.infra.dsRouting;

import com.sushant.RateLimiter.infra.dto.DSType;

public class DSContextHolder {
    private static final ThreadLocal<DSType> CONTEXT = new ThreadLocal<>();

    public static void set(DSType type){
        CONTEXT.set(type);
    }

    public static DSType getDSType(){
        return CONTEXT.get() == null ? DSType.WRITE : CONTEXT.get();
    }

    public static void clear(){
        CONTEXT.remove();
    }
}
