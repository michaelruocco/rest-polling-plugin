package uk.co.mruoc.rest.polling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Slf4j
public class GetExpectedResponse implements Callable<Boolean> {

    private final URI uri;
    private final int minConsecutiveValidRequests;
    private final ExpectedResponse expectedResponse;
    private final HttpClient client;

    private final AtomicInteger consecutiveValidRequests = new AtomicInteger();

    public GetExpectedResponse(String uri, int minConsecutiveValidRequests, ExpectedResponse expectedResponse) {
        this(toUri(uri), minConsecutiveValidRequests, expectedResponse, HttpClient.newHttpClient());
    }

    @Override
    public Boolean call() {
        return getExpectedResponse();
    }

    private boolean getExpectedResponse() {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            boolean valid = expectedResponse.isValid(response);
            return isCompleteSuccessfully(valid);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RestPollingException(e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    private boolean isCompleteSuccessfully(boolean valid) {
        return updateAndGetConsecutiveValidRequests(valid) >= minConsecutiveValidRequests;
    }

    private int updateAndGetConsecutiveValidRequests(boolean valid) {
        if (valid) {
            return consecutiveValidRequests.getAndIncrement();
        }
        consecutiveValidRequests.set(0);
        return consecutiveValidRequests.get();
    }

    private static URI toUri(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new RestPollingException(e);
        }
    }

}
