def call() {

    echo """
====================================================
LOAD CONFIGURATION
====================================================
"""

    def defaultsText =
        libraryResource('default-config.properties')

    def defaults =
        readProperties text: defaultsText

    def projectConfig = [:]

    if (fileExists('.cicd/project.properties')) {

        projectConfig =
            readProperties file: '.cicd/project.properties'

        echo 'Project configuration found.'

    } else {

        echo 'No .cicd/project.properties found.'
        echo 'Framework defaults will be used.'
    }

    def finalConfig = [:]

    finalConfig.putAll(defaults)
    finalConfig.putAll(projectConfig)

    finalConfig.each { key, value ->

        env."${key}" = value?.toString()
    }

    echo """
Application     : ${env.APP_NAME}
Application Type: ${env.APP_TYPE}
Build Tool      : ${env.BUILD_TOOL}
Artifact Type   : ${env.ARTIFACT_TYPE}
Artifact Path   : ${env.ARTIFACT_PATH}
Sonar Enabled   : ${env.SONAR_ENABLED}
Deployment Type : ${env.DEPLOYMENT_TYPE}
"""

    return finalConfig
}