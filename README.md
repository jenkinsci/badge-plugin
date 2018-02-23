jenkins-batch-plugin
=========================

Jenkins plugin to add badges and build summary entries from a ipeline.

This plugin was forked from the [Groovy Postbuild Plugin](https://github.com/jenkinsci/groovy-postbuild-plugin) which will in future use the API from this plugin.


# addBadge

This method allows to add build badge icons.


![alt text](src/doc/badge.png "Badge")

```groovy

# puts a badge with the given icon and text.
addBadge(icon, text)

# like addBadge(icon, text), but the Badge icon then actually links to the given link
addBadge(icon, text, link) 

# puts a badge with  info icon and the given text.
addInfoBadge(text)

# puts a badge with  warning icon and the given text.
addWarningBadge(text)

# puts a badge with  error icon and the given text.
addErrorBadge(text)
```

# addShortText

Puts a badge with a short text

```groovy

# puts a badge with a short text, using the default format.
addShortText(text)

# puts a badge with a short text, using the specified format. For Colors supported, Google "html color names".
addShortText(text, color, background, border, borderColor)
```

# createSummary

Puts a badge with a short text

![alt text](src/doc/summary.png "Summary")


```groovy

# creates an entry in the build summary page and returns a summary object corresponding to this entry. The icon must be one of the 48x48 icons offered 
def summary = createSummary(icon)
summary.appendText(text, escapeHtml)
summary.appendText(text, escapeHtml, bold, italic, color)
```
# icons 
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