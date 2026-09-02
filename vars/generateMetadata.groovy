def call(Map project) {

    def commitId = bat(
        script: '@git rev-parse --short HEAD',
        returnStdout: true
    ).trim()

    def branch = bat(
        script: '@git rev-parse --abbrev-ref HEAD',
        returnStdout: true
    ).trim()

    def metadata = """
APP_NAME=${env.JOB_BASE_NAME}
APP_TYPE=${project.appType}
BUILD_TOOL=${project.buildTool}
BUILD_NUMBER=${env.BUILD_NUMBER}
COMMIT_ID=${commitId}
BRANCH=${branch}
ARTIFACT_TYPE=${project.artifactType}
"""

    writeFile(
        file: 'metadata.properties',
        text: metadata.trim()
    )

    echo metadata
}