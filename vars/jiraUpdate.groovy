def call(def key, def status, def env, def currentBuild) {
    if (key) {
        withEnv(['JIRA_SITE=LOCAL']) {
            def transitionInput =
            [
                transition: [
                    id: status
                ]
            ]
            jiraTransitionIssue idOrKey: key, input: transitionInput
            jiraAddComment idOrKey: key, comment: "Build ${currentBuild.currentResult} \n ============= \n Branch : ${env.BRANCH_NAME} \n Changes: ${env.CHANGE_URL} \n JOB: ${env.BUILD_URL}"
        }
    }
}