pipeline {
    agent any

    // 파이프라인에서 사용할 환경변수 지정
    environment {
      START_MESSAGE = "Hello Jenkins :)"

      DEV_JAR_NAME = 'JenkinsTest-dev.jar'
      DEV_SERVER_JAR_PATH = '/home/od'
      DEV_JENKINS_SERVER_JAR = '/data/application/JenkinsTest/JenkinsTest-dev.jar'
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
          echo env.START_MESSAGE
          sh './gradlew clean build -Pprofile=dev'

          // build 파일을 jar stash 에 임시저장
          stash(name: 'jar', includes: 'build/*.jar')
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

            // jar stash 파일을 해당 경로로 이동
            dir('/data/application/JenkinsTest') {
                unstash 'jar'
            }

            // sshCommand, sshPut : ssh pipeline steps 플러그인
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
            // Jenkins server -> 운영서버 Jar 전송
            sshPut remote: remote, from: env.DEV_JENKINS_SERVER_JAR, into: env.DEV_SERVER_JAR_PATH
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
            sshCommand remote: remote, command: '''
                cd ${DEV_SERVER_JAR_PATH}
                echo remote.password | sudo ./service.sh stop
              '''

            def isStopped = checkStop(remote, env.DEV_JAR_NAME, 1, env.CHECK_STATUS_COUNT.toInteger(), env.SLEEP_SECONDS)
            if(!isStopped) {
              sh 'exit 1'
            } else {
              echo 'service stop success'
            }
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

def checkStop(remote, jarName, executeCnt, checkCnt, sleepSeconds) {
    if(executeCnt > checkCnt) {
      throw new Exception("Server not Stop - executed over 3 times stop-count script")
    }

    def processInfo = sshCommand remote: remote, command: 'ps -ef | grep' + jarName;
    if(!processInfo.trim()) {
      return true;
    }

    sleep(sleepSeconds)
    return checkStop(remote, jarName, executeCnt + 1, checkCnt, sleepSeconds)
}