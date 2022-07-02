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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class Controller {
    //<editor-fold desc="FIELDS">
    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);
    private static final AtomicInteger getCounter = new AtomicInteger();
    private static final AtomicInteger postCounter = new AtomicInteger();

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${delay}")
    private int delay;

    @Value("${appendAmountOfCallsToResponse:false}")
    private boolean appendAmountOfCallsToResponse;
    @Value("${appendParamToResponse:true}")
    private boolean appendParamToResponse;

    @Value("${response:hello world}")
    private String defaultResponse;

    @Value("${forwardCall}")
    private String forwardCall;

    @Value("${responseType:text/plain}")
    private String responseType;
    //</editor-fold>

    @GetMapping
    public ResponseEntity<String> get() throws InterruptedException {

        LOGGER.debug("Delaying get request for {}ms", delay);
        Thread.sleep(delay);

        int countForThisCall = getCounter.incrementAndGet();
        var response = defaultResponse;
        if (forwardCall.isBlank()) {
            if (appendAmountOfCallsToResponse) {
                response += countForThisCall;
            }
        } else {
            final ResponseEntity<String> responseEntity = getCall();
            if (responseEntity.getStatusCode().value() >= 400)
                throw new IllegalArgumentException(String.format("Service call not successful %s", responseEntity.getBody()));

            return responseEntity;
        }

        return wrapResponseWithMediaType(response);
    }

    private ResponseEntity<String> getCall() {

        ResponseEntity<String> responseEntity;
        try {
            LOGGER.debug("Using forwardCalling address {}", forwardCall);
            responseEntity = restTemplate.getForEntity(forwardCall, String.class);
        } catch (RestClientException e) {
            final var targetURL = fixLocalhost();
            LOGGER.debug("Using forwardCalling address {}", targetURL);
            responseEntity = restTemplate.getForEntity(targetURL, String.class);
        }

        return responseEntity;
    }

    private String fixLocalhost() {
        return forwardCall.contains("localhost")
                ? forwardCall.replace("localhost", "host.docker.internal")
                : forwardCall;
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

    //<editor-fold desc="POST Endpoint">
    @PostMapping
    public ResponseEntity<String> post(@RequestParam Map<String, String> param) throws InterruptedException {
        LOGGER.debug("Got params: {}", param);
        LOGGER.debug("Delaying post request for {}ms", delay);
        Thread.sleep(delay);

        int countForThisCall = postCounter.incrementAndGet();
        var response = defaultResponse;
        if (forwardCall.isBlank()) {
            if (appendAmountOfCallsToResponse) {
                response += countForThisCall;
            }
            if (appendParamToResponse) {
                response += param;
            }
        } else {
            final ResponseEntity<String> responseEntity = postCall(param);
            if (responseEntity.getStatusCode().value() >= 400)
                throw new IllegalArgumentException(String.format("Service call not successful %s", responseEntity.getBody()));

            return responseEntity;
        }

        return wrapResponseWithMediaType(response);
    }

    private ResponseEntity<String> postCall(Map<String, String> param) {
        ResponseEntity<String> responseEntity;
        try {
            LOGGER.debug("Using forwardCalling address {}", forwardCall);
            responseEntity = restTemplate.postForEntity(forwardCall, param, String.class);
        } catch (RestClientException e) {
            final var targetURL = fixLocalhost();
            LOGGER.debug("Using forwardCalling address {}", targetURL);
            responseEntity = restTemplate.postForEntity(forwardCall, param, String.class);
        }

        return responseEntity;
    }
    //</editor-fold>
}
