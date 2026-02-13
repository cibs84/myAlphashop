// Consente all'utente di selezionare l'ambiente di build
properties([
    parameters([
        choice(
            name: 'BUILD_ENV',
            choices: ['dev', 'prod'],
            description: 'Build environment'
        )
    ])
])

// Nei casi di 'webhook GitHub' l'ambiente di build viene impostato a 'dev'
def buildEnv = params.BUILD_ENV
if (!buildEnv) {
    buildEnv = 'dev'
}
echo "Building environment: ${buildEnv}"


// Dichiarazione variabili globali
def envFilePath = '.env'
def angularImageName = 'node-with-angular'
def distCachedFolder = 'distCachedFolder'
def mavenDkrImage = 'maven:3.9.9-amazoncorretto-21-debian'
def dockerRegistryCredentialsId = 'dockerhub'

// Variabili popolate dal .env
def frontendDkrImage, frontendDkrContext, frontendDockerfile
def articleDkrImage, articleDkrContext, articleDockerfile, articleSvcName, articleSprBtPath
def jwtAuthDkrImage, jwtAuthDkrContext, jwtAuthDockerfile, jwtAuthSvcName, jwtAuthSprBtPath
def userDkrImage, userDkrContext, userDockerfile, userSvcName, userSprBtPath

node {
    stage('Pull repository') {
        checkout scm
    }

    stage('Load and Clean Environment Variables') {
        script {
            if (fileExists(envFilePath)) {
                def content = readFile(envFilePath)
                def props = [:]
                // Parsing manuale Sandbox-friendly (evita RejectedAccessException)
                content.split('\n').each { line ->
                    line = line.trim()
                    if (line && !line.startsWith('#') && line.contains('=')) {
                        def parts = line.split('=', 2)
                        def key = parts[0].trim()
                        def value = parts[1].trim().replaceAll(/^['"]|['"]$/, "")
                        props[key] = value
                    }
                }

                // --- Mapping Variabili ---
                frontendDkrContext = props['NGINX_DKR_CONTEXT']
                frontendDkrImage = props['NGINX_DKR_IMAGE']
                frontendDockerfile = "${frontendDkrContext}/" + props['NGINX_DKR_FILE']

                def baseSprImage = props['SPR_BT__DKR_IMAGE']
                def sprBtDkrFile = props['SPR_BT__DKR_FILE']
                def sprBtContext = props['SPR_BT__DKR_CONTEXT']

                // Article
                articleSvcName = props['ART_MNG__SVC_NAME']
                articleDkrImage = "${baseSprImage}:${articleSvcName}"
                articleDockerfile = sprBtDkrFile
                articleDkrContext = sprBtContext
                articleSprBtPath = 'back_end/articles-web-service'

                // JWT Auth
                jwtAuthSvcName = props['JWT_AUTH__SVC_NAME']
                jwtAuthDkrImage = "${baseSprImage}:${jwtAuthSvcName}"
                jwtAuthDockerfile = sprBtDkrFile
                jwtAuthDkrContext = sprBtContext
                jwtAuthSprBtPath = 'back_end/jwt-auth-service'

                // User Management
                userSvcName = props['USR_MNG__SVC_NAME']
                userDkrImage = "${baseSprImage}:${userSvcName}"
                userDkrContext = sprBtContext
                userDockerfile = sprBtDkrFile
                userSprBtPath = 'back_end/user-management-service'

                echo "Variabili caricate per: ${articleSvcName}, ${jwtAuthSvcName}, ${userSvcName}"
            } else {
                error ".env non trovato!"
            }
        }
    }

    // --- FRONTEND ---
    stage('Build Angular Docker Image') {
        sh "docker build -t ${angularImageName} -f ${frontendDkrContext}/docker/Dockerfile.node-with-angular ${frontendDkrContext}"
        dir(frontendDkrContext) {
            docker.image(angularImageName).inside {
                def ngConfig = buildEnv == 'prod' ? 'production' : 'development'
                sh "npm install"
                sh "ng build --configuration=${ngConfig}"
            }
        }
    }

    stage('Push Frontend Docker Image') {
        script {
            def tag = params.BUILD_ENV
            def customImage = docker.build("${frontendDkrImage}:${tag}", "-f ${frontendDockerfile} ${frontendDkrContext}")

            docker.withRegistry('', dockerRegistryCredentialsId) {
                customImage.push(tag)
            }
        }
    }

    // --- Backend MICROSERVICES (Maven + Docker) ---
    def services = [
        [name: 'Article', path: articleSprBtPath, img: articleDkrImage, svc: articleSvcName],
        [name: 'JWT Auth', path: jwtAuthSprBtPath, img: jwtAuthDkrImage, svc: jwtAuthSvcName],
        [name: 'User', path: userSprBtPath, img: userDkrImage, svc: userSvcName]
    ]

    services.each { service ->
        stage("Build & Push ${service.name}") {
            script {
                // Maven Build
                docker.image(mavenDkrImage).inside("-u root -v $HOME/.m2:/var/maven/.m2") {
                    sh "mvn -B -DskipTests clean package -f ${service.path}"
                }
                
                // Docker Build & Push
                def jarFile = sh(script: "ls ${service.path}/target/*.jar", returnStdout: true).trim()
                def customImage = docker.build("${service.img}", "-f ${articleDockerfile} --build-arg JAR_FILE=${jarFile} ${articleDkrContext}")
                
                docker.withRegistry('',dockerRegistryCredentialsId) {
                    customImage.push(service.svc)
                }
            }
        }
    }
}