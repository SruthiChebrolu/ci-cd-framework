def call() {

    if (env.HEALTH_CHECK_ENABLED != 'true') {
        echo 'Health check is disabled.'
        return
    }

    echo """
====================================================
HEALTH CHECK
====================================================

Application : ${env.APP_NAME}
Environment : ${env.DEPLOY_ENV}
Type        : ${env.HEALTH_CHECK_TYPE}
URL         : ${env.HEALTH_CHECK_URL}

====================================================
"""

    switch(env.HEALTH_CHECK_TYPE) {

        case 'http':
            httpHealthCheck()
            break

        default:
            error "Unsupported health check type: ${env.HEALTH_CHECK_TYPE}"
    }
}


def httpHealthCheck() {

    if (!env.HEALTH_CHECK_URL) {
        error 'HEALTH_CHECK_URL is not configured.'
    }

    def expectedStatus =
        env.EXPECTED_HTTP_STATUS ?: '200'

    bat """
        powershell -NoProfile -Command ^
        "\$response = Invoke-WebRequest -Uri '${env.HEALTH_CHECK_URL}' -UseBasicParsing; ^
        if (\$response.StatusCode -ne ${expectedStatus}) { ^
            Write-Host 'Health check failed'; ^
            exit 1 ^
        } else { ^
            Write-Host 'Health check passed'; ^
            Write-Host ('HTTP Status: ' + \$response.StatusCode) ^
        }"
    """

    echo """
====================================================
HEALTH CHECK PASSED
====================================================

Application : ${env.APP_NAME}
Status      : HEALTHY

====================================================
"""
}