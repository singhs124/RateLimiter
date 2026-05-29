package com.sushant.RateLimiter.simulation;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class RateLimiterLoadTest extends Simulation {
    List<Map<String, Object>> ipPool = List.of(
            Map.of("randomIp", "192.168.1.1"),
            Map.of("randomIp", "192.168.1.2"),
            Map.of("randomIp", "192.168.1.3"),
            Map.of("randomIp", "192.168.1.4"),
            Map.of("randomIp", "192.168.1.5")
    );

    Iterator<Map<String,Object>> circularFeeder = Stream.generate(()->ipPool).flatMap(List::stream).iterator();

//    Iterator<Map<String, Object>> ipFeeder =
//            Stream.generate(()->{
//                String randomIp = String.format("%d.%d.%d.%d",
//                        (int)(Math.random()*255),(int)(Math.random()*255),
//                        (int)(Math.random()*255),(int)(Math.random()*255));
//                return Collections.singletonMap("randomIp", (Object) randomIp);
//            }).iterator();
    HttpProtocolBuilder httpProtocolBuilder = http.baseUrl("http://localhost:8080");

    ScenarioBuilder scn = scenario("Rate Limit Test")
            .feed(circularFeeder)
            .exec(http("Request")
                    .get("/test/")
                    .header("X-Forwarded-For", "#{randomIp}")
                    .check(status().in(200,404)));

    {
        setUp(
                scn.injectOpen(
                        atOnceUsers(50),            // Immediately fill the 5 buckets (4 reqs per IP) => 20 req
                        nothingFor(Duration.ofSeconds(30)), // Wait for the bucket to "leak" some space (1 min sleep) => 2 req per IP we can accomodate => 10 req
                        atOnceUsers(10)             // These should now be 200 OK again as the bucket leaked
                )
        ).protocols(httpProtocolBuilder);
    }
}

