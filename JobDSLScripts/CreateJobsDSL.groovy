def environments = ['dev', 'qa']

// Loop through each environment
environments.each { env ->
    // Create a folder for each environment
    folder("pulumi/${env}") {
        displayName("Pulumi - ${env}")
        
        // Create a pipeline job for each environment
        job("Pipeline_${env}") {
            displayName("Pipeline - ${env}")
            description("Pipeline for ${env} environment")
            
            // Define the pipeline steps
            steps {
                script {
                    // Read Jenkinsfile content from workspace
                    def jenkinsfileContent = readFileFromWorkspace("Jenkinsfiles/Jenkinsfile_${env}")
                    
                    // Execute Job DSL script to create pipeline job
                    jobDsl {
                        sandbox()
                        script(jenkinsfileContent)
                    }
                }
            }
        }
    }
}
