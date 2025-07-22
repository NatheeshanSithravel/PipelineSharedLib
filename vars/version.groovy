def call(def branchName, def version, def currentBuild) {
    if (branchName == 'production') {
        currentBuild
    } else {
        version
    }
}