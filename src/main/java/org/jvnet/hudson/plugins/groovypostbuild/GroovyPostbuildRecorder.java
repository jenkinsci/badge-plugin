/*
 * The MIT License
 *
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., Serban Iordache
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jvnet.hudson.plugins.groovypostbuild;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import hudson.AbortException;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Recorder;
import hudson.util.IOUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.jenkinsci.plugins.scriptsecurity.sandbox.RejectedAccessException;
import org.jenkinsci.plugins.scriptsecurity.sandbox.Whitelist;
import org.jenkinsci.plugins.scriptsecurity.sandbox.groovy.GroovySandbox;
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.Whitelisted;
import org.jenkinsci.plugins.scriptsecurity.scripts.ApprovalContext;
import org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval;
import org.jenkinsci.plugins.scriptsecurity.scripts.languages.GroovyLanguage;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;

/** This class associates {@link GroovyPostbuildAction}s to a build. */
@SuppressWarnings("unchecked")
public class GroovyPostbuildRecorder extends Recorder {
	private static final Logger LOGGER = Logger.getLogger(GroovyPostbuildRecorder.class.getName());

	private final String groovyScript;
    private final boolean sandbox;
	private final int behavior;
    private final List<GroovyScriptPath> classpath;

    public static class BadgeManager {
		private AbstractBuild<?, ?> build;
		private final BuildListener listener;
		private final Result scriptFailureResult;
		private final Set<AbstractBuild<?, ?>> builds = new HashSet<AbstractBuild<?,?>>();
		private final boolean enableSecurity;

		public BadgeManager(AbstractBuild<?, ?> build, BuildListener listener, Result scriptFailureResult, boolean enableSecurity) {
			setBuild(build);
			this.listener = listener;
			this.scriptFailureResult = scriptFailureResult;
			this.enableSecurity = enableSecurity;
		}

        // TBD: @Whitelisted
		public Hudson getHudson() {
			if(enableSecurity){
				throw new SecurityException("access to 'hudson' is denied by global config");
			}
			return Hudson.getInstance();
		}
        // TBD: @Whitelisted
		public AbstractBuild<?, ?> getBuild() {
			if(enableSecurity){
				throw new SecurityException("access to 'build' is denied by global config");
			}
			return build;
		}
		public void setBuild(AbstractBuild<?, ?> build) {
			if(build != null) {
				this.build = build;
				builds.add(build);
			}
		}
		public boolean setBuildNumber(int buildNumber) {
			AbstractBuild<?, ?> newBuild = build.getProject().getBuildByNumber(buildNumber);
			setBuild(newBuild);
			return (newBuild != null);
		}
        // TBD: @Whitelisted
		public BuildListener getListener() {
			if(enableSecurity){
				throw new SecurityException("access to 'listener' is denied by global config");
			}
			return listener;
		}

        @Whitelisted
		public void addShortText(String text) {
			build.getActions().add(GroovyPostbuildAction.createShortText(text));
		}
        @Whitelisted
		public void addShortText(String text, String color, String background, String border, String borderColor) {
			build.getActions().add(GroovyPostbuildAction.createShortText(text, color, background, border, borderColor));
		}
        @Whitelisted
		public void addBadge(String icon, String text) {
			build.getActions().add(GroovyPostbuildAction.createBadge(icon, text));
		}
        @Whitelisted
		public void addBadge(String icon, String text, String link) {
			build.getActions().add(GroovyPostbuildAction.createBadge(icon, text, link));
		}
        @Whitelisted
		public void addInfoBadge(String text) {
			build.getActions().add(GroovyPostbuildAction.createInfoBadge(text));
		}
        @Whitelisted
		public void addWarningBadge(String text) {
			build.getActions().add(GroovyPostbuildAction.createWarningBadge(text));
		}
        @Whitelisted
		public void addErrorBadge(String text) {
			build.getActions().add(GroovyPostbuildAction.createErrorBadge(text));
		}
		public void removeBadges() {
			List<Action> actions = build.getActions();
			List<GroovyPostbuildAction> badgeActions = build.getActions(GroovyPostbuildAction.class);
			for(GroovyPostbuildAction action : badgeActions) {
				actions.remove(action);
			}
		}
		public void removeBadge(int index) {
			List<Action> actions = build.getActions();
			List<GroovyPostbuildAction> badgeActions = build.getActions(GroovyPostbuildAction.class);
			if(index < 0 || index >= badgeActions.size()) {
				listener.error("Invalid badge index: " + index + ". Allowed values: 0 .. " + (badgeActions.size()-1));
			} else {
				GroovyPostbuildAction action = badgeActions.get(index);
				actions.remove(action);
			}
		}

