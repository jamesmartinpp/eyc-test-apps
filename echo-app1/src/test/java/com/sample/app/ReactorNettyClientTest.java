package com.sample.app;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReactorNettyClientTest {

    @Test public void testNettyClientHttp2() {

        List<String> testREquests = Arrays.asList(
                "this is a test one",
                "this is a test two",
                "this is a test three",
                "this is a test four"
        );
        List<Exception> exceptions = new ArrayList<>();
        Echo echo = new Echo();
        List<String> responses = testREquests.stream()
                .map(s -> {
                    try {
                        return echo.echo(s);
                    }
                    catch(Exception ex) {
                        exceptions.add(ex);
                    }
                    return "ERROR";
                } )
                .collect(Collectors.toList());

        Assert.assertEquals(responses.size(), testREquests.size());
        Assert.assertTrue(responses.contains("ERROR"));
        Assert.assertTrue(responses.size() > 0);
        Assert.assertTrue(exceptions.size() > 0);
    }
}
