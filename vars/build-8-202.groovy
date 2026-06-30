def call(body) {
    // evaluate the body block, and collect configuration into the object
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    pipeline {
        agent any
        tools {
          maven 'Maven 3.2.5'
          jdk 'JDK-8u202'
         
        }
      	options {
    		buildDiscarder(logRotator(numToKeepStr: '30', artifactNumToKeepStr: '15'))
  		}
        parameters {
            booleanParam(name: 'PRODUCTION_BUILD', defaultValue: false, description: '')
            string(name: 'ISSUE_KEY', defaultValue: '', description: '')
            string(name: 'PASSWORD', defaultValue: '', description: '')
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
         //  	
         //   jdk 'JDK_11'
         // }
          //else{
          	//jdk 'JAVA_HOME'
          //}
                when {
                    expression { (params.PRODUCTION_BUILD == true && params.PASSWORD == 'm0bitel#123' && pipelineParams.platform != 'weblogic') || (params.PRODUCTION_BUILD == true && params.PASSWORD == 'WLpr0d*' && pipelineParams.platform == 'weblogic') || (env.BRANCH_NAME == 'dr' && params.PASSWORD == 'm0bitel#123' && pipelineParams.platform != 'weblogic') || env.BRANCH_NAME != 'production' && env.BRANCH_NAME != 'dr'}
                }
                steps {
                    log("Build")
                    log("*************Ths is a "+getBuildEnv(pipelineParams)+" build ****************")
                    sh "mvn clean install ${getMavenArgs(pipelineParams)} -U -Dmaven.test.skip=true "
					script{
                      
                      if ( getBuildEnv(pipelineParams) == 'grunt'){
                      		sh "grunt war"
                      }
                      else {
                      		sh "mvn clean install ${getMavenArgs(pipelineParams)} -U -Dmaven.test.skip=true "
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
            stage('SonarQube analysis') {
                when {
                    expression { (params.PRODUCTION_BUILD == true && params.PASSWORD == 'm0bitel#123' && pipelineParams.platform != 'weblogic') || (params.PRODUCTION_BUILD == true && params.PASSWORD == 'WLpr0d*' && pipelineParams.platform == 'weblogic') || (env.BRANCH_NAME == 'dr' && params.PASSWORD == 'm0bitel#123' && pipelineParams.platform != 'weblogic') || env.BRANCH_NAME != 'production' && env.BRANCH_NAME != 'dr'}
                }
                steps  {
                    script {
                        scannerHome = tool 'Sonarqube mobitel'
                    }
                    log("SonarQube")
                    withSonarQubeEnv('Sonar server') {
                      // sh "${scannerHome}/bin/sonar-scanner -Dsonar.sources=./src -Dsonar.java.binaries=. -Dsonar.projectKey=${pipelineParams.projectName} -Dsonar.projectName=${pipelineParams.projectName}"
                    }
                }
            }
            stage('Publish') {
                when {
                    expression { (params.PRODUCTION_BUILD == true && params.PASSWORD == 'm0bitel#123' && pipelineParams.platform != 'weblogic') || (params.PRODUCTION_BUILD == true && params.PASSWORD == 'WLpr0d*' && pipelineParams.platform == 'weblogic') || (env.BRANCH_NAME == 'dr' && params.PASSWORD == 'm0bitel#123' && pipelineParams.platform != 'weblogic') || env.BRANCH_NAME != 'production' && env.BRANCH_NAME != 'dr'}
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
                    expression { (params.PRODUCTION_BUILD == true && params.PASSWORD == 'm0bitel#123' && pipelineParams.platform != 'weblogic') || (params.PRODUCTION_BUILD == true && params.PASSWORD == 'WLpr0d*' && pipelineParams.platform == 'weblogic') || (env.BRANCH_NAME == 'dr' && params.PASSWORD == 'm0bitel#123' && pipelineParams.platform != 'weblogic') || env.BRANCH_NAME != 'production' && env.BRANCH_NAME != 'dr'}
                }
                steps {
                    script {
                       if ( pipelineParams.build_env != 'grunt'){
                        pom = readMavenPom file: 'pom.xml'
                       }
                    }
                    deploy(env.BRANCH_NAME, pipelineParams, pom)
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
