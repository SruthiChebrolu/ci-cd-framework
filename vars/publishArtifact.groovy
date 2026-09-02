def call(Map project) {

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
    }

    archiveArtifacts(
        artifacts: "${project.artifactPath},metadata.properties",
        fingerprint: true,
        allowEmptyArchive: false
    )

    echo 'Artifact archived successfully.'
}