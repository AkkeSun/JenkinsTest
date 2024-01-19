pipeline {
    agent any

    // 파이프라인에서 사용할 환경변수 지정
    environment {

      DEV_JAR_NAME = 'JenkinsTest-dev.jar'
      DEV_SERVER_JAR_PATH = '/home/od'
      DEV_JENKINS_SERVER_JAR = '/var/lib/jenkins/workspace/JenkinsTest_dev/build/libs/JenkinsTest-dev.jar'
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
          script {
              LAST_COMMIT = sh(returnStdout: true, script: "git log -1 --pretty=%B").trim()
          }

          sh './gradlew clean build -Pprofile=dev'
        }
      }

      stage('[Dev] Deploy'){
        when {
          branch 'dev'
        }
        steps {
          script {

            // ------ use Folder Property plugin
            // Jenkins environment variable setting
            wrap([$class: 'ParentFolderBuildWrapper']) {
                host = "${env.PROD_HOST}"
                username = "${env.PROD_USERNAME}"
                password = "${env.PROD_PASSWORD}"
            }
            def remote = setRemote(host, username, password)

            // ------ use SSH pipeline steps plugin
            // make backup jar
            sshCommand remote: remote, command: "cp ${DEV_SERVER_JAR_PATH}/${DEV_JAR_NAME} ${DEV_SERVER_JAR_PATH}/${DEV_JAR_NAME}_${TODAY}.jar"

            // send jar ()Jenkins server -> service server)
            sshPut remote: remote, from: env.DEV_JENKINS_SERVER_JAR, into: env.DEV_SERVER_JAR_PATH

            // service stop
            sshCommand remote: remote, command: "cd ${DEV_SERVER_JAR_PATH} && ./service.sh stop"
            sleep(2)

            // service start
            sshCommand remote: remote, command: "cd ${DEV_SERVER_JAR_PATH} && ./service.sh start"

            // sshCommand remote: remote, command: "cd ${SERVER_JAR_PATH} && echo '${sweetPassword}' | su sweet -c '${SERVER_JAR_PATH}/service.sh stop'"
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
