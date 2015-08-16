/*
 * The MIT License
 * 
 * Copyright (c) 2015 IKEDA Yasuyuki
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

import static org.junit.Assert.assertEquals;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;

import java.util.Collections;

import org.jenkinsci.plugins.scriptsecurity.sandbox.groovy.SecureGroovyScript;
import org.jenkinsci.plugins.scriptsecurity.scripts.ClasspathEntry;
import org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval;
import org.jenkinsci.plugins.scriptsecurity.scripts.languages.GroovyLanguage;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.recipes.WithPlugin;

/**
 * Tests requires Jenkins launched for each test methods.
 */
public class GroovyPostbuildRecorderAnnotatedTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();
    
    @Test
    @WithPlugin("dependee.hpi") // provides org.jenkinsci.plugins.dependencytest.dependee.Dependee.getValue() which returns "dependee".
    public void testDependencyToAnotherPlugin() throws Exception {
        final String SCRIPT =
                "import org.jenkinsci.plugins.dependencytest.dependee.Dependee;"
                + "manager.addShortText(Dependee.getValue());";
        // as Dependee.getValue isn't whitelisted, we need to approve that.
        ScriptApproval.get().preapprove(SCRIPT, GroovyLanguage.get());
        
        FreeStyleProject p = j.createFreeStyleProject();
        p.getPublishersList().add(
                new GroovyPostbuildRecorder(
                        new SecureGroovyScript(
                                SCRIPT,
                                false,
                                Collections.<ClasspathEntry>emptyList()
                        ),
                        2,
                        false
                )
        );
        
        FreeStyleBuild b = p.scheduleBuild2(0).get();
        j.assertBuildStatusSuccess(b);
        assertEquals("dependee", b.getAction(GroovyPostbuildAction.class).getText());
    }
}
