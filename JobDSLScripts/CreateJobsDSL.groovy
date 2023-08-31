// JobDSLScripts/CreateJobsDSL.groovy

def environments = ['dev', 'qa']

environments.each { env ->
    job("Pipeline_${env}") {
        displayName("Pipeline - ${env}")
        description("Pipeline for ${env} environment")
        
        steps {
            script {
                def jenkinsfileContent = readFileFromWorkspace("Jenkinsfiles/Jenkinsfile_${env}")
                jobDsl {
                    sandbox()
                    script(jenkinsfileContent)
                }
            }
        }
    }
}
