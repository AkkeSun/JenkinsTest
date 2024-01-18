pipeline {
    agent any
    // 파이프라인에서 사용할 환경변수 지정
    environment {
      START_MESSAGE = "Hello Jenkins :)"

      DEV_JAR_NAME = 'JenkinsTest-dev.jar'
      DEV_SERVER_JAR_PATH = '/home/od'
      DEV_JENKINS_SERVER_JAR_PATH = '/home/od/jenkins_home/workspace/JenkinsTest_dev/build/libs'
      DEV_SERVER_PORT = 8080

      LAST_COMMIT = ""
      TODAY= java.time.LocalDate.now()
    }

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

      stage('[Dev] Backup Jar'){
        when {
          branch 'dev'
        }
        steps {
          script {
            // Folder Property 플러그인을 이용하여 Jenkins 설정에서 정의한 환경변수 로드
            wrap([$class: 'ParentFolderBuildWrapper']) {
                username = "${env.PROD_USERNAME}"
                password = "${env.PROD_PASSWORD}"
                host = "${env.PROD_HOST}"
            }
            // 서버 접속을 위한 설정
            def remote = setRemote(host01, username, password)

            // 서버 접근하여 백업파일 생성
            sshCommand remote: remote, command: "cp ${DEV_SERVER_JAR_PATH}/${DEV_JAR_NAME} ${DEV_SERVER_JAR_PATH}/${DEV_JAR_NAME}_${TODAY}.jar"
          }
        }
      }
    }
}

def setRemote(host, username, password) {
    def remote = [:]
    remote.name = host
    remote.host = host
    remote.port = 22
    remote.allowAnyHosts = true
    remote.user = username
    remote.password = password

    return remote
}