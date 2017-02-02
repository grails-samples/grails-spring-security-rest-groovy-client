package grailsclient

import groovy.transform.CompileStatic
import org.grails.springsecurityrest.client.Jwt

@CompileStatic
interface  JwtStorage {

    Jwt getJwt()

    void saveJwt(Jwt jwt)
}
