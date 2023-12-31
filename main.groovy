import groovy.json.JsonSlurper

def services = loadServices()

folder('poc') {
    services.each { service ->
        createPipelineJob(service)
    }
}

def loadServices() {
    def jsonUrl = 'https://raw.githubusercontent.com/lodhasonu/jobdsl/master/aud-dev-1/services.json'
    return new JsonSlurper().parseText(new URL(jsonUrl).text)
}

def createPipelineJob(service) {
    final String REPO_BASE_URL = "https://raw.githubusercontent.com"
    final String USERNAME = "lodhasonu"
    final String REPO_NAME = "jobdsl"
    final String BRANCH = "master"

    def scriptUrl = getScriptUrlForService(service, REPO_BASE_URL, USERNAME, REPO_NAME, BRANCH)

    if (!scriptUrl) {
        println "Unknown service type for ${service.name}, skipping job creation."
        return
    }

    def pipelineScript = fetchAndPreparePipelineScript(scriptUrl, service)
    pipelineJob("poc/${service.name}") {
        definition {
            cps {
                script(pipelineScript)
            }
        }
    }
}

def getScriptUrlForService(service, String repoBaseUrl, String username, String repoName, String branch) {
    switch(service.type) {
        case 'go':
            return "${repoBaseUrl}/${username}/${repoName}/${branch}/pipeline_templates/go.groovy"
        case 'java':
            return "${repoBaseUrl}/${username}/${repoName}/${branch}/pipeline_templates/java.groovy"
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
