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
        
       stage('Maven Package'){
          steps {

           sh './mvnw clean package -DskipTests'

          }

       }
      stage('Docker image build'){

        steps {

          sh '''
            docker build --platform linux/amd64 -t novapay-api:1.4 .
           
            '''
         }

      }
      
      stage ('Check Jenkins Image'){
          steps {

             sh '''
                  echo "===Docker Context==="
                  docker context show
                  
                  echo "===Image==="
                  docker images novapay-api:1.4

                  echo "===trivy verison==="
                  trivy --version
             
                  echo "=== Image Architecture==="
                  docker image inspect novapay-api:1.4 \
                   --format 'OS={{.Os}} Architecture={{.Architecture}}'
               
             '''

       
           }       




      }




      
      stage('Trivy Check'){
        steps{
          sh '''
            trivy image --severity HIGH,CRITICAL --exit-code 1 novapay-api:1.4
          '''
        }
      } 

      
       stage('Check Jenkins Docker') {
       steps {
        sh '''
            echo "DOCKER_HOST=$DOCKER_HOST"
            docker context show
            docker info | grep -E "Server Version|Operating System|Architecture"
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

                        docker push "$DOCKER_USERNAME/novapay-api:1.4"

                docker logout
            '''
            }
        }
      }     

     stage('Deploy to EC2'){
        steps{
          sshagent(['ubuntu']){
 
          sh '''
            ssh -o StrictHostKeyChecking=no ubuntu@13.233.130.91  '
          
            docker pull yadavshrikrishna65/novapay-api:1.4
            docker rm -f novapay-api || true

            docker run -d --name novapay-api --network novapay-network -p 8080:8080  yadavshrikrishna65/novapay-api:1.4 
         '
        '''
      }
    } 
   }   
     stage('Deployed api healthcheck'){
             
       steps {
         sshagent(['ubuntu']){
              sh '''
                ssh -o StrictHostKeyChecking=no ubuntu@13.233.130.91 '
                         echo "Waiting for API to start....."
                      for i in (1..30); do
                          if curl -sf http://localhost:8080/api/health; then 
                                echo "API is healthy"
                                exit 0
                          fi
                      

                         echo "API not ready yet.."
                         sleep 2
                       done
                      
                       echo "API health check failed"
                       docker logs novapay-api
                       exit 1
                    '
                  '''
                      
         }
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
