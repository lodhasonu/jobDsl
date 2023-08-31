def environments = ['dev', 'qa']

environments.each { env ->
    folder("pulumi/${env}") {
        displayName("Pulumi - ${env}")

        job("Pipeline_${env}") {
            displayName("Pipeline - ${env}")
            description("Pipeline for ${env} environment")

            // Define the pipeline steps directly from Jenkinsfile content
            definition {
                cps {
                    script(readFileFromWorkspace("Jenkinsfiles/Jenkinsfile_${env}"))
                }
            }
        }
    }
}
