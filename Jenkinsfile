pipeline {
   agent any
   // 환경변수 등록
   environment {
       PROD_SERVER_JAR_PATH = "/data/application"
       PROD_JENKINS_JAR= "/data/application/jenkins/workspace/SopsApiV4/target/sops-V4-real.jar"
       PROD_JAR_NAME= "sops-V4-real.jar"
       PROD_SERVER_PORT=80

       DEV_SERVER_JAR_PATH= "/data/application/sopsApiV4"
       DEV_JENKINS_JAR= "/data/application/jenkins/workspace/SopsApiV4/target/sops-V4-dev.jar"
       DEV_JAR_NAME= "sops-V4-dev.jar"
       DEV_SERVER_PORT=8132
   }
   tools {
       maven 'Maven 3.8.5'
       jdk 'jdk-1.8'
   }
    stages {
      stage('stage 1'){
        steps {
          echo "Hello World2"
        }
      }
    }
}