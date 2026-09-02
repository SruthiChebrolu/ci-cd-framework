def call() {

    pipeline {

        agent any

        options {
            timestamps()
            disableConcurrentBuilds()
        }

        stages {

            stage('Checkout') {
                steps {
                    checkout scm
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

            stage('Generate Metadata') {
                steps {
                    script {

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
        }

        post {

            success {
                echo """
====================================
PIPELINE SUCCESS
====================================
Application : ${env.JOB_BASE_NAME}
Type        : ${env.APP_TYPE}
Build Tool  : ${env.BUILD_TOOL}
Artifact    : ${env.ARTIFACT_TYPE}
====================================
"""
            }

            failure {
                echo 'Pipeline failed. Check Console Output.'
            }
        }
    }
}