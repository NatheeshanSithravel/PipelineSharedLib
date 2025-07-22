def call(def branch, def pipelineParams, def pom) {
    def configName = pipelineParams.stagingConfigName
    def projectName = pipelineParams.projectName
    def cluster = pipelineParams.stagingClusterName
    def configName1 = ''
    def cluster1 = ''
    def configName2 = ''
    def cluster2 = ''
    if (branch == 'production') {
        configName = pipelineParams.prodConfigName
        cluster = pipelineParams.prodClusterName
        configName1 = pipelineParams.prodConfigName1
        cluster1 = pipelineParams.prodClusterName1
        configName2 = pipelineParams.prodConfigName2
        cluster2 = pipelineParams.prodClusterName2
    }else{
        configName1 = pipelineParams.stagingConfigName1
        cluster1 = pipelineParams.stagingClusterName1
        configName2 = pipelineParams.stagingConfigName2
        cluster2 = pipelineParams.stagingClusterName2
    }

    log("Weblogic Deployer")
    log("Branch Name : ${branch}")
    log("Artifact Name : ${projectName}.${pom.packaging}")
    
    weblogicSSHPublish(projectName,configName,cluster,pom)

    if(configName1 != null && cluster1 != null){
        weblogicSSHPublish(projectName,configName1,cluster1,pom)
    }
    if(configName2 != null && cluster2 != null){
        weblogicSSHPublish(projectName,configName2,cluster2,pom)
    }
    
}
