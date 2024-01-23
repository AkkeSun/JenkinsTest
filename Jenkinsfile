pipeline {
    agent any

    // environment variable setting
    environment {

      DEV_JAR_NAME = 'JenkinsTest-dev.jar'
      DEV_SERVER_JAR_PATH = '/home/od'
      DEV_JENKINS_SERVER_JAR = '/var/lib/jenkins/workspace/JenkinsTest_dev/build/libs/JenkinsTest-dev.jar'

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

      stage ('[Dev] Replace Jar') {
        when {
          branch 'dev'
        }
        steps {
          script {
            // ------ use Folder Property plugin
            // Jenkins environment variable setting
            wrap([$class: 'ParentFolderBuildWrapper']) {
                host = "${env.DEV_HOST}"
                port = "${env.DEV_PORT}"
                username = "${env.DEV_USERNAME}"
                password = "${env.DEV_PASSWORD}"
            }
            def remote = setRemote(host, username, password)

            // ------ use SSH pipeline steps plugin
            // make backup jar
            sshCommand remote: remote, command: "cp ${DEV_SERVER_JAR_PATH}/${DEV_JAR_NAME} ${DEV_SERVER_JAR_PATH}/${DEV_JAR_NAME}_${TODAY}.jar"

            // send jar (Jenkins server -> service server)
            sshPut remote: remote, from: env.DEV_JENKINS_SERVER_JAR, into: env.DEV_SERVER_JAR_PATH
          }
        }
      }

      stage('[Dev] Service stop'){
        when {
          branch 'dev'
        }
        steps {
          script {
            def remote = setRemote(host, username, password)

            sshCommand remote: remote, command: "cd ${DEV_SERVER_JAR_PATH} && ./service.sh stop"
            sleep(2)
            healthCheck(host, port, "stop")

            // sshCommand remote: remote, command: "cd ${SERVER_JAR_PATH} && echo '${sweetPassword}' | su sweet -c '${SERVER_JAR_PATH}/service.sh stop'"
          }
        }
      }

      stage ('[Dev] Service start') {
        when {
          branch 'dev'
        }
        steps {
          script {
            def remote = setRemote(host, username, password)

            sshCommand remote: remote, command: "cd ${DEV_SERVER_JAR_PATH} && ./service.sh start"
            sleep(5)
            healthCheck(host, port, "start")

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

def healthCheck(host, port, type) {
    try {
      def checkResult = sh(script: "curl ${host}:${port}/healthCheck", returnStdout: true)
      echo checkResult

      if(checkResult == "Y") {
        if(type == "stop") {
           echo 'service stop fail'
           sh 'exit 1'
        } else {
           throw new RuntimeException();
        }
      }

    } catch (Exception e) {

      if(type == "start") {
        echo 'service start fail'
        sh 'exit 1'
      }
    }
}
