def call(def branch, def pipelineParams, def pom) {
    def configName = pipelineParams.stagingConfigName
    def projectName = pipelineParams.projectName
    def path = 'MWT_SCHEDULER'
    def args = ''
    if (branch == 'production') {
        configName = pipelineParams.prodConfigName
    }
    if (pipelineParams.path) {
        path = pipelineParams.path
    }
    if (pipelineParams.args) {
        args = pipelineParams.args
    }
    log("Jar Deployer")
    log("Branch Name : ${branch}")
    log("Configuration Name : ${configName}")
    log("Path : ${path}")
    log("Arguments : ${args}")
    log("Artifact Name : ${getArtifactName(pom)}")
    if (jbossValidate(configName, branch)) {
        sshPublisher(publishers: [
        sshPublisherDesc(configName: configName,
            verbose: false,             
            transfers: [
                sshTransfer(
                    sourceFiles:    "target/${getArtifactName(pom)}",
                    removePrefix:   "target",
                    execCommand:    "sh jarrunner.sh ${getArtifactName(pom)} ${path} ${args}"
            )
        ]
        )
        ])
    } else {
        log("Wrong configuration name [Branch Missmatch] : ${branch} ${configName}")
    }    
}
