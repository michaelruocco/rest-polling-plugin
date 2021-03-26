package uk.co.mruoc.rest.polling

import static org.awaitility.Awaitility.await

import groovy.util.logging.Slf4j
import java.time.Duration
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

@Slf4j
class PollRestGetEndpointTask extends DefaultTask {

    @Input
    String uri

    @Input
    int expectedStatus = 200

    @Input
    Map<String, String> expectedValues

    @Input
    int minConsecutiveValidRequests = 1

    @Input
    String delay = "PT0S"

    @Input
    String interval = "PT1S"

    @Input
    String atMost = "PT10S"

    PollRestGetEndpointTask() {
        description = 'Polls a rest get endpoint until the expected values are returned in the response'
    }

    @TaskAction
    def run() {
        log.info("polling ${uri}")
        await().pollDelay(Duration.parse(delay))
                .pollInterval(Duration.parse(interval))
                .atMost(Duration.parse(atMost))
                .until(new GetExpectedResponse(uri, minConsecutiveValidRequests, buildExpectedResponse()))
    }

    private ExpectedResponse buildExpectedResponse() {
        return new ExpectedResponse(expectedStatus, buildExpectedValues())
    }

    private Collection<ExpectedValue> buildExpectedValues() {
        return expectedValues.collect{entry -> toExpectedValue(entry)}
    }

    private static ExpectedValue toExpectedValue(Map.Entry<String, String> entry) {
        return new ExpectedJsonValue(entry.getKey(), entry.getValue())
    }

}
