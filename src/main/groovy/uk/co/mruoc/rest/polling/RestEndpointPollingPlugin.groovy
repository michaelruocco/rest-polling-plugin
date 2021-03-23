package uk.co.mruoc.rest.polling

import org.gradle.api.Plugin
import org.gradle.api.Project

class RestEndpointPollingPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.task('pollRestGetEndpoint', type: PollRestGetEndpointTask)
    }

}
