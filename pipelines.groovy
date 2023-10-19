import groovy.json.JsonSlurper

def services = loadServices()

services.each { service ->
    createPipelineJob(service)
}

def loadServices() {
    // Change this path to wherever your services.json is located.
    def file = new File("aud-dev-1/services.json")  
    def json = new JsonSlurper().parse(file)
    return json
}

def createPipelineJob(service) {
    pipelineJob(service.service_name) {
        definition {
            cpsScm {
                scm {
                    git {
                        remote {
                            url(service.service_repo)
                        }
                        branch('master') 
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
                                    echo "Deploying using ArgoCD file: ${service.argoCDfile}"
                                }
                            }
                        }
                    }
                """)
            }
        }
    }
}
