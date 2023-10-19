def createPipelineJob(service) {
    pipelineJob(service.service_name) {
        definition {
            cpsScm {
                scm {
                    git {
                        remote {
                            url(service.service_repo)
                        }
                        branch('master') // Change branch if needed
                    }
                }
                script("""
                    pipeline {
                        agent any

                        stages {
                            stage('Checkout') {
                                steps {
                                    checkout scm
                                }
                            }
                            
                            stage('Build') {
                                steps {
                                    echo "Building the project..."
                                    // Add build steps here, e.g., 'sh "mvn clean install"'
                                }
                            }
                            
                            stage('Test') {
                                steps {
                                    echo "Running tests..."
                                    // Add test steps here, e.g., 'sh "mvn test"'
                                }
                            }

                            stage('Deploy') {
                                steps {
                                    echo "Deploying using ArgoCD file: ${service.argoCDfile}"
                                    // Deploy logic, e.g., 'sh "./deploy.sh ${service.argoCDfile}"'
                                }
                            }
                        }
                    }
                """)
            }
        }
    }
}

def services = readServicesFromFile('aud-dev-1/service.json')

services.each { service ->
    createPipelineJob(service)
}

def readServicesFromFile(filePath) {
    def fileContent = readFile(filePath)
    return new groovy.json.JsonSlurper().parseText(fileContent)
}
