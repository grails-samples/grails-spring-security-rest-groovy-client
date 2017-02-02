package net.transitionmanager.api.client

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import net.transitionmanager.api.client.model.Project
import net.transitionmanager.api.client.model.TDSApiError
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.grails.springsecurityrest.client.AuthenticationRequest
import org.grails.springsecurityrest.client.Jwt
import org.grails.springsecurityrest.client.GrailsSpringSecurityRestClient
import org.grails.springsecurityrest.client.JwtResponseOK
import org.grails.springsecurityrest.client.RefreshRequest

import java.lang.reflect.Type

@CompileStatic
class TDSApi {
    private static final String API_VERSION = '1.0'
    private static final String HTTP_HEADER_ACCEPT_VERSION = 'Accept-Version'
    private static final String HTTP_HEADER_ACCEPT = 'Accept'
    private static final String HTTP_HEADER_ACCEPT_VALUE = 'application/json'
    private static final String HTTP_HEADER_AUTHORIZATION = 'Authorization'
    private static final String HTTP_HEADER_AUTHORIZATION_BEARER = 'Bearer'

    private final OkHttpClient client = new OkHttpClient()

    private final JwtStorage jwtStorage
    private final String serverUrl

    TDSApi(String serverUrl, JwtStorage jwtStorage) {
        this.serverUrl = serverUrl
        this.jwtStorage = jwtStorage
    }

    @SuppressWarnings('Instanceof')
    void authenticate(String username, String password) {
        def client = new GrailsSpringSecurityRestClient()
        def authenticationRequest = new AuthenticationRequest.Builder()
                .serverUrl(serverUrl)
                .username(username)
                .password(password)
                .build()
        def rsp = client.authenticate(authenticationRequest)
        if (rsp instanceof JwtResponseOK) {
            jwtStorage?.saveJwt(((JwtResponseOK) rsp).jwt)
        }
    }

    void fetchProjects(FetchProjectsListener listener) {
        try {
            Response response = executeFetchProjects()
            fetchProjectsResponse(response, listener)

        } catch (IOException e) {
            listener?.projectsFetched(null, TDSApiError.NETWORKING_ERROR)
        }
    }

    private Response executeFetchProjects() throws IOException {
        Request request = new Request.Builder()
                .header(HTTP_HEADER_ACCEPT_VERSION, API_VERSION)
                .header(HTTP_HEADER_ACCEPT, HTTP_HEADER_ACCEPT_VALUE)
                .header(HTTP_HEADER_AUTHORIZATION, authorizationHeaderValue())
                .url("${serverUrl}/api/projects")
                .get()
                .build()
        client.newCall(request).execute()
    }

    @SuppressWarnings('Instanceof')
    private void refreshAccessToken() {
        String refreshToken = jwtStorage?.jwt?.refreshToken
        def refreshRequest = new RefreshRequest.Builder()
                .serverUrl(serverUrl)
                .refreshToken(refreshToken)
                .build()
        def client = new GrailsSpringSecurityRestClient()
        def jwtResponse = client.refreshToken(refreshRequest)
        if ( jwtResponse instanceof JwtResponseOK) {
            Jwt jwt = (jwtResponse as JwtResponseOK).jwt
            jwtStorage?.saveJwt(jwt)
        }
    }

    private String authorizationHeaderValue() {
        "${HTTP_HEADER_AUTHORIZATION_BEARER} ${jwtStorage.jwt.accessToken}"
    }

    private void fetchProjectsResponse(Response response, FetchProjectsListener listener) {
        if ( response.code() == 200 ) {
            processOKProjectsResponse(response, listener)
            return
        }

        if (response.code() == 401) {
            refreshAccessToken()
            Response rsp = executeFetchProjects()
            if (rsp.code() == 200) {
                processOKProjectsResponse(response, listener)
                return
            }
        }

        listener?.projectsFetched(null, TDSApiError.NETWORKING_ERROR)
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    private static void processOKProjectsResponse(Response response, FetchProjectsListener listener) {
        try {

            def jsonString = response.body().string()
            Type listType = new TypeToken<List<Project>>() { }.type
            def gson = new Gson()
            List<Project> projects = gson.fromJson(jsonString, listType)
            listener?.projectsFetched(projects, TDSApiError.NONE)

        } catch (IOException e) {
            listener?.projectsFetched(null, TDSApiError.JSON_PARSING_ERROR)
        }
    }
}
