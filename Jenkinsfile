import groovy.transform.Field


pipeline {
    agent any
    environment {
        GIT_URL = 'git@github.com:hoangdat6/PBL4_BE.git'
        SSH_ID_REF = 'GitSSH'
    }

    stages {
        stage('checkout') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: env.SSH_ID_REF, keyFileVariable: 'SSH_KEY')]) {
                    script {
                        // Checkout code từ Git
                        git branch: 'main', credentialsId: null, url: env.GIT_URL,
                                credentials: 'SSH_KEY'
                        // chuyển đến thư mục chứa code
                        dir('PBL4_BE')
                    }
                }
            }
        }
        stage('build') {
            steps {
                script {
                    // Build docker compose
                    sh 'docker compose build'
                }
            }
        }
        stage('deploy') {
            steps {
                script {
                    // Deploy docker compose
                    sh 'docker compose up -d'
                }
            }
        }
    }
}
