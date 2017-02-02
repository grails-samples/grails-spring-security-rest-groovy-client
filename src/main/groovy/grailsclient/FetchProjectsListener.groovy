package grailsclient

import groovy.transform.CompileStatic
import grailsclient.model.Project
import grailsclient.model.TDSApiError

@CompileStatic
interface FetchProjectsListener {
    void projectsFetched(List<Project> projects, TDSApiError error)
}
