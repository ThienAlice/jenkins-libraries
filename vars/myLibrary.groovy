def checkStatusSystem(String credential, Boolean isInternal, String internalServer, String remoteScriptPath = "/home/itsj/work/thien/script") {
    try {
        def rawOutput = "" 
        
        if (isInternal) {
            sshagent(credentials: [credential]) {
                rawOutput = sh(
                    script: """
                        cd /bitnami/jenkins/home/.ssh/
                        ssh -o StrictHostKeyChecking=no -F ./config_bk "${internalServer}" '
                            cd /opt/scripts/ && ./check_status.sh > result.txt && cat result.txt
                        '
                    """,
                    returnStdout: true
                )
            }
        } else {
            withCredentials([sshUserPrivateKey(
                credentialsId: credential,
                keyFileVariable: 'SSH_KEY',
                usernameVariable: 'SSH_USER',
                passphraseVariable: 'SSH_PASSPHRASE'
            )]) {
                def remote = [
                    host: internalServer,
                    name: 'remote-host',
                    user: SSH_USER,
                    passphrase: SSH_PASSPHRASE,
                    identityFile: SSH_KEY,
                    allowAnyHosts: true
                ]

                sshCommand remote: remote, command: """
                    cd ${remoteScriptPath}
                    ./check_status.sh > result.txt
                """

                def localResultPath = "/tmp/${env.JOB_NAME}-result.txt"
                sshGet remote: remote, from: "${remoteScriptPath}/result.txt", into: localResultPath, override: true
                rawOutput = readFile(localResultPath)
            }
        }
        def output = rawOutput.trim()
        currentBuild.description = "Status:\n${output}"
        echo "✅ Script output:\n${output}"

    } catch (Exception e) {
        echo "❌ Error during system check: ${e.message}"
        currentBuild.description = "System Check Failed ❌"
        error("Pipeline stopped due to system check failure.")
    }
}
