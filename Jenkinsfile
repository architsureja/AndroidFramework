#!/usr/bin/env groovy

def checkout() {
    stage('Checkout') {
        deleteDir()
        checkout scm
    }
}

def getTypeofBranch() {
    if (env.BRANCH_NAME == "develop") {
        return "develop"
    } else if (env.BRANCH_NAME ==~ /(.*(PR-).*)/) {
        if (env.CHANGE_TARGET ==~ /(.*(codeFreeze).*)/) {
            return "hotfix"
        }
        return "PR"
    } else if (env.BRANCH_NAME.startsWith("codeFreeze/")) {
        return "codeFreeze"
    } else if (env.BRANCH_NAME.startsWith("UAT/")) {
        return "UAT"
    } else if (env.BRANCH_NAME.startsWith("release/")) {
        return "release"
    } else {
        return env.BRANCH_NAME
    }
}

def getFlavour() {
    switch (getTypeofBranch()) {
        case 'codeFreeze':
        case 'hotfix':
            return "sitMob"
        case 'UAT':
            return "uatMob"
        case 'release':
            return "prodMob"
        default:
            return "devMob"
    }
}

node('master') {
    try {
        checkout()
        switch (getTypeofBranch()) {
            case 'develop':
                developPipeline()
                break
            case 'PR':
                prPipeline()
                break
            case 'codeFreeze':
            case 'hotfix':
            case 'UAT':
            case 'release':
                ReleasingPipeline()
                break;
            default:
                otherBranchPipeline()
                break;
        }
    } catch (Exception e) {
        def errorMessage = "Pipeline build ${BUILD_NUMBER} for branch ${BRANCH_NAME} failed with an error: ${e.message}"
        echo errorMessage
        throw e
    } finally {
        clean()
    }
}

def developPipeline() {
    jacocoTest()
    testReportArchiving()
    developSonarStage()
    sonarQualityGate()
    buildStage()
    buildArchiving()
}

def prPipeline() {
    jacocoTest()
    testReportArchiving()
    prSonarStage()
    buildStage()
    buildArchiving()
}

def ReleasingPipeline() {
    env.VERSION_STRING = env.BRANCH_NAME.substring(env.BRANCH_NAME.lastIndexOf('/') + 1, env.BRANCH_NAME.length())
    echo "VERSION_STRING = ${env.VERSION_STRING}"
    jacocoTest()
    testReportArchiving()
    buildStage()
    buildArchiving()
    uploadBuildToAppCenter()
}

def otherBranchPipeline() {
    jacocoTest()
    testReportArchiving()
    buildStage()
    buildArchiving()
}

def jacocoTest() {
    stage('Test') {
        switch (getFlavour()) {
            case "sitMob":
                sh "./gradlew jacocoOfflineSitMobReleaseTestReport jacocoSitMobReleaseTestReport"
                break
            case "uatMob":
                sh "./gradlew jacocoOfflineUatMobReleaseTestReport jacocoUatMobReleaseTestReport"
                break
            case "prodMob":
                sh "./gradlew jacocoOfflineProdMobReleaseTestReport jacocoProdMobReleaseTestReport"
                break
            default:
                sh "./gradlew jacocoOfflineDevMobDebugTestReport jacocoDevMobDebugTestReport"
        }
    }
}

def testReportArchiving() {
    stage('Jacoco report archiving') {
        // Zip complete jacoco report and attach it to jenkins build
        sh "zip jacoco.zip -r app/build/reports/jacocoTestReport/html/"
        archiveArtifacts artifacts: "jacoco.zip"
    }
}

def prSonarStage() {
    String additionalSonarAttributes = ''
    withCredentials([usernamePassword(credentialsId: 'github-token', passwordVariable: 'GITHUB_ACCESS_TOKEN', usernameVariable: 'GITHUB_USERNAME')]) {
        def prID = env.BRANCH_NAME.split("-")[1]
        additionalSonarAttributes = '-Dsonar.analysis.mode=preview'
        additionalSonarAttributes = "${additionalSonarAttributes} -Dsonar.github.pullRequest=${prID}"
        additionalSonarAttributes = "${additionalSonarAttributes} -Dsonar.github.oauth=${GITHUB_ACCESS_TOKEN}"
        additionalSonarAttributes = "${additionalSonarAttributes} -Dsonar.github.repository=${GITHUB_REPOSITORY}"
    }
    stage('Sonar analysis') {
        withSonarQubeEnv('sonar') {
            sh "./gradlew --info --stacktrace --debug sonarqube -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.login=${SONAR_AUTH_TOKEN} ${additionalSonarAttributes}"
        }
    }
}

