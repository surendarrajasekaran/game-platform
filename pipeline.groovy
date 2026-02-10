pipeline {
    agent any

    parameters {
        string(name: 'BUNDLE_NAME', defaultValue: '', description: 'Enter folder name from /bundles')
        choice(name: 'ACTION', choices: ['create', 'destroy'], description: 'Action to perform')
        string(name: 'EKS_CLUSTER_NAME', defaultValue: 'your-cluster-name', description: 'Target EKS Cluster Name')
        string(name: 'AWS_REGION', defaultValue: 'us-east-1', description: 'AWS Region')
    }

    environment {
        NAMESPACE = "${params.BUNDLE_NAME}"
        CONFIG_PATH = "./bundles/${params.BUNDLE_NAME}/config.yaml"
        // Use the ID of the AWS credentials stored in Jenkins (Global Credentials)
        AWS_CRED_ID = "aws-credentials-id" 
    }

    stages {
        stage('Authenticate EKS') {
            steps {
                // Use the Jenkins AWS Steps plugin or withCredentials
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding', 
                    credentialsId: "${env.AWS_CRED_ID}",
                    accessKeyVariable: 'AWS_ACCESS_KEY_ID', 
                    secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                ]]) {
                    sh """
                        echo "Authenticating with EKS: ${params.EKS_CLUSTER_NAME} in ${params.AWS_REGION}"
                        aws eks update-kubeconfig --name ${params.EKS_CLUSTER_NAME} --region ${params.AWS_REGION}
                        
                        # Verify connectivity
                        kubectl get nodes
                    """
                }
            }
        }

        stage('Validate Bundle') {
            when { expression { params.BUNDLE_NAME != '' } }
            steps {
                script {
                    def exists = sh(script: "test -f ${CONFIG_PATH} && echo 'exists'", returnStdout: true).trim()
                    if (exists != 'exists') {
                        error "Configuration not found at ${CONFIG_PATH}!"
                    }
                }
            }
        }

        stage('Provision Stack') {
            when { 
                allOf {
                    expression { params.ACTION == 'create' }
                    expression { params.BUNDLE_NAME != '' }
                }
            }
            steps {
                sh """
                    echo "--- Starting Deployment for ${NAMESPACE} ---"
                    chmod +x deploy.sh
                    ./deploy.sh ${CONFIG_PATH}
                """
            }
        }

        stage('Destroy Stack') {
            when { 
                allOf {
                    expression { params.ACTION == 'destroy' }
                    expression { params.BUNDLE_NAME != '' }
                }
            }
            steps {
                sh """
                    echo "--- Destroying Stack for ${NAMESPACE} ---"
                    chmod +x destroy.sh
                    ./destroy.sh ${CONFIG_PATH}
                """
            }
        }
    }

    post {
        success { echo "Successfully ${params.ACTION}ed bundle: ${params.BUNDLE_NAME}" }
        failure { echo "Pipeline failed for ${params.BUNDLE_NAME}." }
    }
}