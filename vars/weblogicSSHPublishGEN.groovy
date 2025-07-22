def call(def fileName, def configName, def cluster, def pom, def packaging, def filePath) {
    log("Artifact Path : ${filePath}")
    log("Configuration Name : ${configName}")
    log("Cluster Name : ${cluster}")
    log("URL Name : ${getWeblogicURL(configName)}")
    sshPublisher(publishers: [
    sshPublisherDesc(configName: configName,
        transfers: [
            sshTransfer(
                sourceFiles:    "${filePath}/target/${fileName}.${packaging}",
                removePrefix:   "${filePath}/target",
                execCommand:    "export JAVA_HOME=/u01/app/oracle/middleware/java/jdk1.8.0_261 ; " +
                                "export PATH=$JAVA_HOME/bin:$PATH ; " +
                                "export ORACLE_HOME=/u01/app/oracle/middleware/wls ; " +
                                "export DOMAIN_HOME=/u01/app/oracle/middleware/wls/user_projects/domains/adf_domain${getWeblogicADF(configName)} ; " +
                                "export WL_HOME=/u01/app/oracle/middleware/wls/wlserver ; " +
                                "cd \$WL_HOME/server/lib ;" +
                                "/u01/app/oracle/middleware/java/jdk1.8.0_261/bin/java -cp weblogic.jar weblogic.Deployer -adminurl ${getWeblogicURL(configName)} -username jenkindeploy -password jenkindeploy123   -name  ${fileName} -stop -graceful ; " +
                                "mv /u01/uploads/${fileName}.${packaging} /u01/WAR_BKP/`echo ${fileName}.${packaging}`_\$(date +%d-%m-%Y_%H_%M) ;" +
                                "mv /apps/${fileName}.${packaging} /u01/uploads/${fileName}.${packaging} ; " +
                                "/u01/app/oracle/middleware/java/jdk1.8.0_261/bin/java -cp weblogic.jar weblogic.Deployer -adminurl ${getWeblogicURL(configName)} -username jenkindeploy -password jenkindeploy123   -name  ${fileName} -undeploy ; " +
                                "sleep 5 ; "+
                                "/u01/app/oracle/middleware/java/jdk1.8.0_261/bin/java -cp weblogic.jar weblogic.Deployer -adminurl ${getWeblogicURL(configName)} -username jenkindeploy -password jenkindeploy123 -name ${fileName}  -source /u01/uploads/${fileName}.${packaging} -stage -upload -deploy -timeout 300 -targets ${cluster} ; " +
                                "sleep 5 ; "+
                                "/u01/app/oracle/middleware/java/jdk1.8.0_261/bin/java -cp weblogic.jar weblogic.Deployer -adminurl ${getWeblogicURL(configName)} -username jenkindeploy -password jenkindeploy123   -name  ${fileName} -start ; " +
                                " "
        )
    ]
    )
    ])
}