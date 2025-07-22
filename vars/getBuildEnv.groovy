def call(def pipelineParams) {
    if (pipelineParams.build_env) {
        return pipelineParams.build_env
    } else {
        "Maven"
    }
}
