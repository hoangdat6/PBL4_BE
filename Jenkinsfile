pipeline {
    agent any

    environment {
        GIT_URL = 'git@github.com:hoangdat6/PBL4_BE.git'
    }

    stages {
        stage('Checkout') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'github-credentials-id', keyFileVariable: 'GITHUB_SSH_KEY')]) {
                    sh 'mkdir -p ~/.ssh && ssh-keyscan github.com >> ~/.ssh/known_hosts'
                    git branch: 'main', credentialsId: 'github-credentials-id', url: env.GIT_URL
                }
            }
        }

        stage('Build') {
            steps {
                sh 'docker build -t caroarena:latest .'
            }
        }

        stage('Login to DockerHub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials-id', usernameVariable: 'DOCKERHUB_CREDENTIALS_USR', passwordVariable: 'DOCKERHUB_CREDENTIALS_PSW')]) {
                    sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
                }
            }
        }

        stage('Push to DockerHub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials-id', usernameVariable: 'DOCKERHUB_CREDENTIALS_USR', passwordVariable: 'DOCKERHUB_CREDENTIALS_PSW')]) {
                    sh 'docker tag caroarena:latest $DOCKERHUB_CREDENTIALS_USR/caroarena:1.0.0'
                    sh 'docker push $DOCKERHUB_CREDENTIALS_USR/caroarena:1.0.0'
                }
            }
        }
    }
}
