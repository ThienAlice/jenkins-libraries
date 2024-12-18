def getProjectName(String branchName) {
    if (branchName.startsWith("feature/")) {
        return branchName - "feature/"
    }
    return branchName
}
def snykSecurityCheck(String serviceDir, String projectName) {
    dir(serviceDir) {
        snykSecurity failOnError: false, 
                      failOnIssues: false, 
                      projectName: projectName, 
                      snykInstallation: 'snyk', 
                      snykTokenId: 'snyk-token', 
                      targetFile: 'package.json'
    }
}

