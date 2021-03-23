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

@RequiredArgsConstructor
@Slf4j
public class GetExpectedResponse implements Callable<Boolean> {

    private final URI uri;
    private final ExpectedResponse expectedResponse;
    private final HttpClient client;

    public GetExpectedResponse(String uri, ExpectedResponse expectedResponse) {
        this(toUri(uri), expectedResponse, HttpClient.newHttpClient());
    }

    @Override
    public Boolean call() {
        return getExpectedResponse();
    }

    private boolean getExpectedResponse() {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return expectedResponse.isValid(response);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RestPollingException(e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    private static URI toUri(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new RestPollingException(e);
        }
    }

}
