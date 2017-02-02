package net.transitionmanager.api.client

import groovy.transform.CompileStatic
import net.transitionmanager.api.client.model.Project
import net.transitionmanager.api.client.model.TDSApiError

@CompileStatic
interface FetchProjectsListener {
    void projectsFetched(List<Project> projects, TDSApiError error)
}
