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
        sh 'docker build . -t ubuntu-bionic:8082/hello-world:test'
      }
    }
    stage('Push') {
      steps {
        sh 'docker login ubuntu-bionic:8082 -u admin -p admin && docker push ubuntu-bionic:8082/hello-world:test && docker logout'
      }
    }
  }
}