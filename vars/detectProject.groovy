def call(Map config = [:]) {

    echo '========================================'
    echo ' Detecting Project Type and Build Tool'
    echo '========================================'

    def result = [:]

    /*
     * Maven project
     */
    if (fileExists('pom.xml')) {

        echo 'pom.xml detected.'

        result.buildTool = 'maven'

        /*
         * Determine whether this is Spring Boot
         * or a standard Java Maven application.
         */
        def pomContent = readFile('pom.xml')

        if (pomContent.contains('spring-boot')) {
            result.appType = 'springboot'
            echo 'Application Type : Spring Boot'
        } else {
            result.appType = 'java'
            echo 'Application Type : Java'
        }

        /*
         * Determine artifact type.
         */
        if (pomContent.contains('<packaging>war</packaging>')) {
            result.appType = 'java-war'
            result.artifactType = 'war'
            result.artifactPath = 'target/*.war'
}   else {
     result.artifactType = 'jar'
     result.artifactPath = 'target/*.jar'
}
    }

    /*
     * Gradle project
     */
    else if (
        fileExists('build.gradle') ||
        fileExists('build.gradle.kts')
    ) {

        result.appType = 'java'
        result.buildTool = 'gradle'
        result.artifactType = 'jar'
        result.artifactPath = 'build/libs/*.jar'

        echo 'Gradle project detected.'
    }

    /*
     * Node / React project
     */
    else if (fileExists('package.json')) {

        def packageContent = readFile('package.json')

        result.buildTool = 'npm'

        if (
            packageContent.contains('"react"') ||
            packageContent.contains('"react-dom"')
        ) {
            result.appType = 'react'
            result.artifactType = 'zip'
            result.artifactPath = 'react-build.zip'

            echo 'React application detected.'
        } else {
            result.appType = 'nodejs'
            result.artifactType = 'zip'
            result.artifactPath = 'dist/**'

            echo 'Node.js application detected.'
        }
    }

    /*
     * Python project
     */
    else if (
        fileExists('requirements.txt') ||
        fileExists('pyproject.toml') ||
        fileExists('setup.py')
    ) {

        result.appType = 'python'
        result.buildTool = 'pip'
        result.artifactType = 'python-package'
        result.artifactPath = 'dist/*'

        echo 'Python application detected.'
    }

    /*
     * Unsupported project
     */
    else {

        error '''
Unable to determine project type.

Supported project markers:
- pom.xml
- build.gradle
- build.gradle.kts
- package.json
- requirements.txt
- pyproject.toml
- setup.py
'''
    }

    echo '----------------------------------------'
    echo "Application Type : ${result.appType}"
    echo "Build Tool       : ${result.buildTool}"
    echo "Artifact Type    : ${result.artifactType}"
    echo "Artifact Path    : ${result.artifactPath}"
    echo '----------------------------------------'

    return result
}