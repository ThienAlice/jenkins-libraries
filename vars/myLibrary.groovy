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
def typeService(String service) {
    def typeBuild
    if (service == 'feature/order-service' || service == 'feature/store-front') {
        typeBuild = "js"
    } else if (service == 'feature/product-service') {
        typeBuild = "go"
    } else {
        error "No build strategy defined for branch: ${service}"
    }
    return typeBuild
}

