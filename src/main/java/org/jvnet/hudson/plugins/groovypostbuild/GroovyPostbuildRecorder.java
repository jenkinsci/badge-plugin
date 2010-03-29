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

import groovy.lang.GroovyShell;
import hudson.AbortException;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Hudson;
import hudson.model.Result;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Recorder;
import hudson.util.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.kohsuke.stapler.DataBoundConstructor;

/** This class associates {@link GroovyPostbuildAction}s to a build. */
@SuppressWarnings("unchecked")
public class GroovyPostbuildRecorder extends Recorder {
	private static final Logger LOGGER = Logger.getLogger(GroovyPostbuildRecorder.class.getName());

	private final String groovyScript;

	public static class BadgeManager {
		private AbstractBuild<?, ?> build;
		private final BuildListener listener;
		private final Set<AbstractBuild<?, ?>> builds = new HashSet<AbstractBuild<?,?>>();

		public BadgeManager(AbstractBuild<?, ?> build, BuildListener listener) {
			setBuild(build);
			this.listener = listener;
		}
		
		public Hudson getHudson() {
			return Hudson.getInstance();
		}
		public AbstractBuild<?, ?> getBuild() {
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
		public BuildListener getListener() {
			return listener;
		}
		
		public void addShortText(String text) {
			build.getActions().add(GroovyPostbuildAction.createShortText(text));
		}
		public void addShortText(String text, String color, String background, String border, String borderColor) {
			build.getActions().add(GroovyPostbuildAction.createShortText(text, color, background, border, borderColor));
		}
		public void addBadge(String icon, String text) {
			build.getActions().add(GroovyPostbuildAction.createBadge(icon, text));
		}
		public void addInfoBadge(String text) {
			build.getActions().add(GroovyPostbuildAction.createInfoBadge(text));
		}
		public void addWarningBadge(String text) {
			build.getActions().add(GroovyPostbuildAction.createWarningBadge(text));
		}
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
		
		public void buildUnstable() {
			build.setResult(Result.UNSTABLE);
		}
		public void buildFailure() {
			build.setResult(Result.FAILURE);
		}
		public void buildSuccess() {
			build.setResult(Result.SUCCESS);
		}
		
	    public boolean logContains(String regexp) {
	    	return contains(build.getLogFile(), regexp);
	    }

	    public boolean contains(File f, String regexp) {
	    	Matcher matcher = getMatcher(f, regexp);
	    	return (matcher != null) && matcher.matches(); 
		}

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
	
	@DataBoundConstructor
	public GroovyPostbuildRecorder(String groovyScript) {
		this.groovyScript = groovyScript;
		LOGGER.fine("GroovyPostbuildRecorder created with groovyScript:\n" + groovyScript);
	}

	@Override
	public final Action getProjectAction(final AbstractProject<?, ?> project) {
		return null;
	}

	@Override
	public final boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener) throws InterruptedException, IOException {
        Hudson.getInstance().checkPermission(Hudson.ADMINISTER);
		LOGGER.fine("perform() called for script:\n" + groovyScript);
		
		BadgeManager badgeManager = new BadgeManager(build, listener);
		GroovyShell shell = new GroovyShell();
        shell.setVariable("manager", badgeManager);
        try {
			shell.evaluate(groovyScript);
		} catch (Exception e) {
			e.printStackTrace(listener.error("Failed to evaluate groovy script."));
		}
		for(AbstractBuild<?, ?> b : badgeManager.builds) {
			b.save();
		}
		return build.getResult().isBetterThan(Result.FAILURE);
	}

	public final BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}
	
	public String getGroovyScript() {
		return groovyScript;
	}
}
