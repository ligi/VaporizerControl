node {
 def flavorCombination=''

 stage "checkout"
 checkout scm

 stage 'assemble'
 sh "./gradlew clean assemble${flavorCombination}Release"
 archive 'mobile/build/outputs/apk/*'

 stage 'lint'
 try {
  sh "./gradlew clean lint${flavorCombination}Release"
 } catch(err) {
  currentBuild.result = FAILURE
 } finally {
  publishHTML(target:[allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'mobile/build/outputs/', reportFiles: "lint-results-*.html", reportName: 'Lint mobile'])

  publishHTML(target:[allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'wear/build/outputs/', reportFiles: "lint-results-*.html", reportName: 'Lint wear'])

  publishHTML(target:[allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'core/build/outputs/', reportFiles: "lint-results-*.html", reportName: 'Lint core'])
 }

 stage 'test'
 sh "./gradlew test${flavorCombination}DebugUnitTest"
 publishHTML(target:[allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'mobile/build/reports/tests/', reportFiles: "*/index.html", reportName: 'UnitTest'])
 
}