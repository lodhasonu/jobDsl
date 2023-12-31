mport groovy.json.JsonSlurper

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

def createPipelineJob(service) {
    // Move global variables inside the method
    def repoBaseUrl = "https://raw.githubusercontent.com"
    def username = "lodhasonu"
    def repoName = "jobdsl"
    def branch = "master"

    def scriptUrl
    if (service.type == 'go') {
        scriptUrl = "${repoBaseUrl}/${username}/${repoName}/${branch}/pipeline_templates/go.groovy"
    } else if (service.type == 'java') {
        scriptUrl = "${repoBaseUrl}/${username}/${repoName}/${branch}/pipeline_templates/java.groovy"
    } else {
        println "Unknown service type for ${service.name}, skipping job creation."
        return
    }

    def pipelineTemplate = new URL(scriptUrl).text
    def pipelineScript = pipelineTemplate.replace('${service.name}', service.name)
                                         .replace('${service.service_repo}', service.service_repo)
                                         .replace('${service.argocdFile}', service.argocdFile)

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
