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
            cpsScm {
                scm {
                    git {
                        remote {
                            url(service.service_repo)
                        }
                        branch('master') 
                    }
                }
                // Pointing to the Jenkinsfile in the service's repo
                scriptPath('Jenkinsfile')
            }
        }
    }
}
