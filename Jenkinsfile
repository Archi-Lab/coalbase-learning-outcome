pipeline {
    agent {
        docker {
            image 'jenkinsci/slave:3.27-1-jdk11'
        }
    }
    tools {
        maven "mvn_3_5"
    }

    stages {
        stage('Build') {
            steps {
                echo 'Building.. with ID: ${env.BUILD_ID}'
                updateGitlabCommitStatus name: "Building", state: "running"
                sh "mvn clean package docker:build -Dmaven.test.skip=true"
                sh "docker tag de.th-koeln/coalbase-learning-outcome docker.nexus.archi-lab.io/archilab/coalbase-learning-outcome:${env.BUILD_ID}"
                sh "docker push docker.nexus.archi-lab.io/archilab/coalbase-learning-outcome:${env.BUILD_ID}"
            }
            post {
                success {
                    updateGitlabCommitStatus name: "Building", state: "success"
                }
                failure {
                    updateGitlabCommitStatus name: "Building", state: "failed"
                }
                unstable {
                    updateGitlabCommitStatus name: "Building", state: "success"
                }
            }
        }
        stage('Test') {
            steps {
                updateGitlabCommitStatus name: "Test", state: "running"
                sh "mvn -DargLine=\"-Dspring.profiles.active=test\" test"
            }
            post {
                always { junit "target/surefire-reports/*.xml" }
                success {
                    updateGitlabCommitStatus name: "Test", state: "success"
                }
                failure {
                    updateGitlabCommitStatus name: "Test", state: "failed"
                }
                unstable {
                    updateGitlabCommitStatus name: "Test", state: "success"
                }
            }
        }
        stage("Code Quality Check") {
            steps {
                sh "mvn checkstyle:checkstyle"
                jacoco()
                script { scannerHome = tool "SonarQube Scanner"; }
                withSonarQubeEnv("SonarQube-Server") { sh "${scannerHome}/bin/sonar-scanner" }
            }
            post {
                always {
                    step([$class: "hudson.plugins.checkstyle.CheckStylePublisher", pattern: "**/target/checkstyle-result.xml", unstableTotalAll: "100"])
                }
            }
        }
        stage("Quality Gate") {
            steps {
                script {
                    timeout(time: 10, unit: "MINUTES") {
                        // Just in case something goes wrong, pipeline will be killed after a timeout
                        def qg = waitForQualityGate()
                        // Reuse taskId previously collected by withSonarQubeEnv
                        if (qg.status == "WARN") {
                            currentBuild.result = "UNSTABLE"
                        } else {
                            if (qg.status != "OK") {
                                error "Pipeline aborted due to quality gate failure: ${qg.status}"
                            }
                        }
                    }
                }
            }
        }
        stage('Deploy') {
            steps {
                updateGitlabCommitStatus name: "Deploy", state: "running"
                sh "docker-compose -p coalbase -f src/main/docker/docker-compose.yml -f src/main/docker/docker-compose.prod.yml up -d"
            }
            post {
                success {
                    updateGitlabCommitStatus name: "Deploy", state: "success"
                }
                failure {
                    updateGitlabCommitStatus name: "Deploy", state: "failed"
                }
                unstable {
                    updateGitlabCommitStatus name: "Deploy", state: "success"
                }
            }
        }
    }
}
