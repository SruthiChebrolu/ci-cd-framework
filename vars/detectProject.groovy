def call() {

    def project = [:]

    if (fileExists('pom.xml')) {

        def pom = readMavenPom file: 'pom.xml'

        project.buildTool = 'maven'

        if (pom.packaging == 'war') {

            project.appType = 'java-war'
            project.artifactType = 'war'
            project.artifactPath = 'target/*.war'

        } else {

            project.appType = 'springboot'
            project.artifactType = 'jar'
            project.artifactPath = 'target/*.jar'
        }

    } else if (fileExists('package.json')) {

        def pkg = readJSON file: 'package.json'

        def deps = [:]

        deps.putAll(pkg.dependencies ?: [:])
        deps.putAll(pkg.devDependencies ?: [:])

        if (deps.containsKey('react')) {

            project.appType = 'react'
            project.buildTool = 'npm'
            project.artifactType = 'zip'
            project.artifactPath = 'react-build.zip'

        } else {

            error('Only React npm project is supported in this demo.')
        }

    } else {

        error('Unsupported project type.')
    }

    echo """
Project Type : ${project.appType}
Build Tool   : ${project.buildTool}
Artifact     : ${project.artifactType}
"""

    return project
}