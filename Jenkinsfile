pipeline {
  agent any
  tools {
        go 'go1.17.5'
    }
    environment {
        GO111MODULE = 'on'
    }
  stages {
    stage('Git') {
      steps {
        git 'https://github.com/michail-77/netology-devops/'
      }
    }
    stage('Test') {
      steps {
        sh 'go test .'
            }
        }
    stage('Compile') {
      steps {
        sh 'go build'
            }
        }    
#    stage('Nexus') {
#      steps {
#        sh 'curl -v -u admin:admin http://192.168.56.10:8081/repository/repo2/ --upload-file outfile.go'
#      }
#    }
  }
}
