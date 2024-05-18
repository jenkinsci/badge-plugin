# Badge plugin

[![Build Status](https://ci.jenkins.io/job/Plugins/job/badge-plugin/job/master/badge/icon)](https://ci.jenkins.io/job/Plugins/job/badge-plugin/job/master/)
[![Codecov Coverage](https://codecov.io/gh/jenkinsci/badge-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/jenkinsci/badge-plugin)
[![Jenkins Security Scan](https://github.com/jenkinsci/badge-plugin/actions/workflows/jenkins-security-scan.yml/badge.svg)](https://github.com/jenkinsci/badge-plugin/actions/workflows/jenkins-security-scan.yml)

[![Installations](https://img.shields.io/jenkins/plugin/i/badge.svg?color=blue&label=installations)](https://stats.jenkins.io/pluginversions/badge.html)
[![Contributors](https://img.shields.io/github/contributors/jenkinsci/badge-plugin.svg?color=blue)](https://github.com/jenkinsci/badge-plugin/graphs/contributors)
[![Changelog](https://img.shields.io/github/release/jenkinsci/badge-plugin.svg?label=changelog)](https://github.com/jenkinsci/badge-plugin/releases/latest)

Jenkins plugin to add badges and build summary entries from a pipeline.

This plugin was forked from the [Groovy Postbuild Plugin](https://plugins.jenkins.io/groovy-postbuild) which will in future use the API from this plugin.

## addBadge

This method allows to add build badge icons.

![alt text](src/doc/badge.png "Badge")

```groovy

// puts a badge with the given icon and text.

// addBadge
// ------------------------------------------

/**
 * minimal params
 *
 * icon: The icon for this badge
 * text: The text for this badge
 */
addBadge(icon: <icon>, text: <text>)

/**
 * all params
 *
 * icon: The icon for this badge
 * text: The text for this badge
 * color: (optional) The Jenkins palette/semantic color name of the badge icon symbol
 * id: (optional) Badge identifier. Selectively delete badges by id.
 * link: (optional) The link to be added to this badge
 */
addBadge(icon: <icon>, text: <text>, color: <color>,
         id: <id>, link: <link>)


// addInfoBadge
// ------------------------------------------

/**
 * minimal params
 *
 * text: The info text for this badge
 */
addInfoBadge(text: <text>)

/**
 * all params
 *
 * text: The info text for this badge
 * id: (optional) Badge identifier. Selectively delete badges by id.
 * link: (optional) The link to be added to this badge
 */
addInfoBadge(text: <text>, id: <id>, link: <link>)


// addWarningBadge
// ------------------------------------------

/**
 * minimal params
 *
 * text: The text for this warning badge
 */
addWarningBadge(text: <text>)

/**
 * all params
 *
 * text: The text for this warning badge
 * id: (optional) Badge identifier. Selectively delete badges by id.
 * link: (optional) The link to be added to this badge
 */
addWarningBadge(text: <text>, id: <id>, link: <link>)


// addErrorBadge
// ------------------------------------------

/**
 * minimal params
 *
 * text: The text for this error badge
 */
addErrorBadge(text: <text>)

/**
 * all params
 *
 * text: The text for this error badge
 * id: (optional) Badge identifier. Selectively delete badges by id.
 * link: (optional) The link to be added to this badge
 */
addErrorBadge(text: <text>, id: <id>, link: <link>)

```

## removeBadges

Removes badges

```groovy

// removes badges. If no id is provided all are removed.
// If an id is provided, remove badges with the matching id.

// removeBadges
// ------------------------------------------

/**
 * minimal params
 *
 */
removeBadges()

/**
 * all params
 *
 * id: (optional) Badge identifier. Selectively delete badges by id.
 */
removeBadges(id: <id>)

```

## addHtmlBadge

Puts a badge with custom html

```groovy

// puts a badge with a custom html content.
// addHtmlBadge
// ------------------------------------------

/**
 * minimal params
 *
 * html: The html content to be used for this badge
 */
addHtmlBadge(html: <html>)

/**
 * all params
 *
 * html: The html content to be used for this badge
 * id: (optional) Badge identifier. Selectively delete badges by id.
 */
addHtmlBadge(html: <html>, id: <id>)

```
## removeHtmlBadges

Removes html badges

```groovy

// removes html badges. If no id is provided all are removed.
// If an id is provided, remove badges with the matching id.

// removeHtmlBadges
// ------------------------------------------

/**
 * minimal params
 *
 */
removeHtmlBadges()

/**
 * all params
 *
 * id: (optional) Badge identifier. Selectively delete badges by id.
 */
removeHtmlBadges(id: <id>)
 ```

## addShortText

Puts a badge with a short text

```groovy

// puts a badge with a short text, using the default format.
// For Colors supported, Google "html color names".

// addShortText
// ------------------------------------------

/**
 * minimal params
 *
 * text: The text to add fot this badge
 */
addShortText(text: <text>)

/**
 * all params
 *
 * text: The text to add fot this badge
 * background: (optional) The background-color for this short text
 * border: (optional) The border width for this short text
 * borderColor: (optional) The order color for this short text
 * color: (optional) The color for this short text
 * link: (optional) The link for this short text
 */
addShortText(text: <text>, background: <background>,
             border: <border>, borderColor: <borderColor>,
             color: <color>, link: <link>)

```

## createSummary

Puts a badge with a short text

![alt text](src/doc/summary.png "Summary")

```groovy

// creates an entry in the build summary page and returns a summary
// object corresponding to this entry. The icon must be one of the 48x48
// icons offered.

// createSummary
// ------------------------------------------

/**
 * minimal params
 *
 * icon: The icon for this summary
 */
createSummary(icon: <icon>)

/**
 * all params
 *
 * icon: The icon for this summary
 * id: (optional) Badge identifier. Selectively delete badges by id.
 * text: (optional) The title text for this summary
 */
createSummary(icon: <icon>, id: <id>, text: <text>)


def summary = createSummary(icon)
summary.appendText(text, escapeHtml)
summary.appendText(text, escapeHtml, bold, italic, color)
```

## removeSummaries

Removes summaries

```groovy

// removes summaries. If no id is provided all are removed.
// If an id is provided, remove badges with the matching id.

// removeSummaries
// ------------------------------------------

/**
 * minimal params
 *
 */
removeSummaries()

/**
 * all params
 *
 * id: (optional) Badge identifier. Selectively delete badges by id.
 */
removeSummaries(id: <id>)

```

## Icons

Icons can reference [Jenkins Symbols](https://weekly.ci.jenkins.io/design-library/Symbols/), including all the symbols provided by the [ionicons-api-plugin](https://plugins.jenkins.io/ionicons-api).

More symbols can be referenced by installing additional plugins, such as [font-awesome-api-plugin](https://plugins.jenkins.io/font-awesome-api) or [custom-folder-icon-plugin](https://plugins.jenkins.io/custom-folder-icon).

```groovy
addBadge(icon: 'symbol-cube', text: 'a cubed build')
addBadge(icon: 'symbol-star plugin-ionicons-api', text: 'a starred build')
addBadge(icon: 'symbol-symbolName plugin-yourArtifactId', text: 'another icon build')
```

In addition to the default [16x16](https://github.com/jenkinsci/jenkins/tree/master/war/src/main/webapp/images/16x16) icons offered by Jenkins, badge plugin provides the following icons:

- ![alt text](src/main/webapp/images/completed.gif "completed.gif") completed.gif
- ![alt text](src/main/webapp/images/db_in.gif "db_in.gif") db_in.gif
- ![alt text](src/main/webapp/images/db_out.gif "db_out.gif") db_out.gif
- ![alt text](src/main/webapp/images/delete.gif "delete.gif") delete.gif
- ![alt text](src/main/webapp/images/error.gif "error.gif") error.gif
- ![alt text](src/main/webapp/images/folder.gif "folder.gif") folder.gif
- ![alt text](src/main/webapp/images/green.gif "green.gif") green.gif
- ![alt text](src/main/webapp/images/info.gif "info.gif") info.gif
- ![alt text](src/main/webapp/images/red.gif "red.gif") red.gif
- ![alt text](src/main/webapp/images/save.gif "save.gif") save.gif
- ![alt text](src/main/webapp/images/success.gif "success.gif") success.gif
- ![alt text](src/main/webapp/images/text.gif "text.gif") text.gif
- ![alt text](src/main/webapp/images/warning.gif "warning.gif") warning.gif
- ![alt text](src/main/webapp/images/yellow.gif "yellow.gif") yellow.gif

### Other plugin icons

Other plugin icons can be used by setting the path of the icon within the jenkins context. Don't forget the leading '/'.

```groovy
addBadge(icon: "/static/8361d0d6/images/16x16/help.png", text: "help")
```

## Colors

Colors may reference [Jenkins palette colors or variables](https://weekly.ci.jenkins.io/design-library/Colors/).

`symbol` badges **must** reference a named color from the Jenkins palette.
```groovy
// jenkins palette colors
addBadge(icon: 'symbol-star plugin-ionicons-api', text: 'A star', color: 'yellow')
addBadge(icon: 'symbol-star plugin-ionicons-api', text: 'A star', color: 'dark-yellow')
addBadge(icon: 'symbol-star plugin-ionicons-api', text: 'A star', color: 'jenkins-!-color-yellow')

// jenkins semantic colors
addBadge(icon: 'symbol-star plugin-ionicons-api', text: 'A star', color: 'warning')
addBadge(icon: 'symbol-star plugin-ionicons-api', text: 'A star', color: 'success')
addBadge(icon: 'symbol-star plugin-ionicons-api', text: 'A star', color: 'jenkins-!-warning-color')
```

Short text badges may use additional css color styles.
```groovy
// jenkins palette colors
addShortText(text: 'ok', color: 'green')
addShortText(text: 'ok', color: 'jenkins-!-color-green')

// jenkins semantic colors
addShortText(text: 'ok', color: 'success')
addShortText(text: 'ok', color: 'jenkins-!-success-color')

// css colors
addShortText(text: 'ok', color: '#42f557')
addShortText(text: 'ok', color: 'rgb(66, 245, 87)')
addShortText(text: 'ok', color: 'var(--green)') // jenkins css vars
```

## Allow HTML in Badge and Summary

The badge plugin uses by default the OWASP Markup Formatter to sanitize the HTML Badge and Summary. This feature can be disabled in the Jenkins configuration:
Manage Jenkins -> Configure System -> Badge Plugin

![alt text](src/doc/config.png "Config")

## Configuration as Code Sample

The [configuration as code plugin](https://plugins.jenkins.io/configuration-as-code/) can define the markup formatter configuration for the badge plugin.
Markup formatter sanitization is enabled by default.
The configuration as code setup for that default setting is:

```yaml
security:
  badgePlugin:
    disableFormatHTML: false
```
