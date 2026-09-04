def call() {

    if (env.ROLLBACK_ENABLED != 'true') {
        echo 'Rollback is disabled.'
        return
    }

    echo """
====================================================
ROLLBACK APPLICATION
====================================================

Application      : ${env.APP_NAME}
Deployment Type  : ${env.DEPLOYMENT_TYPE}
Environment      : ${env.DEPLOY_ENV}

====================================================
"""

    switch(env.DEPLOYMENT_TYPE) {

        case 'tomcat':
            rollbackTomcat()
            break

        case 'springboot-service':
            rollbackSpringBoot()
            break

        case 'nginx':
            rollbackNginx()
            break

        case 'local-copy':
            echo 'No rollback configured for local-copy.'
            break

        default:
            error "Unsupported rollback deployment type: ${env.DEPLOYMENT_TYPE}"
    }
}


def rollbackTomcat() {

    echo 'Starting Tomcat rollback...'

    bat """
        if not exist "${env.TOMCAT_HOME}\\webapps\\${env.APP_NAME}.war.bak" (
            echo No Tomcat backup found
            exit /b 1
        )

        copy /Y ^
        "${env.TOMCAT_HOME}\\webapps\\${env.APP_NAME}.war.bak" ^
        "${env.TOMCAT_HOME}\\webapps\\${env.APP_NAME}.war"
    """

    echo 'Tomcat rollback completed.'
}


def rollbackSpringBoot() {

    echo 'Starting Spring Boot rollback...'

    bat """
        if not exist "deployment\\app.jar.bak" (
            echo No Spring Boot backup found
            exit /b 1
        )

        powershell -NoProfile -Command "\$process = Get-CimInstance Win32_Process | Where-Object { \$_.Name -eq 'java.exe' -and \$_.CommandLine -like '*deployment\\app.jar*' }; if (\$process) { \$process | ForEach-Object { Stop-Process -Id \$_.ProcessId -Force } }"

        copy /Y ^
        "deployment\\app.jar.bak" ^
        "deployment\\app.jar"

        set JENKINS_NODE_COOKIE=dontKillMe

        powershell -NoProfile -Command "Start-Process -FilePath 'java' -ArgumentList '-jar','deployment\\app.jar' -RedirectStandardOutput 'deployment\\springboot.log' -RedirectStandardError 'deployment\\springboot-error.log' -WindowStyle Hidden"

        powershell -NoProfile -Command "Start-Sleep -Seconds 10"
    """

    echo 'Spring Boot rollback completed.'
}


def rollbackNginx() {

    echo 'Starting static application rollback...'

    bat """
        if not exist "deployment\\static\\website.zip.bak" (
            echo No static backup found
            exit /b 1
        )

        copy /Y ^
        "deployment\\static\\website.zip.bak" ^
        "deployment\\static\\website.zip"
    """

    echo 'Static rollback completed.'
}