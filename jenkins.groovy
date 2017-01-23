node('master') {

    // we need the new versions
    stage ('Checkout') {
        dir('brein-workspace') {
            git credentialsId: 'BREIN-STAGE-ACCESS', url: 'https://github.com/Breinify/brein-workspace.git'
        }

        dir ('brein-time-utilities') {
            git url: 'https://github.com/Breinify/brein-time-utilities.git'
        }
    }

    // it is enough to run the test, it will resolve, build and test
    stage ('Test & Build') {
        try {
            dir ('brein-time-utilities/brein-time-utilities') {
                sh 'ant 06-run-test-suite'
            }
        } catch (err) {
            emailextrecipients([[$class: 'CulpritsRecipientProvider']])
            throw err
        }
    }

    // now we should just publish the new version, it made it through the test
    stage ('Deploy') {
        try {
            dir ('brein-time-utilities/brein-time-utilities') {
                sh 'ant 04-publish-results'
            }
        } catch (err) {
            emailextrecipients([[$class: 'CulpritsRecipientProvider']])
            throw err
        }
    }
}