package tech.dobler.cmsbenchmarkingapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @Value("${responseType:text/plain}")
    private String responseType;


    @GetMapping
    public ResponseEntity<String> get() throws InterruptedException {
        LOGGER.debug("Delaying get request for {}ms", delay);
        Thread.sleep(delay);
        int countForThisCall = getCounter.incrementAndGet();

        var response = defaultResponse;
        if (appendAmountOfCallsToResponse) {
            response += countForThisCall;
        }

        return wrapResponseWithMediaType(response);
    }

    @PostMapping
    public ResponseEntity<String> post(@RequestParam String param) throws InterruptedException {
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

        return wrapResponseWithMediaType(response);
    }

    private ResponseEntity<String> wrapResponseWithMediaType(String response) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        final MediaType mediaType;
        switch (responseType) {
            case "json":
                mediaType = MediaType.APPLICATION_JSON;
                break;
            case "plain":
            case "text":
                mediaType = MediaType.TEXT_PLAIN;
                break;
            default:
                final var parts = responseType.split("/");
                if (parts.length < 2)
                    throw new IllegalArgumentException(String.format("Unrecognizable MediaType '%s'", responseType));
                mediaType = new MediaType(parts[0], parts[1]);
                break;
        }
        LOGGER.debug("Using mediaType {}", mediaType);
        httpHeaders.setContentType(mediaType);

        return ResponseEntity.ok().headers(httpHeaders).body(response);
    }
}
