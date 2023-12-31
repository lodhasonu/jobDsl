import groovy.json.JsonSlurper

def services = loadServices()

folder('poc') {
    services.each { service ->
        createPipelineJob(service)
    }
}

def loadServices() {
    def jsonUrl = 'https://raw.githubusercontent.com/lodhasonu/jobdsl/master/aud-dev-1/services.json'
    def jsonContent = loadRemoteJson(jsonUrl)
    def json = new JsonSlurper().parseText(jsonContent)
    return json
}

def loadRemoteJson(String url) {
    // This method loads JSON content from a remote URL
    // For public repositories
    return new URL(url).text

    // Add error handling, private repo handling, etc. as needed
}

def createPipelineJob(service) {
    def scriptUrl = 'https://raw.githubusercontent.com/lodhasonu/jobdsl/master/pipeline_templates/pipelineScript.groovy'
    def pipelineTemplate = loadRemoteScript(scriptUrl)

    // Replace placeholders in the pipeline template
    def pipelineScript = pipelineTemplate.replaceAll("\\$\\{service\\.name}", service.name)
                                         .replaceAll("\\$\\{service\\.service_repo}", service.service_repo)
                                         .replaceAll("\\$\\{service\\.argocdFile}", service.argocdFile)

    pipelineJob("poc/${service.name}") {
        definition {
            cps {
                script(pipelineScript)
            }
        }
    }
}

def loadRemoteScript(String url) {
    // This method loads the script content from a remote URL
    // For public repositories
    return new URL(url).text

    // For private repositories, you would need to include authentication,
    // which is more complex and depends on how you handle secrets/authentication.
}
