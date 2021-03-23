package uk.co.mruoc.rest.polling

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.catchThrowable
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.UnexpectedBuildFailure
import spock.lang.Specification


class PollRestGetEndpointTaskTest extends Specification {

    private def server
    private def testProjectDir
    private def buildFile

    def setup() {
        server = new WireMockExtension()
        testProjectDir = File.createTempDir()
        testProjectDir.deleteOnExit()
        buildFile = new File(testProjectDir.getPath() + "/build.gradle")
        buildFile.createNewFile()
        println "created file ${buildFile.absolutePath}"
        buildFile << """
            plugins {
                id 'com.github.michaelruocco.rest-polling'
            }
        """
    }

    def cleanup() {
        if (server.isRunning()) {
            server.stop()
        }
    }

    def "should error if expected response not returned within atMost duration"() {
        given:
        buildFile << """
            pollRestGetEndpoint {
                uri = 'http://localhost:8080/my-endpoint'
                expectedStatus = 200
                expectedValues = ['myValue':'expected-value']
                delay = "PT0S"
                interval = "PT0.25S"
                atMost = "PT0.5S"
            }
        """

        when:
        def error = catchThrowable({ ->
            GradleRunner.create()
                    .withProjectDir(testProjectDir)
                    .withArguments('pollRestGetEndpoint', '--info')
                    .withPluginClasspath()
                    .build()
        })

        then:
        assertThat(error)
                .isInstanceOf(UnexpectedBuildFailure.class)
                .hasMessageContaining("Condition uk.co.mruoc.rest.polling.GetExpectedResponse was not fulfilled within 500 milliseconds.")
    }

    def "should throw exception if expected status is not returned"() {
        given:
        server.start()
        server.stubFor(get(urlEqualTo("/my-endpoint"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withBody("{\"myValue\":\"expected-value\"}")))
        def uri = "${server.getBaseUrl()}/my-endpoint"
        buildFile << """
            pollRestGetEndpoint {
                uri = '${uri}'
                expectedStatus = 200
                expectedValues = ['myValue':'expected-value']
                delay = "PT0S"
                interval = "PT0.25S"
                atMost = "PT0.5S"
            }
        """

        when:
        def error = catchThrowable({ ->
            GradleRunner.create()
                    .withProjectDir(testProjectDir)
                    .withArguments('pollRestGetEndpoint', '--info')
                    .withPluginClasspath()
                    .build()
        })

        then:
        assertThat(error)
                .isInstanceOf(UnexpectedBuildFailure.class)
                .hasMessageContaining("Condition uk.co.mruoc.rest.polling.GetExpectedResponse was not fulfilled within 500 milliseconds.")
    }

    def "should throw exception if expected value is not returned"() {
        given:
        server.start()
        server.stubFor(get(urlEqualTo("/my-endpoint"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{\"myValue\":\"expected-value\"}")))
        def uri = "${server.getBaseUrl()}/my-endpoint"
        buildFile << """
            pollRestGetEndpoint {
                uri = '${uri}'
                expectedStatus = 200
                expectedValues = ['myValue':'some-other-value']
                delay = "PT0S"
                interval = "PT0.25S"
                atMost = "PT0.5S"
            }
        """

        when:
        def error = catchThrowable({ ->
            GradleRunner.create()
                    .withProjectDir(testProjectDir)
                    .withArguments('pollRestGetEndpoint', '--info')
                    .withPluginClasspath()
                    .build()
        })

        then:
        assertThat(error)
                .isInstanceOf(UnexpectedBuildFailure.class)
                .hasMessageContaining("Condition uk.co.mruoc.rest.polling.GetExpectedResponse was not fulfilled within 500 milliseconds.")
    }

    def "should not throw exception if expected value is returned"() {
        given:
        server.start()
        server.stubFor(get(urlEqualTo("/my-endpoint"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{\"myValue\":\"expected-value\"}")))
        def uri = "${server.getBaseUrl()}/my-endpoint"
        buildFile << """
            pollRestGetEndpoint {
                uri = '${uri}'
                expectedStatus = 200
                expectedValues = ['myValue':'expected-value']
                delay = "PT0S"
                interval = "PT0.25S"
                atMost = "PT3S"
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir)
                .withArguments('pollRestGetEndpoint', '--info')
                .withPluginClasspath()
                .build()

        then:
        println result.output
        result.task(':pollRestGetEndpoint').outcome == SUCCESS
    }

}
