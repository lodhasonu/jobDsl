import groovy.json.JsonSlurper

def services = loadServices()

folder('poc') {
    services.each { service ->
        createPipelineJob(service)
    }
}

def loadServices() {
    // Change this path to wherever your services.json is located.
    def file = new File("aud-dev-1/services.json")  
    def json = new JsonSlurper().parse(file)
    return json
}

def createPipelineJob(service) {
pipelineJob("poc/${service.name}") {
    definition {
        cps {
            script("""
pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo "Building the project... ${service.service_repo}"
            }
        }

        stage('Test') {
            steps {
                echo "Running tests... ${service.name}"
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
