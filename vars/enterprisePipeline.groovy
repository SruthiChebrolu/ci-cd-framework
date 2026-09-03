def call() {

    pipeline {

        agent any

        options {
            timestamps()
            disableConcurrentBuilds()
        }

        parameters {
            choice(
                name: 'DEPLOY_ENV',
                choices: ['DEV', 'SIT', 'QA', 'UAT', 'PROD'],
                description: 'Environment to deploy the application'
            )
        }

        stages {

            stage('Checkout') {
                steps {
                    checkout scm
                }
            }

            stage('Load Configuration') {
                steps {
                    script {
                        loadConfiguration()

                        env.DEPLOY_ENV = params.DEPLOY_ENV
                    }
                }
            }

            stage('Detect Project') {
                steps {
                    script {

                        def project = detectProject()

                        env.APP_TYPE = project.appType
                        env.BUILD_TOOL = project.buildTool
                        env.ARTIFACT_TYPE = project.artifactType
                        env.ARTIFACT_PATH = project.artifactPath
                    }
                }
            }

            stage('Validate Configuration') {
                steps {
                    script {
                        validateConfiguration()
                    }
                }
            }

            stage('Build') {
                steps {
                    script {

                        def project = [
                            appType: env.APP_TYPE,
                            buildTool: env.BUILD_TOOL,
                            artifactType: env.ARTIFACT_TYPE,
                            artifactPath: env.ARTIFACT_PATH
                        ]

                        buildApplication(project)
                    }
                }
            }

            stage('Sonar Analysis') {
                when {
                    expression {
                        return env.SONAR_ENABLED == 'true'
                    }
                }

                steps {
                    script {

                        def project = [
                            appType: env.APP_TYPE,
                            buildTool: env.BUILD_TOOL,
                            artifactType: env.ARTIFACT_TYPE,
                            artifactPath: env.ARTIFACT_PATH
                        ]

                        runSonar(project)
                    }
                }
            }

            stage('Generate Metadata') {
                steps {
                    script {

                        env.DEPLOYMENT_STATUS = 'NOT_DEPLOYED'

                        def project = [
                            appType: env.APP_TYPE,
                            buildTool: env.BUILD_TOOL,
                            artifactType: env.ARTIFACT_TYPE,
                            artifactPath: env.ARTIFACT_PATH
                        ]

                        generateMetadata(project)
                    }
                }
            }

            stage('Publish Artifact') {
                steps {
                    script {

                        def project = [
                            appType: env.APP_TYPE,
                            buildTool: env.BUILD_TOOL,
                            artifactType: env.ARTIFACT_TYPE,
                            artifactPath: env.ARTIFACT_PATH
                        ]

                        publishArtifact(project)
                    }
                }
            }

            stage('Approval') {
                when {
                    expression {
                        return env.DEPLOYMENT_ENABLED == 'true'
                    }
                }

                steps {
                    script {
                        approvalGate()
                    }
                }
            }

stage('Deploy and Validate') {
    when {
        expression {
            return env.DEPLOYMENT_ENABLED == 'true'
        }
    }

    steps {
        script {

            def project = [
                appType: env.APP_TYPE,
                buildTool: env.BUILD_TOOL,
                artifactType: env.ARTIFACT_TYPE,
                artifactPath: env.ARTIFACT_PATH
            ]

            try {

                deployApplication(project)

                env.DEPLOYMENT_STATUS = 'DEPLOYED'

                if (env.HEALTH_CHECK_ENABLED == 'true') {

                    healthCheck()

                    env.DEPLOYMENT_STATUS = 'HEALTHY'

                } else {

                    echo 'Health check is disabled.'
                }

                recordDeployment()

            } catch (Exception e) {

                env.DEPLOYMENT_STATUS = 'FAILED'

                echo """
====================================================
DEPLOYMENT FAILED
====================================================

Application : ${env.APP_NAME}
Environment : ${env.DEPLOY_ENV}

Starting rollback...

====================================================
"""

                if (
                    env.ROLLBACK_ENABLED == 'true' &&
                    env.AUTO_ROLLBACK == 'true'
                ) {

                    rollbackApplication()

                    env.DEPLOYMENT_STATUS = 'ROLLED_BACK'
                }

                error "Deployment failed: ${e.message}"
            }
        }
    }
}
        }

        post {

            success {

                echo """
====================================================
PIPELINE SUCCESS
====================================================

Application : ${env.APP_NAME ?: env.JOB_BASE_NAME}
Type        : ${env.APP_TYPE}
Build Tool  : ${env.BUILD_TOOL}
Artifact    : ${env.ARTIFACT_TYPE}
Published   : ${env.PUBLISHED_ARTIFACT ?: 'N/A'}
Environment : ${env.DEPLOY_ENV}
Status      : ${env.DEPLOYMENT_STATUS ?: 'NOT_DEPLOYED'}

====================================================
"""
            }

            failure {
                echo 'Pipeline failed. Check Console Output.'
            }
        }
    }
}