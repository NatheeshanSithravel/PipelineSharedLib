def call(def branchName) {
    if (branchName == 'production') {
        "releases"
    } else {
        "snapshots"
    }
}