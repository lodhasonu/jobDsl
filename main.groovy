import groovy.json.JsonSlurper

def services = loadServices()

folder('poc') {
    services.each { service ->
        createPipelineJob(service)
    }
}

def loadServices() {
    def file = new File("aud-dev-1/services.json")
    def json = new JsonSlurper().parse(file)
    return json
}

def createPipelineJob(service) {
    def pipelineScript = loadPipelineScript("pipeline_templates/pipelineScript.groovy")

    pipelineJob("poc/${service.name}") {
        definition {
            cps {
                script(pipelineScript)
            }
        }
    }
}

def loadPipelineScript(String path) {
    return new File(path).text
}


