def call(body) {
    // evaluate the body block, and collect configuration into the object
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()
    def CodeScan = pipelineParams.CodeScan ?: false

    pipeline {
        agent any
      //  tools {
      //    maven 'Maven 3.9.9'
      //    jdk 'JDK_21'
         
     //   }
        environment {
        NEXUS_URL = "http://192.168.56.103:8081"
        REPO = "raw-war-backup"
        // CREDS = "admin:admin"
		CREDS = credentials('nexus-cred')
        }
      	options {
    		buildDiscarder(logRotator(numToKeepStr: '30', artifactNumToKeepStr: '15'))
  		}
        parameters {
            booleanParam(name: 'PRODUCTION_BUILD', defaultValue: false, description: '')
            string(name: 'ISSUE_KEY', defaultValue: '', description: '')
            string(name: 'PASSWORD', defaultValue: '', description: '')
			booleanParam(name: 'ROLLBACK', defaultValue: false, description: 'Enable rollback from Nexus')
            string(name: 'ROLLBACK_FILE', defaultValue: '', description: 'Enter WAR file name (e.g., api-tracker-2026-04-21_10-30-00.war)')
            //password(name: 'PASSWORD', defaultValue: '', description: '')
        }
        stages {
            stage('Checkout') {
                steps {
                    log("checkout... ${params.ISSUE_KEY}")
                }
            }
            stage('Build') {
              
               //def configName = pipelineParams.stagingConfigName
    	  //def cluster = pipelineParams.stagingClusterName
    	  //def standalone = 'standalone'
    	  //if (branch == 'production' || branch == 'Production') {
        	//configName = pipelineParams.prodConfigName
        	//cluster = pipelineParams.prodClusterName
    	 //} else {
        //	configName = pipelineParams."${branch}ConfigName"
        //	cluster = pipelineParams."${branch}ClusterName"
        //  }
        //  if ( configName == 'ipgtestNew.mobitel.lk-DR'){
           	
         //   jdk 'JDK_11'
         // }
          //else{
          	//jdk 'JAVA_HOME'
          //}
                when {
                    expression { !params.ROLLBACK && ( 
						         (params.PRODUCTION_BUILD == true && params.PASSWORD == 'm0bitel#123' && pipelineParams.platform != 'weblogic') ||
						         (params.PRODUCTION_BUILD == true && params.PASSWORD == 'WLpr0d*' && pipelineParams.platform == 'weblogic') ||
						         (env.BRANCH_NAME == 'dr' && params.PASSWORD == 'm0bitel#123' && pipelineParams.platform != 'weblogic') ||
						          env.BRANCH_NAME != 'production' && env.BRANCH_NAME != 'dr'  && env.BRANCH_NAME != 'production2' )
						        }
                }
                steps {
                    log("Build")
                    log("*************Ths is a "+getBuildEnv(pipelineParams)+" build ****************")
                    //sh "mvn clean install ${getMavenArgs(pipelineParams)} -U -Dmaven.test.skip=true "
					script{
                      
                      if ( getBuildEnv(pipelineParams) == 'grunt'){
                      		sh "grunt war"
                      }
                      else {
                      		//sh "mvn clean install ${getMavenArgs(pipelineParams)} -U -Dmaven.test.skip=true clean install -X" 
                            sh "mvn -Dmaven.test.skip=true clean install -X"
                      }
                    
					}
					
                }
            }
            stage('Test') {
                 when {
                    expression { pipelineParams.test == true && params.PRODUCTION_BUILD == false }
                }
                steps {
                    sh 'mvn test'
                }
                post {
                    always {
                        junit 'target/surefire-reports/*.xml'
                    }
                }
            }
          
          
            stage('Code Quality analysis') {
                 when {
                     allOf {
                         expression { false }
                         anyOf {
                             branch 'staging'
                             branch 'staging2'
                             branch 'production'
                        }
                     }
               }
                  steps {
                      script {
                          def scannerHome = tool 'sonar-scanner'
                          def sonarServer = ''

                          if (env.BRANCH_NAME == 'staging' || env.BRANCH_NAME == 'staging2') {
                               sonarServer = 'Sonar server'
                          } else if (env.BRANCH_NAME == 'production') {
                              sonarServer = 'sonar-server-prod'
                          }

                          withSonarQubeEnv(sonarServer) {
                              sh "${scannerHome}/bin/sonar-scanner -Dsonar.sources=. -Dsonar.java.binaries=. -Dsonar.projectKey=${pipelineParams.projectName} -Dsonar.projectName=${pipelineParams.projectName}-JBoss"
                          }
                      }
                  }
               }

               stage('Upload with Timestamp') {
				   when {
    expression { !params.ROLLBACK }
}
    steps {
        script {
            def pom = readMavenPom file: 'pom.xml'
            def file = "target/${getArtifactName(pom)}"

            sh """
                set -e

                BASE_URL=$NEXUS_URL/repository/$REPO/${pipelineParams.projectName}

                TIMESTAMP=\$(date +"%Y-%m-%d_%H-%M-%S")
                FILE_NAME="${pom.artifactId}-\$TIMESTAMP.war"

                echo "Uploading \$FILE_NAME"

                curl -s -u $CREDS \
                    --upload-file ${file} \
                    "\$BASE_URL/\$FILE_NAME"
            """
        }
    }
}
          
          
      /*       stage('Quality Gate') {
                when {
                    expression { CodeScan == true }
                    }
                agent any
                steps {
                  script {
                      try {
                // Retry if SonarQube response is slow
                retry(2) {
                    timeout(time: 30, unit: 'SECONDS') {
                        def qg = waitForQualityGate abortPipeline: true
                        echo "Quality Gate status: ${qg.status}"
                    }
                }
                  } catch (err) {

                    echo "Quality Gate stage failed: ${err}"

                     def sonarURL = "http://codescanner-stg.mobitel.lk/dashboard?id=${pipelineParams.projectName}"

                // Run node block for workspace operations
                    node {

                    // Checkout repository
                    checkout scm

                    // Get committer email
                    env.COMMITTER_EMAIL = sh(
                        script: "git log -1 --pretty=format:'%ce'",
                        returnStdout: true
                    ).trim()

                    echo "Committer Email: ${env.COMMITTER_EMAIL}"

                    // Send failure notification
                    mail to: "${env.COMMITTER_EMAIL}",
                         subject: "Jenkins Pipeline Alert: Code Quality Gate Failed",
                         body: """Hello Team,

THE CODE QUALITY GATE HAS FAILED

Project: ${env.JOB_NAME}
Build URL: ${env.BUILD_URL}
CodeScanner URL: ${sonarURL}

Please review the CodeScanner Dashboard for details.
"""
                }

                currentBuild.result = 'FAILURE'
                error "Stopping pipeline due to Quality Gate failure"
            }
        }
    }
}         */ 

            stage('Download Rollback Artifact') {
    when {
        expression { params.ROLLBACK }
    }
    steps {
        script {
            sh """
                set -e

                BASE_URL=${NEXUS_URL}/repository/${REPO}/${pipelineParams.projectName}

                echo "Downloading ${params.ROLLBACK_FILE}"

                curl -f -u ${CREDS} \
                    -o rollback.war \
                    "\${BASE_URL}/${params.ROLLBACK_FILE}"

                ls -lh rollback.war
            """
        }
    }
}
          
            stage('Publish') {
                when {
                    expression { (params.PRODUCTION_BUILD == true && params.PASSWORD == 'm0bitel#123' && pipelineParams.platform != 'weblogic') ||
						        (params.PRODUCTION_BUILD == true && params.PASSWORD == 'WLpr0d*' && pipelineParams.platform == 'weblogic') ||
						        (env.BRANCH_NAME == 'dr' && params.PASSWORD == 'm0bitel#123' && pipelineParams.platform != 'weblogic') ||
						        env.BRANCH_NAME != 'production' && env.BRANCH_NAME != 'dr' && env.BRANCH_NAME != 'production2'  }
                }
                steps {
                    script {
                      if ( pipelineParams.build_env != 'grunt'){
                        pom = readMavenPom file: 'pom.xml'
                      }
                    }
                    log("Publish")
                   // sh "mvn deploy:deploy-file -DrepositoryId=${nexusRepo(env.BRANCH_NAME)} -DgroupId=${pom.groupId} -DartifactId=${pom.artifactId} -Dversion=${version(env.BRANCH_NAME, pom.version, currentBuild.number)} -DgeneratePom=true -Dpackaging=${pom.packaging} -Durl=http://192.168.1.18:8081/nexus/content/repositories/${nexusRepo(env.BRANCH_NAME)} -Dfile=target/${getArtifactName(pom)}"
                }
            }
            stage('Deploy') {
    when {
        expression {
            (params.PRODUCTION_BUILD == true && params.PASSWORD == 'm0bitel#123' && pipelineParams.platform != 'weblogic') ||
            (params.PRODUCTION_BUILD == true && params.PASSWORD == 'WLpr0d*' && pipelineParams.platform == 'weblogic') ||
            (env.BRANCH_NAME == 'dr' && params.PASSWORD == 'm0bitel#123' && pipelineParams.platform != 'weblogic') ||
            (env.BRANCH_NAME != 'production' &&
             env.BRANCH_NAME != 'dr' &&
             env.BRANCH_NAME != 'production2')
        }
    }

    steps {
        script {

            if (params.ROLLBACK) {
                echo "Deploying rollback artifact..."

                def warName = sh(
                    script: '''
                        mvn help:evaluate \
                          -Dexpression=project.build.finalName \
                          -q \
                          -DforceStdout
                    ''',
                    returnStdout: true
                ).trim()

                echo "Final WAR Name: ${warName}.war"

                sh """
                    mkdir -p target
                    cp rollback.war target/${warName}.war
                """

                deploy(pipelineParams.deployEnv ?: env.BRANCH_NAME, pipelineParams, null)

            } else {

                def pom = null

                if (pipelineParams.build_env != 'grunt') {
                    pom = readMavenPom file: 'pom.xml'
                }

                deploy(pipelineParams.deployEnv ?: env.BRANCH_NAME, pipelineParams, pom)
            }
        }
    }
}
}
        }

        post {
            failure {
                jiraUpdate(params.ISSUE_KEY, 31, env, currentBuild)
            }
            success {
                jiraUpdate(params.ISSUE_KEY, 21, env, currentBuild)
            }
        }
    }
}