def developSonarStage() {
    stage('Sonar analysis') {
        withSonarQubeEnv('sonar') {
            sh "./gradlew --info sonarqube -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.login=${SONAR_AUTH_TOKEN}"
        }
    }
}

def sonarQualityGate() {
    stage("Quality Gate") {
        timeout(time: 1) {
            def qg = waitForQualityGate()
            if (qg.status != 'OK') {
                error "Pipeline aborted due to quality gate failure: ${qg.status}"
            }
        }
    }
}

def buildStage() {
    stage('Build') {
        switch (getFlavour()) {
            case "sitMob":
                sh "./gradlew :app:assembleSitMobRelease"
                break
            case 'uatMob':
                sh "./gradlew :app:assembleUatMobRelease"
                break
            case 'prodMob':
                sh "./gradlew :app:assembleProdMobRelease"
                break
            default:
                sh "./gradlew :app:assembleDevMobDebug"
        }
    }
}

def buildArchiving() {
    stage('Archiving artifacts') {
        archiveArtifacts artifacts: getArchivePath()
    }
}

def getArchivePath() {
    def versionInfo = sh(
            script: './gradlew -q printVersion',
            returnStdout: true
    ).trim()
    switch (getFlavour()) {
        case 'sitMob':
            return "app/build/outputs/apk/sitMob/release/sitMobRelease-${versionInfo}.apk, app/build/outputs/mapping/sitMob/release/mapping.txt"
        case 'uatMob':
            return "app/build/outputs/apk/uatMob/release/uatMobRelease-${versionInfo}.apk, app/build/outputs/mapping/uatMob/release/mapping.txt "
        case 'prodMob':
            return "app/build/outputs/apk/prodMob/release/prodMobRelease-${versionInfo}.apk, app/build/outputs/mapping/prodMob/release/mapping.txt"
        default:
            return "app/build/outputs/apk/devMob/debug/devMobDebug-${versionInfo}.apk, app/build/outputs/mapping/devMob/debug/mapping.txt"
    }
}

def clean() {
    stage("Clean") {
        cleanWs()
    }
}

def getAppName() {
    switch (getFlavour()) {
        case 'sitMob':
            return "SIT-Android"
        case 'uatMob':
            return "UAT-Android"
        case 'prodMob':
            return "Prod-Android"
        default:
            return "Dev-Android"
    }
}

def uploadBuildToAppCenter() {
    stage('Upload the build to AppCenter') {
        def apiToken = ''
        def owner_name = ''
        def app_name = getAppName()

        def headers = "--header 'Content-Type: application/json' " +
                "--header 'Accept: application/json' " +
                "--header 'X-API-Token: ${apiToken}' "

        def url = "https://api.appcenter.ms/v0.1/apps/${owner_name}/${app_name}/release_uploads"

        def response = sh(script: "curl -X POST ${headers} ${url}", returnStdout: true)

        def upload_id = sh(script: "echo '${response}'  | jq '.upload_id'", returnStdout: true).replace("\"", "").trim()
        def upload_url = sh(script: "echo '${response}'  | jq '.upload_url'", returnStdout: true).replace("\"", "").trim()

        def values = getArchivePath().split(',')
        sh(script: "curl -F ipa=@${values[0]} ${upload_url}", returnStdout: true)

        def commit_url = "${headers} -d '{ \"status\": \"committed\"  }' 'https://api.appcenter.ms/v0.1/apps/${owner_name}/${app_name}/release_uploads/${upload_id}'"
        sh(script: "curl -X PATCH ${commit_url}", returnStdout: true)
    }
}