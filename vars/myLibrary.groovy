// Snyk
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
 
// SonarQube Analysis Function
def runSonarQubeAnalysis(String scannerHome, String folderPath) {
    dir(folderPath) {
        withSonarQubeEnv('sonar') {
            sh "${scannerHome}/bin/sonar-scanner"
        }
    }
}

// Trivy File System Scan
def runTrivyFileSystemScan(String reportName, String folderPath) {
    sh """trivy fs --format template --template "@/usr/local/share/trivy/templates/html.tpl" -o ${reportName} ${folderPath}"""
    archiveArtifacts artifacts: reportName, allowEmptyArchive: true
}

// Trivy Image Scan
def runTrivyImageScan(String imageName, String reportName) {
    sh "trivy clean --all"
    sh """
        trivy image --timeout 10m \
        --format template \
        --template "@/usr/local/share/trivy/templates/html.tpl" \
        --output ${reportName} \
        ${imageName}
    """
    archiveArtifacts artifacts: reportName, allowEmptyArchive: true
}
// DAST Scan
def runDastScan(String domain, String reportPath) {
    echo "Running DAST scan on ${domain}"
    sh "mkdir -p dast-report"
    sh """docker run --rm -v ${reportPath}/dast-report:/tmp/ thien0810/arachni:v1.4-0.5.10 bin/arachni --output-verbose --scope-include-subdomains ${domain} --report-save-path=/tmp/microservice.afr"""
    sh """docker run --rm -v ${reportPath}/dast-report:/tmp/ thien0810/arachni:v1.4-0.5.10 bin/arachni_reporter /tmp/microservice.afr --reporter=html:outfile=/tmp/microservice.html.zip"""
    sh "sudo chown -R jenkins:jenkins dast-report && sudo chmod 777 dast-report"
    sh 'cd dast-report && unzip microservice.html.zip && rm -rf *.zip *.afr '
    archiveArtifacts artifacts: "dast-report/**/*", allowEmptyArchive: true
}
