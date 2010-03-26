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

import java.io.IOException;
import java.util.List;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.Plugin;
import hudson.model.Action;
import hudson.model.Job;
import hudson.model.Run;

public class GroovyPostbuildPlugin extends Plugin {

    public void doRemoveBadges(StaplerRequest req, StaplerResponse rsp) throws IOException {
    	removeActions(GroovyPostbuildAction.class, req, rsp);
	}

    public void doRemoveSummaries(StaplerRequest req, StaplerResponse rsp) throws IOException {
    	removeActions(GroovyPostbuildSummaryAction.class, req, rsp);
	}
    
    @SuppressWarnings("unchecked")
	private void removeActions(Class type, StaplerRequest req, StaplerResponse rsp) throws IOException {
		req.findAncestorObject(Job.class).checkPermission(Run.UPDATE);
		Run run = req.findAncestorObject(Run.class);
		if (run != null) {
			List<Action> actions = run.getActions();
			List<Action> groovyActions = run.getActions(type);
			for(Action action : groovyActions) {
				actions.remove(action);
			}
			run.save();
			rsp.sendRedirect(req.getRequestURI().substring(0, req.getRequestURI().indexOf("parent/parent")));
		}
	}
    
    public void doRemoveBadge(StaplerRequest req, StaplerResponse rsp) throws IOException {
    	removeAction(GroovyPostbuildAction.class, req, rsp);
	}

    public void doRemoveSummary(StaplerRequest req, StaplerResponse rsp) throws IOException {
    	removeAction(GroovyPostbuildSummaryAction.class, req, rsp);
	}
    
    @SuppressWarnings("unchecked")
    private void removeAction(Class type, StaplerRequest req, StaplerResponse rsp) throws IOException {
    	String index = req.getParameter("index");
    	if(index == null) {
    		throw new IOException("Missing parameter 'index'.");
    	}
		int idx;
		try {
			idx = Integer.parseInt(index);
		} catch (NumberFormatException e) {
			throw new IOException("Invalid index: " + index);
		}
		req.findAncestorObject(Job.class).checkPermission(Run.UPDATE);
		Run run = req.findAncestorObject(Run.class);
		if (run != null) {
			List<Action> actions = run.getActions();
			List<Action> groovyActions = run.getActions(type);
			if(idx < 0 || idx >= groovyActions.size()) {
				throw new IOException("Index out of range: " + idx);
			}
			actions.remove(groovyActions.get(idx));
			run.save();
			rsp.sendRedirect(req.getRequestURI().substring(0, req.getRequestURI().indexOf("parent/parent")));
		}
	}
}
