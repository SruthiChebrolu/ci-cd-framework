def call(Map config = [:]) {

    echo """
====================================================
RETRIEVE ARTIFACT
====================================================

Application : ${env.APP_NAME}
Build       : ${env.SOURCE_BUILD_NUMBER}
Environment : ${env.DEPLOY_ENV}

====================================================
"""

    if (!env.SOURCE_BUILD_NUMBER) {
        error 'SOURCE_BUILD_NUMBER is not configured.'
    }

    if (!env.SOURCE_JOB_NAME) {
        env.SOURCE_JOB_NAME = env.JOB_NAME
    }

    step([
        $class: 'CopyArtifact',
        projectName: env.SOURCE_JOB_NAME,
        selector: [
            $class: 'SpecificBuildSelector',
            buildNumber: env.SOURCE_BUILD_NUMBER
        ],
        filter: 'release/**',
        fingerprintArtifacts: true
    ])

    def extension

    switch(env.ARTIFACT_TYPE) {

        case 'war':
            extension = 'war'
            break

        case 'jar':
            extension = 'jar'
            break

        case 'zip':
            extension = 'zip'
            break

        default:
            error "Unsupported artifact type: ${env.ARTIFACT_TYPE}"
    }

    def artifact =
        "release\\${env.APP_NAME}-${env.SOURCE_BUILD_NUMBER}.${extension}"

    if (!fileExists(artifact)) {
        error "Retrieved artifact not found: ${artifact}"
    }

    env.PUBLISHED_ARTIFACT = artifact

    echo """
====================================================
ARTIFACT RETRIEVED
====================================================

Artifact : ${env.PUBLISHED_ARTIFACT}
Status   : SUCCESS

====================================================
"""
}