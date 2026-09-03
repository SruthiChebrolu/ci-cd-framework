def call(Map project = [:]) {

    if (env.SONAR_ENABLED != 'true') {
        echo 'Sonar analysis is disabled.'
        return
    }

    echo """
====================================================
SONAR ANALYSIS
====================================================

Application : ${env.APP_NAME}
Type        : ${env.APP_TYPE}
Build Tool  : ${env.BUILD_TOOL}

====================================================
"""

    withSonarQubeEnv('SonarQube') {

        switch(env.BUILD_TOOL) {

            case 'maven':

                bat """
                    mvn sonar:sonar ^
                    -Dsonar.projectKey=${env.SONAR_PROJECT_KEY} ^
                    -Dsonar.projectName="${env.SONAR_PROJECT_NAME ?: env.APP_NAME}"
                """

                break


            case 'gradle':

                bat """
                    gradlew sonar ^
                    -Dsonar.projectKey=${env.SONAR_PROJECT_KEY}
                """

                break


            case 'npm':

                bat """
                    sonar-scanner ^
                    -Dsonar.projectKey=${env.SONAR_PROJECT_KEY} ^
                    -Dsonar.projectName="${env.SONAR_PROJECT_NAME ?: env.APP_NAME}" ^
                    -Dsonar.sources=${env.SONAR_SOURCES ?: '.'}
                """

                break


            default:

                error "Sonar is not configured for build tool: ${env.BUILD_TOOL}"
        }
    }

    echo """
====================================================
SONAR ANALYSIS COMPLETED
====================================================

Application : ${env.APP_NAME}
Status      : SUCCESS

====================================================
"""
}