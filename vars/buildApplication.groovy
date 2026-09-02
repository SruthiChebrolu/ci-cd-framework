def call(Map project) {

    echo "Building ${project.appType} using ${project.buildTool}"

    if (project.buildTool == 'maven') {

        bat 'mvn clean package'

    } else if (project.buildTool == 'npm') {

        bat 'npm ci'
        bat 'npm run build'

    } else {

        error("Unsupported build tool: ${project.buildTool}")
    }
}