pipeline {
    agent any

    environment {
        MAVEN_HOME = tool 'Maven' // Tu dois avoir Maven configuré dans Jenkins
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'nom_prenom_emargement', url: 'https://github.com/tonuser/nom_prenom_emargement.git'
            }
        }

        stage('Install Dependencies') {
            steps {
                sh "${MAVEN_HOME}/bin/mvn clean install -DskipTests"
            }
        }

        stage('Package Jar') {
            steps {
                sh "${MAVEN_HOME}/bin/mvn package -DskipTests"
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t emargement-app .'
            }
        }

        stage('Run Docker Container (optionnel)') {
            steps {
                sh 'docker run -d -p 8080:8080 --name emargement emargement-app'
            }
        }
    }
}
