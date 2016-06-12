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
import hudson.AbortException;
import hudson.EnvVars;
import hudson.Launcher;
import hudson.matrix.MatrixAggregatable;
import hudson.matrix.MatrixAggregator;
import hudson.matrix.MatrixBuild;
import hudson.model.*;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Recorder;
import hudson.util.IOUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import jenkins.model.Jenkins;

import org.jenkinsci.plugins.scriptsecurity.sandbox.groovy.SecureGroovyScript;
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.Whitelisted;
import org.jenkinsci.plugins.scriptsecurity.scripts.ApprovalContext;
import org.jenkinsci.plugins.scriptsecurity.scripts.ClasspathEntry;

/** This class associates {@link GroovyPostbuildAction}s to a build. */
@SuppressWarnings("unchecked")
public class GroovyPostbuildRecorder extends Recorder implements MatrixAggregatable {
	private static final Logger LOGGER = Logger.getLogger(GroovyPostbuildRecorder.class.getName());

	@Deprecated private String groovyScript;
    private SecureGroovyScript script;
	private final int behavior;
    @Deprecated private List<GroovyScriptPath> classpath;
	private final boolean runForMatrixParent;

    public static class BadgeManager {
		private Run<?, ?> build;
		private final TaskListener listener;
		private final Result scriptFailureResult;
		private final Set<Run<?, ?>> builds = new HashSet<Run<?, ?>>();
		private EnvVars envVars;

		public BadgeManager(Run<?, ?> build, TaskListener listener, Result scriptFailureResult) {
			setBuild(build);
			try {
				this.envVars = build.getEnvironment(listener);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace(listener.getLogger());
			} catch (IOException e){
				e.printStackTrace(listener.getLogger());
			}
			this.listener = listener;
			this.scriptFailureResult = scriptFailureResult;
		}

        // TBD: @Whitelisted
		public EnvVars getEnvVars(){
			return this.envVars;
		}

        @Whitelisted
		public void println(String string){
			this.listener.getLogger().println(string);
		}
		
        @Whitelisted
		public String getEnvVariable(String key) throws IOException, InterruptedException{
			return this.envVars.get(key);
		}
		
        // TBD: @Whitelisted
		public Hudson getHudson() {
			return Hudson.getInstance();
		}
        // TBD: @Whitelisted
		public Run<?, ?> getBuild() {
			return build;
		}
		public void setBuild(Run<?, ?> build) {
			if(build != null) {
				this.build = build;
				builds.add(build);
			}
		}
		public boolean setBuildNumber(int buildNumber) {
			Run<?, ?> newBuild = build.getParent().getBuildByNumber(buildNumber);
			setBuild(newBuild);
			return (newBuild != null);
		}
        // TBD: @Whitelisted
		public TaskListener getListener() {
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

        @Whitelisted
        public String getResult() {
            Result r = build.getResult();
            return (r != null) ? r.toString() : null;
        }

		@Whitelisted
		public void removeBadges() {
			List<Action> actions = build.getActions();
			List<GroovyPostbuildAction> badgeActions = build.getActions(GroovyPostbuildAction.class);
			for(GroovyPostbuildAction action : badgeActions) {
				actions.remove(action);
			}
		}
		@Whitelisted
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
			if(result == null || result.isBetterThan(scriptFailureResult)) {
				build.setResult(scriptFailureResult);
			}
		}

        @Whitelisted
	    public boolean logContains(String regexp) {
	    	return contains(build.getLogFile(), build.getCharset(), regexp);
	    }


        @Deprecated
        public boolean contains(File f, String regexp) {
            return contains(f, Charset.defaultCharset(), regexp);
        }

        // not @Whitelisted unless we know what file that is
	    public boolean contains(File f, Charset charset, String regexp) {
	    	Matcher matcher = getMatcher(f, charset, regexp);
	    	return (matcher != null) && matcher.matches();
		}

        @Whitelisted
	    public Matcher getLogMatcher(String regexp) {
	    	return getMatcher(build.getLogFile(), build.getCharset(), regexp);
	    }

        @Deprecated
        public Matcher getMatcher(File f, String regexp) {
            return getMatcher(f, Charset.defaultCharset(), regexp);
        }

