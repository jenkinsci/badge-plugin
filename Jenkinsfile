/* `buildPlugin` step provided by: https://github.com/jenkins-infra/pipeline-library */
buildPlugin(
  // Container agents start faster and are easier to administer
  useContainerAgent: true,
  // Show failures on all configurations
  failFast: false,
  // Test Java 11 with a recent LTS, Java 17 even more recent
  configurations: [
    [platform: 'linux',   jdk: '17', jenkins: '2.375.1'],
    [platform: 'windows', jdk: '11']
  ]
)
