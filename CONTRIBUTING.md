# Contributing to the Badge Plugin

Plugin source code is hosted on [GitHub](https://github.com/jenkinsci/badge-plugin).
New feature proposals and bug fix proposals should be submitted as
[GitHub pull requests](https://help.github.com/articles/creating-a-pull-request).
Your pull request will be evaluated by the [Jenkins job](https://ci.jenkins.io/job/Plugins/job/badge-plugin/).

Before submitting your change, please assure that you've added tests
which verify your change.

## Code Coverage

Code coverage reporting is available as a maven target.
Please try to improve code coverage with tests when you submit a pull request.
* `mvn -P enable-jacoco clean install jacoco:report` to report code coverage

Please don't introduce new spotbugs output.
* `mvn spotbugs:check` to analyze project using [Spotbugs](https://spotbugs.github.io)
* `mvn spotbugs:gui` to review report using GUI

## Maintaining the README

The README file samples are created during the maven `prepare-package` phase by the ReadmeGenerator.
The base template for the generator is `src/test/resources/readme/README.tmpl`.

Update the README with the command:
* `mvn prepare-package`

## Security Issues

Follow the [Jenkins project vulnerability reporting instructions](https://jenkins.io/security/reporting/) to report vulnerabilities.
