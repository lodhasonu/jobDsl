
pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                echo "Building the ${service.name} project..."
            }
        }
        stage('Test') {
            steps {
                echo "Running tests ${service.service_repo}..."
            }
        }
        stage('Deploy') {
            steps {
                echo "Deploying using ArgoCD file: ${service.argocdFile}"
            }
        }
    }
}
