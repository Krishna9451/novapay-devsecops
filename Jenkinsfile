pipeline{
   agent any
     stages {
             stage('Checkout'){
                 steps {
                   echo 'Source code checked from Github'
                 }
                      
              }
            
            stage('Maven Test'){
              steps {
                 sh ' ./mvnw test'
              }
            }
      }
}
