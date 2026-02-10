// Dichiarazione variabili globali
def envFilePath = '.env'
def angularImageName = 'node-with-angular'
def distCachedFolder = 'distCachedFolder'
def mavenDkrImage = 'maven:3.9.9-amazoncorretto-21-debian'
def dockerRegistryUrl = 'https://registry.hub.docker.com'
def dockerRegistryCredentialsId = 'dockerhub'

// Variabili popolate dal .env
def frontendDkrImage, frontendDkrContext, frontendDockerfile
def articleDkrImage, articleDkrContext, articleDockerfile, articleSvcName, articleSprBtPath, articleSprBtProfile
def jwtAuthDkrImage, jwtAuthDkrContext, jwtAuthDockerfile, jwtAuthSvcName, jwtAuthSprBtPath, jwtAuthSprBtProfile
def userDkrImage, userDkrContext, userDockerfile, userSvcName, userSprBtPath, userSprBtProfile

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
                frontendDkrContext = props['NGINX_DKR_CONTEXT'] ?: 'front_end'
                frontendDkrImage = props['NGINX_DKR_IMAGE']
                frontendDockerfile = "${frontendDkrContext}/" + (props['NGINX_DKR_FILE'] ?: 'Dockerfile')
                angularBuildConfiguration = props['NG_BUILD_CONFIG']

                def baseSprImage = props['SPR_BT__DKR_IMAGE']
                def sprBtDkrFile = props['SPR_BT__DKR_FILE']
                def sprBtContext = props['SPR_BT__DKR_CONTEXT']

                // Article
                articleSvcName = props['ART_MNG__SVC_NAME']
                articleDkrImage = "${baseSprImage}:${articleSvcName}"
                articleDockerfile = sprBtDkrFile
                articleDkrContext = sprBtContext
                articleSprBtPath = 'back_end/articles-web-service'
                articleSprBtProfile = props['ART_MNG__SPR_PROFILE']

                // JWT Auth
                jwtAuthSvcName = props['JWT_AUTH__SVC_NAME']
                jwtAuthDkrImage = "${baseSprImage}:${jwtAuthSvcName}"
                jwtAuthDockerfile = sprBtDkrFile
                jwtAuthDkrContext = sprBtContext
                jwtAuthSprBtPath = 'back_end/jwt-auth-service'
                jwtAuthSprBtProfile = props['JWT_AUTH__SPR_PROFILE']

                // User Management
                userSvcName = props['USR_MNG__SVC_NAME']
                userDkrImage = "${baseSprImage}:${userSvcName}"
                userDkrContext = sprBtContext
                userDockerfile = sprBtDkrFile
                userSprBtPath = 'back_end/user-management-service'
                userSprBtProfile = props['USR_MNG__SPR_PROFILE']

                echo "Variabili caricate per: ${articleSvcName}, ${jwtAuthSvcName}, ${userSvcName}"
            } else {
                error ".env non trovato!"
            }
        }
    }

    // --- FRONTEND ---
    stage('Build Angular Image & Project') {
        sh "docker build -t ${angularImageName} -f ${frontendDkrContext}/docker/Dockerfile.node-with-angular ${frontendDkrContext}"
        dir(frontendDkrContext) {
            docker.image(angularImageName).inside {
                sh "npm install && ng build --configuration=${angularBuildConfiguration}"
            }
        }
    }

    stage('Push Frontend Image') {
        script {
            def customImage = docker.build("${frontendDkrImage}", "-f ${frontendDockerfile} ${frontendDkrContext}")
            docker.withRegistry(dockerRegistryUrl, dockerRegistryCredentialsId) {
                customImage.push("latest")
            }
        }
    }

    // --- MICROSERVICES (Maven + Docker) ---
    def services = [
        [name: 'Article', path: articleSprBtPath, img: articleDkrImage, svc: articleSvcName, prof: articleSprBtProfile],
        [name: 'JWT Auth', path: jwtAuthSprBtPath, img: jwtAuthDkrImage, svc: jwtAuthSvcName, prof: jwtAuthSprBtProfile],
        [name: 'User', path: userSprBtPath, img: userDkrImage, svc: userSvcName, prof: userSprBtProfile]
    ]

    services.each { service ->
        stage("Build & Push ${service.name}") {
            script {
                // Maven Build
                docker.image(mavenDkrImage).inside("-u root -v $HOME/.m2:/var/maven/.m2") {
                    sh "mvn -B -DskipTests clean package -f ${service.path} -Dspring.profiles.active=${service.prof}"
                }
                
                // Docker Build & Push
                def jarFile = sh(script: "ls ${service.path}/target/*.jar", returnStdout: true).trim()
                def customImage = docker.build("${service.img}", "-f ${articleDockerfile} --build-arg JAR_FILE=${jarFile} ${articleDkrContext}")
                
                docker.withRegistry(dockerRegistryUrl, dockerRegistryCredentialsId) {
                    customImage.push("latest")
                    customImage.push(service.svc)
                }
            }
        }
    }
}