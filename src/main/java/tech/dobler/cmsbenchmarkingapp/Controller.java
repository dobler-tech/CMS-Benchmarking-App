package tech.dobler.cmsbenchmarkingapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class Controller {
    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);
    private static final AtomicInteger getCounter = new AtomicInteger();
    private static final AtomicInteger postCounter = new AtomicInteger();

    @Value("${delay}")
    private int delay;

    @Value("${appendAmountOfCallsToResponse:false}")
    private boolean appendAmountOfCallsToResponse;
    @Value("${appendParamToResponse:true}")
    private boolean appendParamToResponse;

    @Value("${response:hello world}")
    private String defaultResponse;


    @GetMapping
    public String get() throws InterruptedException {
        LOGGER.debug("Delaying get request for {}ms", delay);
        Thread.sleep(delay);
        int countForThisCall = getCounter.incrementAndGet();

        var response = defaultResponse;
        if (appendAmountOfCallsToResponse) {
            response += countForThisCall;
        }
        return response;
    }

    @PostMapping
    public String post(@RequestParam String param) throws InterruptedException {
        LOGGER.debug("Delaying post request for {}ms", delay);
        Thread.sleep(delay);
        int countForThisCall = postCounter.incrementAndGet();

        var response = defaultResponse;
        if (appendAmountOfCallsToResponse) {
            response += countForThisCall;
        }
        if (appendParamToResponse) {
            response += param;
        }
        return response;
    }
}
