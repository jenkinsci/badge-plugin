jenkins-badge-plugin
=========================

Jenkins plugin to add badges and build summary entries from a pipeline.

This plugin was forked from the [Groovy Postbuild Plugin](https://github.com/jenkinsci/groovy-postbuild-plugin) which will in future use the API from this plugin.


## addBadge

This method allows to add build badge icons.


![alt text](src/doc/badge.png "Badge")

```groovy

// puts a badge with the given icon and text.

// addBadge
// ------------------------------------------

// minimal params
addBadge(icon, text)

// all params
addBadge(icon, text, link)


// addInfoBadge
// ------------------------------------------

// minimal params
addInfoBadge(text)

// all params
addInfoBadge(text, link)


// addWarningBadge
// ------------------------------------------

// minimal params
addWarningBadge(text)

// all params
addWarningBadge(text, link)


// addErrorBadge
// ------------------------------------------

// minimal params
addErrorBadge(text)

// all params
addErrorBadge(text, link)

```

## addHtmlBadge

Puts a badge with custom html

```groovy

// puts a badge with a custom html content.
// addHtmlBadge
// ------------------------------------------

// params
addHtmlBadge(html)


```

## addShortText

Puts a badge with a short text

```groovy

// puts a badge with a short text, using the default format.
// For Colors supported, Google "html color names".

// addShortText
// ------------------------------------------

// minimal params
addShortText(text)

// all params
addShortText(text, background, border, borderColor, color, link)

```

## createSummary

Puts a badge with a short text

![alt text](src/doc/summary.png "Summary")


```groovy

// creates an entry in the build summary page and returns a summary object corresponding to this entry. The icon must be one of the 48x48 icons offered

// createSummary
// ------------------------------------------

// minimal params
createSummary(icon)

// all params
createSummary(icon, text)


def summary = createSummary(icon)
summary.appendText(text, escapeHtml)
summary.appendText(text, escapeHtml, bold, italic, color)
```
## icons
The badge plugin provides the following additional icons:

![alt text](src/main/webapp/images/delete.gif "delete.gif")
![alt text](src/main/webapp/images/completed.gif "completed.gif")
![alt text](src/main/webapp/images/db_out.gif "db_out.gif")
![alt text](src/main/webapp/images/info.gif "info.gif")
![alt text](src/main/webapp/images/db_in.gif "db_in.gif")
![alt text](src/main/webapp/images/text.gif "text.gif")
![alt text](src/main/webapp/images/red.gif "red.gif")
![alt text](src/main/webapp/images/success.gif "success.gif")
![alt text](src/main/webapp/images/green.gif "green.gif")
![alt text](src/main/webapp/images/save.gif "save.gif")
![alt text](src/main/webapp/images/error.gif "error.gif")
![alt text](src/main/webapp/images/warning.gif "warning.gif")
![alt text](src/main/webapp/images/folder.gif "folder.gif")
![alt text](src/main/webapp/images/yellow.gif "yellow.gif")

