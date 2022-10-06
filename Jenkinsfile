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
    stage('Compile') {
      steps {
        sh 'go build'
            }
        }
    stage('Test') {
      steps {
        sh 'go test .'
      }
    }
  }
}
