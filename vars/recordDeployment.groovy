def call() {

    echo """
====================================================
RECORD DEPLOYMENT
====================================================

Application : ${env.APP_NAME}
Environment : ${env.DEPLOY_ENV}
Artifact    : ${env.PUBLISHED_ARTIFACT}
Status      : ${env.DEPLOYMENT_STATUS}

====================================================
"""

    bat 'if not exist deployment-history mkdir deployment-history'

    def timestamp = new Date().format('yyyyMMdd_HHmmss')

    def record = """
APP_NAME=${env.APP_NAME}
ENVIRONMENT=${env.DEPLOY_ENV}
BUILD_NUMBER=${env.BUILD_NUMBER}
ARTIFACT=${env.PUBLISHED_ARTIFACT}
STATUS=${env.DEPLOYMENT_STATUS}
BRANCH=${env.BRANCH_NAME ?: ''}
COMMIT=${env.GIT_COMMIT ?: ''}
DEPLOYED_AT=${new Date()}
"""

    writeFile(
        file: "deployment-history\\${env.APP_NAME}-${env.DEPLOY_ENV}-${timestamp}.properties",
        text: record
    )

    writeFile(
        file: 'current-deployment.properties',
        text: record
    )

    archiveArtifacts(
        artifacts: 'deployment-history/*.properties,current-deployment.properties',
        fingerprint: false,
        allowEmptyArchive: false
    )

    echo 'Deployment history recorded successfully.'
}