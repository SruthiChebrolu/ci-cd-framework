def call(Map project) {

    echo """
====================================================
BUILD APPLICATION
====================================================

Application : ${project.appType}
Build Tool  : ${project.buildTool}

====================================================
"""

    switch(project.buildTool) {

        case 'maven':

            bat 'mvn clean package -DskipTests'
            break


        case 'gradle':

            if (fileExists('gradlew.bat')) {
                bat 'gradlew.bat clean build -x test'
            } else {
                bat 'gradle clean build -x test'
            }

            break


        case 'npm':

            bat 'npm ci'
            bat 'npm run build'
            break


        case 'yarn':

            bat 'yarn install --frozen-lockfile'
            bat 'yarn build'
            break


        case 'pip':

            bat 'python -m pip install -r requirements.txt'

            if (fileExists('pyproject.toml')) {
                bat 'python -m build'
            }
            else if (fileExists('setup.py')) {
                bat 'python setup.py sdist bdist_wheel'
            }
            else {
                echo 'Python dependencies installed. No package build file found.'
            }

            break


        default:

            error "Unsupported build tool: ${project.buildTool}"
    }

    echo """
====================================================
BUILD COMPLETED
====================================================

Application : ${project.appType}
Build Tool  : ${project.buildTool}
Status      : SUCCESS

====================================================
"""
}