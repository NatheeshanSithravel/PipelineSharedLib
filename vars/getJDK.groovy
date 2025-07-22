def call(def pipelineParams) {
    if (pipelineParams.jdk == '1.6') {
        'jdk_6'
    } else {
        'JAVA_HOME'
    }

}