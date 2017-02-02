package net.transitionmanager.api.client.model

import groovy.transform.CompileStatic

@CompileStatic
enum TDSApiError {
    NONE, UNAUTHORIZED, FORBIDDEN, JSON_PARSING_ERROR, NETWORKING_ERROR
}
