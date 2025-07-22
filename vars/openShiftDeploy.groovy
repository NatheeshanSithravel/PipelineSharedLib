def call(def branch, def pipelineParams, def pom) {
    def exposeURL = pipelineParams.stagingExposeURL
    if (branch == 'production') {
        exposeURL = pipelineParams.prodExposeURL
    }
    def oc = openshift(branch)
    
    log("Openshift Deployer")
    log("Branch Name : ${branch}")
    log("Project Name : ${pipelineParams.projectName}")
    log("Application Name : ${pipelineParams.appname}")
    log("Image Name : ${pipelineParams.image}")
    log("Expose URL : ${exposeURL}")
    log("OC CLI : ${oc}")

    sh "rm -rf oc-build"
    sh "mkdir -p oc-build/deployments"
    sh "cp -rf .openshift/* oc-build/ > /dev/null || :"
    sh "cp target/${getArtifactName(pom)} oc-build/deployments/${getArtifactName(pom)}"
    sh "${oc} login ${openshiftURL(branch)} --insecure-skip-tls-verify  -u ${env.OC_USERNAME} -p ${env.OC_PASSWORD} -n ${pipelineParams.projectName}"
    sh "${oc} delete dc,bc,svc -l app=${pipelineParams.appname} -n ${pipelineParams.projectName}"
    sh "${oc} new-build --name=${pipelineParams.appname} --image-stream=${pipelineParams.image} --binary=true --labels=app=${branch} -n ${pipelineParams.projectName} || true"
    sh "${oc} start-build ${pipelineParams.appname} --from-dir=oc-build --wait=true -n ${pipelineParams.projectName}"
    sh "${oc} new-app ${pipelineParams.appname}:latest -n ${pipelineParams.projectName}"
    // sh "${oc} expose svc/${pipelineParams.appname} --hostname=${exposeURL} -n ${pipelineParams.projectName}"
    sh "${oc} logout"
}