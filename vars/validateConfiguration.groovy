def call() {

    echo """
====================================================
VALIDATE CONFIGURATION
====================================================
"""

    if (!env.APP_NAME?.trim()) {
        env.APP_NAME = env.JOB_BASE_NAME
        echo "APP_NAME not configured. Using Jenkins job name: ${env.APP_NAME}"
    }

    def required = [
        APP_NAME: env.APP_NAME,
        APP_TYPE: env.APP_TYPE,
        BUILD_TOOL: env.BUILD_TOOL,
        ARTIFACT_TYPE: env.ARTIFACT_TYPE
    ]
    required.each { key, value ->

        if (!value?.trim()) {

            error(
                "Required configuration '${key}' is missing."
            )
        }
    }

    def buildTools = [
        'auto',
        'maven',
        'gradle',
        'npm',
        'yarn',
        'pip',
        'xcode'
    ]

    if (
        !buildTools.contains(
            env.BUILD_TOOL.toLowerCase()
        )
    ) {

        error(
            "Unsupported build tool: ${env.BUILD_TOOL}"
        )
    }

    def sourceControls = [
        'git',
        'github',
        'gitlab',
        'bitbucket',
        'azure-repos'
    ]

    if (
        !sourceControls.contains(
            env.SOURCE_CONTROL.toLowerCase()
        )
    ) {

        error(
            "Unsupported source control: ${env.SOURCE_CONTROL}"
        )
    }

    echo 'Configuration validation: SUCCESS'
}