def envFilePath = '.env'

def angularImageName = 'node-with-angular'
def angularDkrContext = 'front_end'
def angularDockerfile = 'front_end/docker/Dockerfile.node-with-angular'

def distCachedFolder = 'distCachedFolder'

def frontendDkrContext = ''
def frontendDkrImage = ''
def frontendDockerfile = ''

def mavenDkrImage = 'maven:3.9.9-amazoncorretto-21-debian'

def articleSvcName = ''
def articleDockerfile = ''
def articleDkrContext = ''
def articleDkrImage = ''
def articleSprBtProfile = 'prod'
def articleSprBtPath = 'back_end/articles-web-service'
def articleSprBtJarFile = ''

def userSvcName = ''
def userDkrContext = ''
def userDockerfile = ''
def userDkrImage = ''
def userSprBtProfile = 'prod'
def userSprBtPath = 'back_end/user-management-service'
def userSprBtJarFile = ''

def dockerRegistryUrl = 'https://registry.hub.docker.com'
def dockerRegistryCredentialsId = 'dockerhub'

catchError {
    node {
        stage('Pull repository') {
            checkout scm
        }
        stage('Load Environment Variables') {
            script {
                def envFile = readFile(envFilePath)
                def envVars = envFile.split('\n').collect { it.trim() }.findAll { it && !it.startsWith('#') }
                envVars.each { envVar ->
                    def (key, value) = envVar.split('=', 2)
                    env[key] = value
                }
                frontendDkrContext = env.NGINX_DKR_CONTEXT
                frontendDkrImage = env.NGINX_DKR_IMAGE
                frontendDockerfile = "${env.NGINX_DKR_CONTEXT}/${env.NGINX_DKR_FILE}"
                articleSvcName = env.ART_MNG__SVC_NAME
                articleDockerfile = env.SPR_BT__DKR_FILE
                articleDkrContext = env.SPR_BT__DKR_CONTEXT
                articleDkrImage = "${env.SPR_BT__DKR_IMAGE}:${env.ART_MNG__SVC_NAME}"
                articleSprBtJarFile = env.ART_MNG__JAR_FILE
                userSvcName = env.USR_MNG__SVC_NAME
                userDkrContext = env.SPR_BT__DKR_CONTEXT
                userDockerfile = env.SPR_BT__DKR_FILE
                userDkrImage = "${env.SPR_BT__DKR_IMAGE}:${env.USR_MNG__SVC_NAME}"
                userSprBtJarFile = env.USR_MNG__JAR_FILE
            }
        }
    }
}

node {
    stage('Build Angular Image') {
        sh "docker build -t ${angularImageName} -f ${angularDockerfile} ${angularDkrContext}"
    }
    stage('Build Angular Project') {
        def customNodeImage = docker.image(angularImageName)
        dir('front_end') {
            customNodeImage.inside {
                stage('Install npm') {
                    sh 'npm install'
                }
                stage('Build') {
                    sh 'ng build'
                }
                stage('Stash dist folder') {
                    stash includes: 'dist/**/*', name: distCachedFolder
                }
            }
        }
    }
    stage('Build and Push Frontend Docker Image') {
        unstash distCachedFolder
        def customImage = docker.build("${frontendDkrImage}", "-f ${frontendDockerfile} ${frontendDkrContext}")
        docker.withRegistry(dockerRegistryUrl, dockerRegistryCredentialsId) {
            customImage.push("$BUILD_NUMBER")
            customImage.push("latest")
        }
    }
}

node {
    stage('Build and Push Article Management Docker Image') {
        docker.image(mavenDkrImage).inside("-u root -v $HOME/.m2:/var/maven/.m2 -e MAVEN_CONFIG=/var/maven/.m2 -e MAVEN_OPTS=\"-Duser.home=/var/maven\"") {
            stage('Create Jenkins User and Group') {
                sh '''
                    useradd jenkins
                    usermod -aG jenkins jenkins
                '''
            }
            stage('Build Article Management') {
                sh "mvn -B -DskipTests clean package -f ${articleSprBtPath} -Dspring.profiles.active=${articleSprBtProfile}"
            }
            stage('Set Jenkins ownership on artifact file') { 
                sh "chown jenkins:jenkins ${articleSprBtPath}/target/*.jar"
            }
            stage('Stash Article Artifact') {
                stash includes: "${articleSprBtPath}/target/*.jar",
                name: 'article-artifact'
            }
        }
        stage('Unstash Article Artifact') {
            unstash 'article-artifact'
        }
        stage('Build and Push Article Docker Image') {
            def jarFile = sh(script: "ls ${articleSprBtPath}/target/*.jar", returnStdout: true).trim()
            def customImage = docker.build(articleDkrImage, "-f ${articleDockerfile} --build-arg JAR_FILE=${jarFile} ${articleDkrContext}")
            docker.withRegistry(dockerRegistryUrl, dockerRegistryCredentialsId) {
                customImage.push(articleSvcName)
            }
        }
    }
}

node {
    stage('Build and Push User Management Docker Image') {
        docker.image(mavenDkrImage).inside("-u root -v $HOME/.m2:/var/maven/.m2 -e MAVEN_CONFIG=/var/maven/.m2 -e MAVEN_OPTS=\"-Duser.home=/var/maven\"") {
            stage('Create Jenkins User and Group') {
                sh '''
                    useradd jenkins
                    usermod -aG jenkins jenkins
                '''
            }
            stage('Build User Management') {
                sh "mvn -B -DskipTests clean package -f ${userSprBtPath} -Dspring.profiles.active=${articleSprBtProfile}"
            }
            stage('Set Jenkins ownership on artifact file') { 
                sh "chown jenkins:jenkins ${userSprBtPath}/target/*.jar"
            }
            stage('Stash User Artifact') {
                stash includes: "${userSprBtPath}/target/*.jar",
                name: 'user-artifact'
            }
        }
        stage('Unstash User Artifact') {
            unstash 'user-artifact'
        }
        stage('Build and Push User Docker Image') {
            def jarFile = sh(script: "ls ${userSprBtPath}/target/*.jar", returnStdout: true).trim()
            def customImage = docker.build(userDkrImage, "-f ${userDockerfile} --build-arg JAR_FILE=${jarFile} ${articleDkrContext}")
            docker.withRegistry(dockerRegistryUrl, dockerRegistryCredentialsId) {
                customImage.push(userSvcName)
            }
        }
    }
}