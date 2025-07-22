def call(def branch) {
    if (branch == "staging") {
        "/apps/openshift/oc"
    } else if (branch == "production") {
        "/apps/openshift/oc"
    } else if (branch == "staging-2") {
        "/apps/openshift/oc"
    }
}