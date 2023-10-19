import groovy.json.JsonSlurper

def services = loadServices()

services.each { service ->
    createPipelineJob(service)
}

def loadServices() {
    // Replace with the raw URL of services.json from your GitHub repository.
    // Make sure to use the raw content URL.
    def url = "https://github.com/sonulodha/jobDsl/blob/master/aud-dev-1/services.json"
    
    // Fetching the content of services.json using curl.
    def fileContent = sh(script: "curl -s '${url}'", returnStdout: true).trim()
    
    def json = new JsonSlurper().parseText(fileContent)
    return json
}

def createPipelineJob(service) {
    pipelineJob(service.name) {
        definition {
            cpsScm {
                scm {
                    git {
                        remote {
                            url(service.service_repo)
                        }
                        branch('master')  // Adjust branch if necessary
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
                                }
                            }
                            stage('Test') {
                                steps {
                                    echo "Running tests..."
                                }
                            }
                            stage('Deploy') {
                                steps {
                                    echo "Deploying using ArgoCD file: ${service.argocdFile}"
                                }
                            }
                        }
                    }
                """)
            }
        }
    }
}
