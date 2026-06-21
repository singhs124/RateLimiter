package com.sushant.RateLimiter.infra.dsRouting;

import com.sushant.RateLimiter.infra.dto.DSType;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DSRoutingAspect {

    @Before("@annotation(ReadOnlyConnection)")
    public void setReadDS(){
        DSContextHolder.set(DSType.READ);
    }

    @After("@annotation(ReadOnlyConnection)")
    public void clearDS(){
        DSContextHolder.clear();
    }
}
