def call(Map project) {

    echo """
====================================================
PUBLISH ARTIFACT
====================================================

Application : ${env.APP_NAME}
Type        : ${project.artifactType}
Build       : ${env.BUILD_NUMBER}

====================================================
"""

    bat 'if not exist release mkdir release'

    if (project.appType == 'react') {

        bat '''
        if exist dist (
            powershell Compress-Archive -Path dist\\* -DestinationPath react-build.zip -Force
        ) else if exist build (
            powershell Compress-Archive -Path build\\* -DestinationPath react-build.zip -Force
        ) else (
            echo React build output not found
            exit /b 1
        )
        '''

        bat """
            copy /Y react-build.zip release\\${env.APP_NAME}-${env.BUILD_NUMBER}.zip
        """

        env.PUBLISHED_ARTIFACT =
            "release\\${env.APP_NAME}-${env.BUILD_NUMBER}.zip"
    }

    else if (project.artifactType == 'war') {

        bat """
            for %%F in (${project.artifactPath}) do (
                copy /Y "%%F" "release\\${env.APP_NAME}-${env.BUILD_NUMBER}.war"
                
            )
            
        """

        env.PUBLISHED_ARTIFACT =
            "release\\${env.APP_NAME}-${env.BUILD_NUMBER}.war"
    }

    else if (project.artifactType == 'jar') {

        bat """
            for %%F in (${project.artifactPath}) do (
                copy /Y "%%F" "release\\${env.APP_NAME}-${env.BUILD_NUMBER}.jar"
                
            )
            
        """

        env.PUBLISHED_ARTIFACT =
            "release\\${env.APP_NAME}-${env.BUILD_NUMBER}.jar"
    }

    else {

        error "Unsupported artifact type: ${project.artifactType}"
    }

    archiveArtifacts(
        artifacts: "${env.PUBLISHED_ARTIFACT},metadata.properties",
        fingerprint: true,
        allowEmptyArchive: false
    )

    echo """
====================================================
ARTIFACT PUBLISHED
====================================================

Artifact : ${env.PUBLISHED_ARTIFACT}
Status   : SUCCESS

====================================================
"""
}