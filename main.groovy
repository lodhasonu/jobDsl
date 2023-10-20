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
    def pipelineScript = loadPipelineScript(service)
    pipelineJob("poc/${service.name}") {
        definition {
            cps {
                script(pipelineScript)
            }
        }
    }
}

def loadPipelineScript(service) {
    // Assuming the pipeline script is named 'pipelineScript.groovy'
    def scriptFile = new File("pipeline_templates/pipelineScript.groovy")
    def pipelineScript = scriptFile.text.replace("${service.argocdFile}", service.argocdFile)
    return pipelineScript
}
