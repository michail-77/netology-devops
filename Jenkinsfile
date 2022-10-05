pipeline {
  agent any
  stages {
    stage('Git') {
      steps {
        git 'https://github.com/michail-77/netology-devops/'
      }
    }

    stage('Test') {
      steps {
        sh '/usr/local/go/bin/go test .'
      }
    }

    stage('Build') {
      steps {
        sh 'go build'
      }
    }
  }
}
