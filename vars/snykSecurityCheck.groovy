def call(String serviceDir, String projectName) {
    dir(serviceDir) {
        snykSecurity failOnError: false, 
                      failOnIssues: false, 
                      projectName: projectName, 
                      snykInstallation: 'snyk', 
                      snykTokenId: 'snyk-token', 
                      targetFile: 'package.json'
    }
}