	    public Matcher getMatcher(File f, Charset charset, String regexp) {
	    	LOGGER.fine("Searching for '" + regexp + "' in '" + f + "'.");
			Matcher matcher = null;
			BufferedReader reader = null;
			try {
		    	Pattern pattern = compilePattern(regexp);
				// Assume default encoding and text files
				String line;
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), charset));
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

		/**
		 * Test whether the current build is specified type.
		 * 
		 * @param buildClass
		 * @return true if the current build is an instance of buildClass
		 */
		@Whitelisted
		public boolean buildIsA(Class<? extends AbstractBuild<?, ?>> buildClass) {
			return buildClass.isInstance(getBuild());
		}
	}

	@DataBoundConstructor
	public GroovyPostbuildRecorder(SecureGroovyScript script, int behavior, boolean runForMatrixParent) {
        this.script = script.configuringWithNonKeyItem();
		this.behavior = behavior;
		this.runForMatrixParent = runForMatrixParent;
	}

    private Object readResolve() {
        if (groovyScript != null) {
            List<ClasspathEntry> cp = new ArrayList<ClasspathEntry>();
            if (classpath != null) {
                for (@SuppressWarnings("deprecation") GroovyScriptPath gsp : classpath) {
                    try {
                        cp.add(new ClasspathEntry(gsp.path.getAbsolutePath()));
                    } catch (MalformedURLException x) {
                        LOGGER.log(Level.WARNING, "cannot load " + gsp.path, x);
                    }
                }
                classpath = null;
            }
            script = new SecureGroovyScript(groovyScript, false, cp).configuring(ApprovalContext.create());
            groovyScript = null;
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
		boolean scriptResult = true;
		LOGGER.fine("perform() called for script");
		LOGGER.fine("behavior: " + behavior);
		Result scriptFailureResult = Result.SUCCESS;
		switch(behavior) {
			case 0: scriptFailureResult = Result.SUCCESS; break;
			case 1: scriptFailureResult = Result.UNSTABLE; break;
			case 2: scriptFailureResult = Result.FAILURE; break;
			default:scriptFailureResult = Result.SUCCESS; break; // same to 0
		}
		BadgeManager badgeManager = new BadgeManager(build, listener, scriptFailureResult);
        ClassLoader cl = Jenkins.getActiveInstance().getPluginManager().uberClassLoader;
        Binding binding = new Binding();
        binding.setVariable("manager", badgeManager);
        try {
            script.evaluate(cl, binding);
		} catch (Exception e) {
            // TODO could print more refined errors for UnapprovedUsageException and/or RejectedAccessException:
			e.printStackTrace(listener.error("Failed to evaluate groovy script."));
			badgeManager.buildScriptFailed(e);
			scriptResult = false;
		}
		for (Run<?, ?> b : badgeManager.builds) {
			b.save();
		}
		
		if (!scriptResult && scriptFailureResult.isWorseOrEqualTo(Result.FAILURE)){
			return false;
		} else {
			return true;
		}
	}

    public final BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	public SecureGroovyScript getScript() {
		return script;
	}

	public int getBehavior() {
		return behavior;
	}
	
	public boolean isRunForMatrixParent() {
		return runForMatrixParent;
	}
	
	/**
	 * @param build
	 * @param launcher
	 * @param listener
	 * @return
	 * @see hudson.matrix.MatrixAggregatable#createAggregator(hudson.matrix.MatrixBuild, hudson.Launcher, hudson.model.BuildListener)
	 */
	public MatrixAggregator createAggregator(final MatrixBuild build, final Launcher launcher, final BuildListener listener) {
		if (!isRunForMatrixParent()) {
			return null;
		}
		
		return new MatrixAggregator(build, launcher, listener) {
			/**
			 * Called when all child builds are finished.
			 * 
			 * @return
			 * @throws InterruptedException
			 * @throws IOException
			 * @see hudson.matrix.MatrixAggregator#endBuild()
			 */
			@Override
			public boolean endBuild() throws InterruptedException, IOException {
				return perform(build, launcher, listener);
			}
		};
	}
}