		public GroovyPostbuildSummaryAction createSummary(String icon) {
			GroovyPostbuildSummaryAction action = new GroovyPostbuildSummaryAction(icon);
			build.getActions().add(action);
			return action;
		}
		public void removeSummaries() {
			List<Action> actions = build.getActions();
			List<GroovyPostbuildSummaryAction> summaryActions = build.getActions(GroovyPostbuildSummaryAction.class);
			for(GroovyPostbuildSummaryAction action : summaryActions) {
				actions.remove(action);
			}
		}
		public void removeSummary(int index) {
			List<Action> actions = build.getActions();
			List<GroovyPostbuildSummaryAction> summaryActions = build.getActions(GroovyPostbuildSummaryAction.class);
			if(index < 0 || index >= summaryActions.size()) {
				listener.error("Invalid summary index: " + index + ". Allowed values: 0 .. " + (summaryActions.size()-1));
			} else {
				GroovyPostbuildSummaryAction action = summaryActions.get(index);
				actions.remove(action);
			}
		}

        @Whitelisted
		public void buildUnstable() {
			build.setResult(Result.UNSTABLE);
		}
        @Whitelisted
		public void buildFailure() {
			build.setResult(Result.FAILURE);
		}
        @Whitelisted
		public void buildSuccess() {
			build.setResult(Result.SUCCESS);
		}
        @Whitelisted
    public void buildAborted() {
      build.setResult(Result.ABORTED);
    }
        @Whitelisted
    public void buildNotBuilt() {
      build.setResult(Result.NOT_BUILT);
    }

		public void buildScriptFailed(Exception e) {
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			boolean isError = scriptFailureResult.isWorseThan(Result.UNSTABLE);
			String icon = isError ? "error" : "warning";
			GroovyPostbuildSummaryAction summary = createSummary(icon + ".gif");
			summary.appendText("<b><font color=\"red\">Groovy script failed:</font></b><br><pre>", false);
			summary.appendText(writer.toString(), true);
			summary.appendText("</pre>", false);

			addShortText("Groovy", "black", isError ? "#FFE0E0" : "#FFFFC0", "1px", isError ? "#E08080" : "#C0C080");

			Result result = build.getResult();
			if(result.isBetterThan(scriptFailureResult)) {
				build.setResult(scriptFailureResult);
			}
		}

        @Whitelisted
	    public boolean logContains(String regexp) {
	    	return contains(build.getLogFile(), regexp);
	    }

        // not @Whitelisted unless we know what file that is
	    public boolean contains(File f, String regexp) {
	    	Matcher matcher = getMatcher(f, regexp);
	    	return (matcher != null) && matcher.matches();
		}

        @Whitelisted
	    public Matcher getLogMatcher(String regexp) {
	    	return getMatcher(build.getLogFile(), regexp);
	    }

