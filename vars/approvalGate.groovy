def call() {

    if (!env.DEPLOY_ENV) {
        echo 'No deployment environment selected.'
        return
    }

    def approvalRequired = false

    switch(env.DEPLOY_ENV) {

        case 'QA':
            approvalRequired = (env.QA_APPROVAL == 'true')
            break

        case 'UAT':
            approvalRequired = (env.UAT_APPROVAL == 'true')
            break

        case 'PROD':
            approvalRequired = (env.PROD_APPROVAL == 'true')
            break

        default:
            approvalRequired = false
    }

    if (!approvalRequired) {
        echo "No approval required for ${env.DEPLOY_ENV}."
        return
    }

    echo """
====================================================
DEPLOYMENT APPROVAL REQUIRED
====================================================

Application : ${env.APP_NAME}
Environment : ${env.DEPLOY_ENV}
Artifact    : ${env.PUBLISHED_ARTIFACT}

====================================================
"""

    input(
        message: "Approve deployment to ${env.DEPLOY_ENV}?",
        ok: 'Deploy'
    )

    echo "Deployment approved for ${env.DEPLOY_ENV}."
}