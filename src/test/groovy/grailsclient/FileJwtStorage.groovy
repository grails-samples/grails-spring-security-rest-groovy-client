package grailsclient

import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.grails.springsecurityrest.client.Jwt
import org.grails.springsecurityrest.client.JwtImpl

class FileJwtStorage implements JwtStorage {

    String path

    FileJwtStorage(String path) {
        this.path = path
    }

    @SuppressWarnings('JavaIoPackageAccess')
    static save(Object content, String filePath) {
        new File(filePath).write(new JsonBuilder(content).toPrettyString())
    }

    @SuppressWarnings('JavaIoPackageAccess')
    static Object load(String filePath) {
        new JsonSlurper().parseText(new File(filePath).text)
    }

    String filePath() {
        "${path}/jwt.json"
    }

    @Override
    Jwt getJwt() {
        def obj = load(filePath())
        def slurper = new JsonSlurper()
        def result = slurper.parseText(obj)

        def jwt = new JwtImpl()
        jwt.with {
            accessToken = result.accessToken
            refreshToken = result.refreshToken
            expiresIn = result.expiresIn
            roles = result.roles as List<String>
            tokenType = result.tokenType
            username = result.username
        }
        jwt
    }

    @Override
    void saveJwt(Jwt jwt) {
        String jsonString = jsonStringWithJwt(jwt)
        save(jsonString, filePath())
    }

    String jsonStringWithJwt(Jwt jwt) {
        def m = [accessToken: jwt.accessToken,
                 refreshToken: jwt.refreshToken,
                 roles: jwt.roles,
                 expiresIn: jwt.expiresIn,
                 tokenType: jwt.tokenType,
                 username: jwt.username]
        JsonOutput.toJson(m)
    }
}
