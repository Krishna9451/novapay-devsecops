pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                echo 'Source code checked out from GitHub'
            }
        }

        stage('Start PostgreSQL') {
            steps {
                sh '''
                    docker network create novapay-ci-network || true

                    docker rm -f novapay-test-postgres || true

                    docker run -d \
                      --name novapay-test-postgres \
                      --network novapay-ci-network \
                      --network-alias postgres \
                      -e POSTGRES_DB=novapay_db \
                      -e POSTGRES_USER=novapay \
                      -e POSTGRES_PASSWORD=novapay_dev \
                      postgres:16

                    echo "Waiting for PostgreSQL..."

                    until docker exec novapay-test-postgres \
                      pg_isready -U novapay -d novapay_db
                    do
                        sleep 2
                    done
                '''
            }
        }

        stage('Maven Test') {
            steps {
                sh '''
                    docker run --rm \
                      --network novapay-ci-network \
                      -v "$PWD:/workspace" \
                      -w /workspace \
                      maven:3.9-eclipse-temurin-21 \
                      mvn test
                '''
            }
        }
    }

    post {
        always {
            sh '''
                docker rm -f novapay-test-postgres || true
                docker network rm novapay-ci-network || true
            '''
        }
    }
}
