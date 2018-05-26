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

/**
 * minimal params
 * 
 * icon: The icon for this badge
 * text: The text for this badge
 */
addBadge(icon, text)

/**
 * all params
 * 
 * icon: The icon for this badge
 * text: The text for this badge
 * id: (optional) The id for this badge. This id can be used to selectively delete badges.
 * link: (optional) The link to be added to this badge
 */
addBadge(icon, text, id, link)


// addInfoBadge
// ------------------------------------------

/**
 * minimal params
 * 
 * text: The info text for this badge
 */
addInfoBadge(text)

/**
 * all params
 * 
 * text: The info text for this badge
 * id: (optional) The id for this badge. This id can be used to selectively delete badges.
 * link: (optional) The link to be added to this badge
 */
addInfoBadge(text, id, link)


// addWarningBadge
// ------------------------------------------

/**
 * minimal params
 * 
 * text: The text for this warning badge
 */
addWarningBadge(text)

/**
 * all params
 * 
 * text: The text for this warning badge
 * id: (optional) The id for this badge. This id can be used to selectively delete badges.
 * link: (optional) The link to be added to this badge
 */
addWarningBadge(text, id, link)


// addErrorBadge
// ------------------------------------------

/**
 * minimal params
 * 
 * text: The text for this error badge
 */
addErrorBadge(text)

/**
 * all params
 * 
 * text: The text for this error badge
 * id: (optional) The id for this badge. This id can be used to selectively delete badges.
 * link: (optional) The link to be added to this badge
 */
addErrorBadge(text, id, link)

```


## removeBadges

Removes badges

```groovy

// removes badges. If no id is provided all are removed, otherwise only the badges with a matching id
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
 * id: (optional) The id for this badge. This id can be used to selectively delete badges.
 */
removeBadges(id)

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
addHtmlBadge(html)

/**
 * all params
 * 
 * html: The html content to be used for this badge
 * id: (optional) The id for this badge. This id can be used to selectively delete badges.
 */
addHtmlBadge(html, id)

```
## removeHtmlBadges

Removes html badges

```groovy

// removes html badges. If no id is provided all are removed, otherwise only the badges with a matching id
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
 * id: (optional) The id for this badge. This id can be used to selectively delete badges.
 */
removeHtmlBadges(id)

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
addShortText(text)

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
addShortText(text, background, border, borderColor, color, link)

```

## createSummary

Puts a badge with a short text

![alt text](src/doc/summary.png "Summary")


```groovy

// creates an entry in the build summary page and returns a summary object corresponding to this entry. The icon must be one of the 48x48 icons offered

// createSummary
// ------------------------------------------

/**
 * minimal params
 * 
 * icon: The icon for this summary
 */
createSummary(icon)

/**
 * all params
 * 
 * icon: The icon for this summary
 * id: (optional) The id for this badge. This id can be used to selectively delete badges.
 * text: (optional) The title text for this summary
 */
createSummary(icon, id, text)


def summary = createSummary(icon)
summary.appendText(text, escapeHtml)
summary.appendText(text, escapeHtml, bold, italic, color)
```
## icons
The badge plugin provides the following additional icons:

![alt text](src/main/webapp/images/completed.gif "completed.gif")
![alt text](src/main/webapp/images/db_in.gif "db_in.gif")
![alt text](src/main/webapp/images/db_out.gif "db_out.gif")
![alt text](src/main/webapp/images/delete.gif "delete.gif")
![alt text](src/main/webapp/images/error.gif "error.gif")
![alt text](src/main/webapp/images/folder.gif "folder.gif")
![alt text](src/main/webapp/images/green.gif "green.gif")
![alt text](src/main/webapp/images/info.gif "info.gif")
![alt text](src/main/webapp/images/red.gif "red.gif")
![alt text](src/main/webapp/images/save.gif "save.gif")
![alt text](src/main/webapp/images/success.gif "success.gif")
![alt text](src/main/webapp/images/text.gif "text.gif")
![alt text](src/main/webapp/images/warning.gif "warning.gif")
![alt text](src/main/webapp/images/yellow.gif "yellow.gif")