	    public Matcher getMatcher(File f, String regexp) {
	    	LOGGER.info("Searching for '" + regexp + "' in '" + f + "'.");
			Matcher matcher = null;
			BufferedReader reader = null;
			try {
		    	Pattern pattern = compilePattern(regexp);
				// Assume default encoding and text files
				String line;
				reader = new BufferedReader(new FileReader(f));
				while ((line = reader.readLine()) != null) {
					Matcher m = pattern.matcher(line);
					if (m.matches()) {
						matcher = m;
						break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace(listener.error("Groovy Postbuild: getMatcher(\"" + f + "\", \"" + regexp + "\") failed."));
				buildScriptFailed(e);
			} finally {
				IOUtils.closeQuietly(reader);
			}
			return matcher;
		}

	    private Pattern compilePattern(String regexp) throws AbortException {
	        Pattern pattern;
	        try {
	            pattern = Pattern.compile(regexp);
	        } catch (PatternSyntaxException e) {
	            listener.getLogger().println("Groovy Postbuild: Unable to compile regular expression '" + regexp + "'");
	            throw new AbortException();
	        }
	        return pattern;
	    }

	}

	@Deprecated
	public GroovyPostbuildRecorder(String groovyScript, List<GroovyScriptPath> classpath, int behavior) {
        this(groovyScript, false, classpath, behavior);
    }

    private static AbstractProject<?,?> currentProject() {
        StaplerRequest req = Stapler.getCurrentRequest();
        return req != null ? req.findAncestorObject(AbstractProject.class) : null;
    }

	@DataBoundConstructor
	public GroovyPostbuildRecorder(String groovyScript, boolean sandbox, List<GroovyScriptPath> classpath, int behavior) {
		this.groovyScript = sandbox ? groovyScript : ScriptApproval.get().configuring(groovyScript, GroovyLanguage.get(), ApprovalContext.create().withCurrentUser().withItem(currentProject()));
        this.sandbox = sandbox;
        this.classpath = classpath;
		this.behavior = behavior;
		LOGGER.fine("GroovyPostbuildRecorder created with groovyScript:\n" + groovyScript);
		LOGGER.fine("GroovyPostbuildRecorder behavior:" + behavior);
	}

    private Object readResolve() {
        if (!sandbox) {
            ScriptApproval.get().configuring(groovyScript, GroovyLanguage.get(), ApprovalContext.create());
        }
        return this;
    }

	@Override
	public final Action getProjectAction(final AbstractProject<?, ?> project) {
		return null;
	}

	@Override
    public GroovyPostbuildDescriptor getDescriptor() {
        return (GroovyPostbuildDescriptor)super.getDescriptor();
    }

	@Override
	public final boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener) throws InterruptedException, IOException {
        Hudson.getInstance().checkPermission(Hudson.ADMINISTER);
		LOGGER.fine("perform() called for script:\n" + groovyScript);
		LOGGER.fine("behavior: " + behavior);
		Result scriptFailureResult = Result.SUCCESS;
		switch(behavior) {
			case 0: scriptFailureResult = Result.SUCCESS; break;
			case 1: scriptFailureResult = Result.UNSTABLE; break;
			case 2: scriptFailureResult = Result.FAILURE; break;
		}
		BadgeManager badgeManager = new BadgeManager(build, listener, scriptFailureResult, getDescriptor().isSecurityEnabled());
        ClassLoader cl = new URLClassLoader(getClassPath(), getClass().getClassLoader());
        Binding binding = new Binding();
        binding.setVariable("manager", badgeManager);
        try {
            if (sandbox) {
                final GroovyShell shell = new GroovyShell(cl, binding, GroovySandbox.createSecureCompilerConfiguration());
                GroovySandbox.runInSandbox(new Runnable() {
                    @Override public void run() {
                        shell.evaluate(groovyScript);
                    }
                }, Whitelist.all());
            } else {
                new GroovyShell(cl, binding).evaluate(ScriptApproval.get().using(groovyScript, GroovyLanguage.get()));
            }
		} catch (Exception e) {
            // TODO could print more refined errors for UnapprovedUsageException and/or RejectedAccessException:
			e.printStackTrace(listener.error("Failed to evaluate groovy script."));
            if (e instanceof RejectedAccessException) {
                ScriptApproval.get().accessRejected((RejectedAccessException) e, ApprovalContext.create());
            }
			badgeManager.buildScriptFailed(e);
		}
		for(AbstractBuild<?, ?> b : badgeManager.builds) {
			b.save();
		}
		return build.getResult().isBetterThan(Result.FAILURE);
	}

    public List<GroovyScriptPath> getClasspath() {
        return classpath;
    }

    private URL[] getClassPath() throws MalformedURLException {
        URL[] urls = new URL[0];
        // even though classpath is final: existing, not updated jobs do not have it set when loaded from disc
        if(classpath != null) {
            urls = new URL[classpath.size()];
            int i = 0;
            for (GroovyScriptPath path : classpath) {
                urls[i++] = path.getPath().toURI().toURL();
            }
        }
        return urls;
    }

    public final BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	public String getGroovyScript() {
		return groovyScript;
	}

    public boolean isSandbox() {
        return sandbox;
    }

	public int getBehavior() {
		return behavior;
	}
}
