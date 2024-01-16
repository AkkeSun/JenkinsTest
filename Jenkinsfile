pipeline {
    agent any
    // 파이프라인에서 사용할 환경변수 지정
    environment {
      START_MESSAGE = "Hello Jenkins :)"

      DEV_SERVER_JAR_PATH = ''
      DEV_JENKINS_JAR = ''
      DEV_JAR_NAME = ''
      DEV_SERVER_PORT = 9092

      LAST_COMMIT = ""
    }

    // 파이프라인 시작
    stages {

      stage('[Dev] Build'){
        when {
          branch 'dev'
        }
        steps {
          echo START_MESSAGE
          sh './gradlew clean build -Pprofile=dev'
        }
      }

    }
}