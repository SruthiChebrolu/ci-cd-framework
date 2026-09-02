def call() {

    echo """
====================================================
SOURCE CHECKOUT
====================================================
SCM Provider : ${env.SOURCE_CONTROL}
Branch       : ${env.BRANCH}
====================================================
"""

    /*
     * For normal Pipeline Script from SCM jobs,
     * Jenkins already knows repository + credentials.
     */

    if (!env.REPOSITORY_URL?.trim()) {

        echo 'Using SCM configuration from Jenkins job.'

        checkout scm

        return
    }

    def remote = [
        url: env.REPOSITORY_URL
    ]

    if (env.CREDENTIALS_ID?.trim()) {

        remote.credentialsId =
            env.CREDENTIALS_ID
    }

    checkout([
        $class: 'GitSCM',

        branches: [[
            name: "*/${env.BRANCH}"
        ]],

        userRemoteConfigs: [
            remote
        ]
    ])
}