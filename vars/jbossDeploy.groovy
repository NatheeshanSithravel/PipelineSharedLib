def call(def branch, def pipelineParams, def pom) {
    def configName = pipelineParams.stagingConfigName
    def cluster = pipelineParams.stagingClusterName
    def standalone = 'standalone'
    if (branch == 'production' || branch == 'Production') {
        configName = pipelineParams.prodConfigName
        cluster = pipelineParams.prodClusterName
    } else {
        configName = pipelineParams."${branch}ConfigName"
        cluster = pipelineParams."${branch}ClusterName"
    }
    if (pipelineParams.standalone) {
        standalone = pipelineParams.standalone
    }
    log("JBoss Deployer")
    log("Branch Name : ${branch}")
    log("Configuration Name : ${configName}")
    log("Cluster Name : ${cluster}")
    log("Cluster standalone : ${pipelineParams.standalone}")
    log("Jboss Controller Name : ${jbossController(configName)}")
    log("Jboss CLI : ${jbossPath(configName)}")
    if (jbossValidate(configName, branch)) {
        log("Configuration name [Branch matched] : ${branch} ${configName}")
        if (cluster == '') {
            sshPublisher(publishers: [
            sshPublisherDesc(configName: configName,   
            verbose: true,
                transfers: [
                    sshTransfer(
                        sourceFiles:    "target/${getArtifactName(pom)}",
                        removePrefix:   "target",
                        execCommand:    "mv ${jbossPath(configName)}/${standalone}/deployments/${getArtifactName(pom)} backup/ > /dev/null || : ; " +
                                        "cp /apps/${getArtifactName(pom)} ${jbossPath(configName)}/${standalone}/deployments/ > /dev/null || : ; "  +
                                        "rm /apps/${getArtifactName(pom)} > /dev/null || : ; "
                )
            ]
            )
            ])
        } else{

            if (branch == 'production' || branch == 'Production'){
            sshPublisher(publishers: [
            sshPublisherDesc(configName: configName,  
            verbose: true,
                transfers: [
                    sshTransfer(
                        sourceFiles:    "target/${getArtifactName(pom)}",
                        removePrefix:   "target",
                        execCommand:    "mv ${jbossPath(configName)}/domain/deploy/${cluster}/${getArtifactName(pom)} ${jbossPath(configName)}/domain/deploy/${cluster}/backup/ > /dev/null || : ; " +
                                        "${jbossPath(configName)}/bin/jboss-cli.sh --controller=${jbossController(configName)} --connect --command='undeploy ${getArtifactName(pom)} --server-groups=${cluster}'; " +
                                        "${jbossPath(configName)}/bin/jboss-cli.sh --controller=${jbossController(configName)} --connect --command='deploy /apps/${getArtifactName(pom)} --server-groups=${cluster}'; " +
                                        "cp /apps/${getArtifactName(pom)} ${jbossPath(configName)}/domain/deploy/${cluster}/ > /dev/null || : ; " +
                                        "rm /apps/${getArtifactName(pom)} > /dev/null || : ; "
                )
            ]
            )
            ])
            }
            else {

                sshPublisher(publishers: [
                sshPublisherDesc(configName: configName,  
                verbose: true,
                    transfers: [
                        sshTransfer(
                        sourceFiles:    "target/${getArtifactName(pom)}",
                        removePrefix:   "target",
                        execCommand:    "${jbossPath(configName)}/bin/jboss-cli.sh --controller=${jbossController(configName)} --connect --command='undeploy ${getArtifactName(pom)} --server-groups=${cluster}'; " +
                                        "${jbossPath(configName)}/bin/jboss-cli.sh --controller=${jbossController(configName)} --connect --command='deploy /apps/${getArtifactName(pom)} --server-groups=${cluster}'; " 
                                         
                )
            ]
            )
            ])

            }
        } 
    } else {
        log("Wrong configuration name [Branch Missmatch] : ${branch} ${configName}")
    }
}
