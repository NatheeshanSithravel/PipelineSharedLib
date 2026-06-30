def call(def branchName, def user, def pass) {
    if (branchName == 'production') {
        // Using the credentials for Production nodes
        return [
            "-i tcp://${user}:${pass}@${getActiveACENode('192.168.35.12', '192.168.35.13', '4414')} -p 4414",
            "-i tcp://${user}:${pass}@${getActiveACENode('192.168.35.13', '192.168.35.12', '4415')} -p 4415"
        ]
    } else if (branchName == 'staging') {
        log("Branch Name : ${branchName}")
        // Using the credentials for Staging nodes
        return [
            "-i tcp://${user}:${pass}@${getActiveACENode('172.27.31.12', '172.27.31.13', '4414')} -p 4414",
            "-i tcp://${user}:${pass}@${getActiveACENode('172.27.31.13', '172.27.31.12', '4415')} -p 4415"
        ]
    } else if (branchName == 'development') {
        // Using the credentials for the Development node
        return ["-i tcp://${user}:${pass}@172.27.32.36 -p 4414"]      
    } else {
        return ""
    }
}
