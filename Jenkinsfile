pipeline {
    agent any

    // 파이프라인에서 사용할 환경변수 지정
    environment {

      DEV_JAR_NAME = 'JenkinsTest-dev.jar'
      DEV_SERVER_JAR_PATH = '/home/od'
      DEV_JENKINS_SERVER_JAR = '/var/lib/jenkins/workspace/JenkinsTest_dev/build/libs/JenkinsTest-dev.jar'
      DEV_SERVER_PORT = 8080

      COMMIT_MSG = ""
      CHECK_STATUS_COUNT=20
      SLEEP_SECONDS=5
      TODAY= java.time.LocalDate.now()
    }

    stages {
      stage('[Dev] Build'){
        when {
          branch 'dev'
        }
        steps {
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
                host = "${env.PROD_HOST}"
                username = "${env.PROD_USERNAME}"
                password = "${env.PROD_PASSWORD}"
            }

            def remote = setRemote(host, username, password)

            // sshCommand, sshPut : ssh pipeline steps 플러그인 사용
            // 서버 접근하여 백업파일 생성
            sshCommand remote: remote, command: "cp ${DEV_SERVER_JAR_PATH}/${DEV_JAR_NAME} ${DEV_SERVER_JAR_PATH}/${DEV_JAR_NAME}_${TODAY}.jar"
          }
        }
      }

      stage('[Dev] Deploy'){
        when {
          branch 'dev'
        }
        steps {
          script {
            def remote = setRemote(host, username, password)

            // 중도 계정을 변경하는 경우
            // sshCommand remote: remote, command: "cd ${SERVER_JAR_PATH} && echo '${sweetPassword}' | su sweet -c '${SERVER_JAR_PATH}/service.sh stop'"

            // Jenkins server -> 운영서버 Jar 전송
            sshPut remote: remote, from: env.DEV_JENKINS_SERVER_JAR, into: env.DEV_SERVER_JAR_PATH

            // 기존 서비스 stop
            sshCommand remote: remote, command: "cd ${DEV_SERVER_JAR_PATH} && ./service.sh stop"
            sleep(sleepSeconds)

            // 신규 서비스 start
            sshCommand remote: remote, command: "cd ${DEV_SERVER_JAR_PATH} && ./service.sh start"
            sleep(sleepSeconds)
          }
        }
      }


      stage('[Dev] Service Stop'){
        when {
          branch 'dev'
        }
        steps {
          script {
            def remote = setRemote(host, username, password)
            // && 여러 명령어 연결
            // echo ${password} | sudo -S ./service.sh stop : 명령어에 입력값이 필요한 경유
            sshCommand remote: remote, command: "cd ${DEV_SERVER_JAR_PATH} && ./service.sh stop"
            sleep(sleepSeconds)
           }
        }
      }

      stage('[Dev] Service start'){
        when {
          branch 'dev'
        }
        steps {
          script {
            def remote = setRemote(host, username, password)
            // && 여러 명령어 연결
            // echo ${password} | sudo -S ./service.sh stop : 명령어에 입력값이 필요한 경유
            sshCommand remote: remote, command: "cd ${DEV_SERVER_JAR_PATH} && ./service.sh start"
            sleep(sleepSeconds)
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
