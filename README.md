# Groovy Client - Spring Security Rest for Grails
 
This repository contains a Groovy client which shows how to interact with [Spring Security Rest for Grails](http://alvarosanchez.github.io/grails-spring-security-rest/latest/docs/) plugin.

It uses the [JSON Web Token (JWT)](http://alvarosanchez.github.io/grails-spring-security-rest/latest/docs/#_json_web_token) capabilities offered by plugin. 
 

## Code Static Analysis
 
This project uses [Codenarc](http://codenarc.sourceforge.net)

```
./gradlew check
```

## Tests 

```
./gradlew -DGRAILS_SERVER_URL=http://localhost:8080/tdstm 
          -DGRAILS_USERNAME=watson 
          -DGRAILS_PASSWORD=Foobar123! 
          test
```

This tests, after authenticating, calls with a GET request an endpoint named _/api/projects_. It expects a JSON array of projects to be returned. 