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
    // Using Jenkins's readFile function to read from the workspace.
    def pipelineScriptContent = readFile "pipelineScript.groovy"
    def parsedPipelineScript = pipelineScriptContent.replace("${service.argocdFile}", service.argocdFile)
    return parsedPipelineScript
}
