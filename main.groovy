import groovy.json.JsonSlurper

// Fetch the 'ENV' parameter from Jenkins job parameters
def selectedEnv = binding.variables.get('ENV')

if (!selectedEnv) {
    println "ENV parameter is not provided. Exiting script execution."
    return  // Exit the script if ENV is not provided
}

folder('pipeline') {
    displayName('pipeline-0')
}

folder("pipeline/${selectedEnv}") {
    displayName("${selectedEnv}")
    description('Folder for all audience projects')
}

def services = loadServices(selectedEnv)

services.each { service ->
    createPipelineJob(service, selectedEnv)
}

def loadServices(String env) {
    def jsonUrl = "https://raw.githubusercontent.com/lodhasonu/jobdsl/master/${env}/services.json"
    return new JsonSlurper().parseText(new URL(jsonUrl).text)
}

def createPipelineJob(service, String env) {
    def scriptUrl = getScriptUrlForService(service)

    if (!scriptUrl) {
        println "Unknown service type for ${service.name}, skipping job creation."
        return
    }

    def pipelineScript = fetchAndPreparePipelineScript(scriptUrl, service)
    pipelineJob("pipeline/${env}/${service.name}") {
        definition {
            cps {
                script(pipelineScript)
            }
        }
    }
}

def getScriptUrlForService(service) {
    switch(service.type) {
        case 'go':
            return "https://raw.githubusercontent.com/lodhasonu/jobdsl/master/pipeline_templates/go.groovy"
        case 'java':
            return "https://raw.githubusercontent.com/lodhasonu/jobdsl/master/pipeline_templates/java.groovy"
        case 'python': // Assuming you have a Python template
            return "https://raw.githubusercontent.com/lodhasonu/jobdsl/master/pipeline_templates/python.groovy"
        default:
            return null
    }
}

def fetchAndPreparePipelineScript(String scriptUrl, service) {
    def pipelineTemplate = new URL(scriptUrl).text
    return pipelineTemplate.replace('${service.name}', service.name)
                           .replace('${service.service_repo}', service.service_repo)
                           .replace('${service.argocdFile}', service.argocdFile)
}
