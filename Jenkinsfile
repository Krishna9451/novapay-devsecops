pipeline {
    agent any

    environment {
      DOCKER_IMAGE= 'yadavshrikrishna65/novapay-api:1.4-arm64'
    }


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
                  mkdir -p "$PWD/.m2"
                    docker run --rm \
                      --network novapay-ci-network \
                      --user "$(id -u):$(id -g)" \
                     -v "$PWD/.m2:/tmp/maven-repo"\
                      -v "$PWD:/workspace" \
                      -w /workspace \
                      maven:3.9-eclipse-temurin-21 \
                      mvn -Dmaven.repo.local=/tmp/maven-repo test
                '''
            }
        }

       stage('Maven Package'){
          steps {

           sh './mvnw clean package -DskipTests'

          }

       }
      stage('Docker image build'){

        steps {

          sh '''
            docker build --no-cache --platform linux/arm64 -t novapay-api:1.4-arm64 .

            '''
         }

      }

     stage('Trivy Check'){
        steps{
          sh '''
            trivy image --config /dev/null --scanners vuln --timeout 15m \
           --ignorefile /dev/null --severity HIGH,CRITICAL --exit-code 1 novapay-api:1.4-arm64
          '''
        }
      }



     stage('Docker Push') {
      steps {
        withCredentials([usernamePassword(
            credentialsId: 'dockerhub-credentials',
            usernameVariable: 'DOCKER_USERNAME',
            passwordVariable: 'DOCKER_PASSWORD'
        )]) {
            sh '''
                echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin

                echo "Starting Docker Push..."

                        docker push "$DOCKER_USERNAME/novapay-api:1.4-arm64"

                docker logout
            '''
            }
        }
      }
    stage('Deploy to Kubernetes'){
        steps {
        sh '''
          kubectl --kubeconfig=/var/lib/jenkins/.kube/config apply -f novapay-k8s/
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
