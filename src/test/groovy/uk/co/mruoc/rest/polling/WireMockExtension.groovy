package uk.co.mruoc.rest.polling

import com.github.tomakehurst.wiremock.WireMockServer

class WireMockExtension extends WireMockServer {

    String getBaseUrl() {
        return String.format("http://localhost:%d", port())
    }

}
