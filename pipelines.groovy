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
    pipelineJob(service.name) {
        definition {
            cps {
                script("""
                    pipeline {
                        agent any
                        stages {
                            stage('Checkout') {
                                steps {
                                    checkout([
                                        $class: 'GitSCM',
                                        branches: [[name: 'master']],
                                        userRemoteConfigs: [[url: '${service.service_repo}']]
                                    ])
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
