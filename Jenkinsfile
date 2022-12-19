pipeline {
  
  agent any

  options {
      skipDefaultCheckout(true)
  }

  environment {
      NEXUS_CREDS = credentials('nexus-publish-credentials')

      SONAR_CREDS = credentials('sonarqube-scanner')
      SONAR_PROJECT_KEY = "ms-impressao"
      SONAR_PROJECT_NAME = "ms-impressao"

      CONTAINER_REGISTRY_REPOSITORY = "ms-impressao"
  }

  stages {

    stage('checkout') {
      steps {
        echo "[${env.BUILD_ID}] Checkout.."

        checkout scm
	      
        sh '''
            chmod +x ./gradlew
         '''
      }
    }

    stage('build') {
      steps {
        echo "[${env.BUILD_ID}] Building.."
        sh './gradlew bootJar -x test -i'
      }
    }

    stage('test') {
      steps {
        echo "[${env.BUILD_ID}] Testing.."
        sh './gradlew test'
      }
    }
    
    stage('[sonar] scan') {
        steps {
            echo "[${env.BUILD_ID}] Scanning sonar.."

            sh "./gradlew sonar \
              -Dsonar.projectKey=$SONAR_PROJECT_KEY \
              -Dsonar.host.url=$SONAR_HOST \
              -Dsonar.login=$SONAR_CREDS_PSW \
              -Dsonar.projectName=$SONAR_PROJECT_NAME \
              -Dsonar.projectVersion=${BUILD_NUMBER}"
         }
    }

    stage('[nexus] publish artifact') {
      steps {
        echo "[${env.BUILD_ID}] Publishing to Nexus.."

        sh "./gradlew publish -q \
            -PnexusHost=$NEXUS_HOST \
            -PnexusPort=$NEXUS_PORT \
            -PnexusRepoUser=$NEXUS_CREDS_USR \
            -PnexusRepoPassword=$NEXUS_CREDS_PSW"
      }
    }


    stage('[docker] build') {
			steps {

        echo "[${env.BUILD_ID}] Docker build..."

      }
    }

    stage('[docker] push image') {
			steps {

        echo "[${env.BUILD_ID}] Push to ACR..."

      }
    }
  }
}
