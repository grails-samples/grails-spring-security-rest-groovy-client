package grailsclient

import grailsclient.model.Project
import grailsclient.model.TDSApiError
import spock.lang.IgnoreIf
import spock.lang.Specification
import spock.util.concurrent.AsyncConditions

class TDSApiSpec extends Specification {

    def conditions = new AsyncConditions()

    @IgnoreIf({
            !System.getProperty('GRAILS_SERVER_URL') ||
            !System.getProperty('GRAILS_USERNAME') ||
            !System.getProperty('GRAILS_PASSWORD')
    })
    def "fetch projects"() {
        given:
        String serverUrl = System.getProperty('GRAILS_SERVER_URL')
        String username = System.getProperty('GRAILS_USERNAME')
        String password = System.getProperty('GRAILS_PASSWORD')

        when:
        def jwtStorage = new FileJwtStorage('.')
        def tdsApi = new TDSApi(serverUrl, jwtStorage)
        tdsApi.authenticate(username, password)
        tdsApi.fetchProjects { List<Project> projects, TDSApiError error ->
            conditions.evaluate {
                assert projects
                assert projects.size() > 1
                assert error == TDSApiError.NONE
            }
        }

        then:
        conditions.await(2)
    }
}
