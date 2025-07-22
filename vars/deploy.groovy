def call(def branch, def pipelineParams, def pom) {
    if (pipelineParams.platform == 'openshift') {
        openShiftDeploy(branch, pipelineParams, pom)
    } else if (pipelineParams.platform == 'weblogic') {
        weblogicDeploy(branch, pipelineParams, pom)
    } else if (pipelineParams.platform == 'jar') {
        jarDeploy(branch, pipelineParams, pom)
    }  else {
        jbossDeploy(branch, pipelineParams, pom)
    }

}