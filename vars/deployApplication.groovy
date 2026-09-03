def call(Map config = [:]) {

    if (env.DEPLOYMENT_ENABLED != 'true') {
        echo 'Deployment is disabled.'
        return
    }

    echo """
====================================================
DEPLOY APPLICATION
====================================================

Application      : ${env.APP_NAME}
Deployment Type  : ${env.DEPLOYMENT_TYPE}
Environment      : ${env.DEPLOY_ENV}
Artifact         : ${env.PUBLISHED_ARTIFACT}

====================================================
"""

    switch(env.DEPLOYMENT_TYPE) {

        case 'tomcat':
            deployTomcat()
            break

        case 'springboot-service':
            deploySpringBoot()
            break

        case 'nginx':
            deployNginx()
            break

        case 'local-copy':
            deployLocalCopy()
            break

        case 'none':
            echo 'No deployment configured.'
            break

        default:
            error "Unsupported deployment type: ${env.DEPLOYMENT_TYPE}"
    }
}


def deployTomcat() {

    if (!env.PUBLISHED_ARTIFACT) {
        error 'PUBLISHED_ARTIFACT is not defined.'
    }

    if (!env.TOMCAT_HOME) {
        error 'TOMCAT_HOME is not configured.'
    }

    echo """
Deploying WAR to local Tomcat

Artifact    : ${env.PUBLISHED_ARTIFACT}
Tomcat Home : ${env.TOMCAT_HOME}
"""

    bat """
        if not exist "${env.PUBLISHED_ARTIFACT}" (
            echo Published artifact not found
            exit /b 1
        )

        if not exist "${env.TOMCAT_HOME}\\webapps" (
            echo Tomcat webapps directory not found
            exit /b 1
        )

        if exist "${env.TOMCAT_HOME}\\webapps\\${env.APP_NAME}.war" (
            copy /Y ^
            "${env.TOMCAT_HOME}\\webapps\\${env.APP_NAME}.war" ^
            "${env.TOMCAT_HOME}\\webapps\\${env.APP_NAME}.war.bak"
        )

        copy /Y ^
        "${env.PUBLISHED_ARTIFACT}" ^
        "${env.TOMCAT_HOME}\\webapps\\${env.APP_NAME}.war"
    """

    echo 'Tomcat deployment completed.'
}


def deploySpringBoot() {

    if (!env.PUBLISHED_ARTIFACT) {
        error 'PUBLISHED_ARTIFACT is not defined.'
    }

    echo """
Deploying Spring Boot application

Artifact : ${env.PUBLISHED_ARTIFACT}
"""

    bat """
        if not exist "${env.PUBLISHED_ARTIFACT}" (
            echo Published artifact not found
            exit /b 1
        )

        if not exist "deployment" mkdir deployment

        if exist "deployment\\app.jar" (
            copy /Y ^
            "deployment\\app.jar" ^
            "deployment\\app.jar.bak"
        )

        copy /Y ^
        "${env.PUBLISHED_ARTIFACT}" ^
        "deployment\\app.jar"
    """

    echo 'Spring Boot artifact deployed locally.'
}


def deployNginx() {

    if (!env.PUBLISHED_ARTIFACT) {
        error 'PUBLISHED_ARTIFACT is not defined.'
    }

    echo """
Deploying static application

Artifact : ${env.PUBLISHED_ARTIFACT}
"""

    bat """
        if not exist "${env.PUBLISHED_ARTIFACT}" (
            echo Published artifact not found
            exit /b 1
        )

      if not exist "deployment\\static" mkdir deployment\\static

if exist "deployment\\static\\website.zip" (
    copy /Y ^
    "deployment\\static\\website.zip" ^
    "deployment\\static\\website.zip.bak"
)

copy /Y ^
"${env.PUBLISHED_ARTIFACT}" ^
"deployment\\static\\website.zip"
    """

    echo 'Static deployment completed locally.'
}


def deployLocalCopy() {

    if (!env.PUBLISHED_ARTIFACT) {
        error 'PUBLISHED_ARTIFACT is not defined.'
    }

    bat """
        if not exist "deployment" mkdir deployment

        copy /Y ^
        "${env.PUBLISHED_ARTIFACT}" ^
        "deployment\\"
    """

    echo 'Local copy deployment completed.'
}