package uk.co.mruoc.rest.polling;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpResponse;
import java.util.Collection;

@Builder
@Slf4j
public class ExpectedResponse {

    private final int expectedStatus;
    private final Collection<ExpectedValue> expectedValues;

    public boolean isValid(HttpResponse<String> response) {
        return isStatusValid(response.statusCode()) && isBodyValid(response.body());
    }

    private boolean isStatusValid(int status) {
        boolean valid = expectedStatus == status;
        log.info("status valid {} expected status {} actual status {}", valid, expectedStatus, status);
        return valid;
    }

    private boolean isBodyValid(String body) {
        log.info("response body {}", body);
        return expectedValues.stream().allMatch(expectedValue -> expectedValue.isPresent(body));
    }

}
