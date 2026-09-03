def call(Map project) {

    def commitId = bat(
        script: '@git rev-parse --short HEAD',
        returnStdout: true
    ).trim()

    def branch = bat(
        script: '@git rev-parse --abbrev-ref HEAD',
        returnStdout: true
    ).trim()

    def appName = env.APP_NAME ?: env.JOB_BASE_NAME
    def deployEnv = env.DEPLOY_ENV ?: 'NOT_SELECTED'
    def deploymentStatus = env.DEPLOYMENT_STATUS ?: 'NOT_DEPLOYED'

    def metadata = """
APP_NAME=${appName}
APP_TYPE=${project.appType}
BUILD_TOOL=${project.buildTool}
BUILD_NUMBER=${env.BUILD_NUMBER}
COMMIT_ID=${commitId}
BRANCH=${branch}
ARTIFACT_TYPE=${project.artifactType}
ARTIFACT_PATH=${project.artifactPath}
ENVIRONMENT=${deployEnv}
DEPLOYMENT_STATUS=${deploymentStatus}
"""

    writeFile(
        file: 'metadata.properties',
        text: metadata.trim()
    )

    echo """
====================================================
BUILD METADATA
====================================================
${metadata.trim()}
====================================================
"""
}