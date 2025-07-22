def call(def pipelineParams) {
    if (pipelineParams.mvnArgs) {
        return pipelineParams.mvnArgs
    } else {
        ""
    }
}
