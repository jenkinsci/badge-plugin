/*
 See the documentation for more options:
 https://github.com/jenkins-infra/pipeline-library/
*/
buildPlugin(
  forkCount: '1C', // Run parallel tests on ci.jenkins.io for lower costs, faster feedback
  useContainerAgent: true, // Set to `false` if you need to use Docker for containerized tests
  configurations: [
    [platform: 'linux', jdk: 21, jenkins: '2.505'], // UITest fails on 2.504 and 2.505
    [platform: 'windows', jdk: 17, jenkins: '2.504'],
])
