node {
 def flavorCombination=''

 stage "checkout"
 checkout scm

 stage 'assemble'
 sh "./gradlew clean assemble${flavorCombination}Release"
 archive 'mobile/build/outputs/apk/*'

 stage 'lint'
 sh "./gradlew lint${flavorCombination}Release"
publishHTML(target:[allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'mobile/build/outputs/', reportFiles: "lint-results-*elease.html", reportName: 'Lint'])

 stage 'test'
 sh "./gradlew test${flavorCombination}DebugUnitTest"
 publishHTML(target:[allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'mobile/build/reports/tests/', reportFiles: "*/index.html", reportName: 'UnitTest'])
 
}