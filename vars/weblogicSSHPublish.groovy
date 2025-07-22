def call(def projectName, def configName, def cluster, def pom) {
    log("Configuration Name : ${configName}")
    log("Cluster Name : ${cluster}")
    log("URL Name : ${getWeblogicURL(configName)}")
    sshPublisher(publishers: [
    sshPublisherDesc(configName: configName,
        transfers: [
            sshTransfer(
                sourceFiles:    "target/${getArtifactName(pom)}",
                removePrefix:   "target",
                execCommand:    "export JAVA_HOME=/u01/app/oracle/middleware/java/jdk1.8.0_261 ; " +
                                "export PATH=$JAVA_HOME/bin:$PATH ; " +
                                "export ORACLE_HOME=/u01/app/oracle/middleware/wls ; " +
                                "export DOMAIN_HOME=/u01/app/oracle/middleware/wls/user_projects/domains/adf_domain${getWeblogicADF(configName)} ; " +
                                "export WL_HOME=/u01/app/oracle/middleware/wls/wlserver ; " +
                                "cd \$WL_HOME/server/lib ;" +
                                "/u01/app/oracle/middleware/java/jdk1.8.0_261/bin/java -cp weblogic.jar weblogic.Deployer -adminurl ${getWeblogicURL(configName)} -username jenkindeploy -password jenkindeploy123   -name  ${projectName} -stop -graceful ; " +
                                "mv /u01/uploads/${projectName}.${pom.packaging} /u01/WAR_BKP/`echo ${projectName}.${pom.packaging}`_\$(date +%d-%m-%Y_%H_%M) ;" +
                                "mv /apps/${getArtifactName(pom)} /u01/uploads/${projectName}.${pom.packaging} ; " +
                                "/u01/app/oracle/middleware/java/jdk1.8.0_261/bin/java -cp weblogic.jar weblogic.Deployer -adminurl ${getWeblogicURL(configName)} -username jenkindeploy -password jenkindeploy123   -name  ${projectName} -undeploy ; " +
                                "sleep 5 ; "+
                                "/u01/app/oracle/middleware/java/jdk1.8.0_261/bin/java -cp weblogic.jar weblogic.Deployer -adminurl ${getWeblogicURL(configName)} -username jenkindeploy -password jenkindeploy123 -name ${projectName}  -source /u01/uploads/${projectName}.${pom.packaging}   -stage -upload -deploy -timeout 300 -targets ${cluster} ; " +
                                "sleep 5 ; "+
                                "/u01/app/oracle/middleware/java/jdk1.8.0_261/bin/java -cp weblogic.jar weblogic.Deployer -adminurl ${getWeblogicURL(configName)} -username jenkindeploy -password jenkindeploy123   -name  ${projectName} -start ; " +
                                " "
        )
    ]
    )
    ])
}