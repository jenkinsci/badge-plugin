/*
 * The MIT License
 * 
 * Copyright (c) 2014 IKEDA Yasuyuki
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

import static org.junit.Assert.*;
import hudson.matrix.AxisList;
import hudson.matrix.Combination;
import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixProject;
import hudson.matrix.TextAxis;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval;
import org.jenkinsci.plugins.scriptsecurity.scripts.languages.GroovyLanguage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 *
 */
public class GroovyPostbuildRecorderTest {
    @Rule
    public GroovyPostbuildJenkinsRule j = new GroovyPostbuildJenkinsRule();
    
    private static final String SCRIPT_FOR_MATRIX = StringUtils.join(new String[]{
            "import hudson.matrix.MatrixBuild;",
            "import hudson.matrix.MatrixRun;",
            "if (manager.build instanceof MatrixBuild) {",
            "  // codes for matrix parents.",
            "  manager.addShortText(\"parent\");",
            "} else if(manager.build instanceof MatrixRun) {",
            "  // codes for matrix children.",
            "  manager.addShortText(manager.build.buildVariables[\"axis1\"]);",
            "} else {",
            "  // unexpected case.",
            "  manager.buildFailure();",
            "}"
    }, '\n');

    @Before
    public void setUp() throws Exception {
        ScriptApproval.get().preapprove(SCRIPT_FOR_MATRIX, GroovyLanguage.get());
    }
    
    @Test
    public void testMatrixProjectWithParent() throws Exception {
        MatrixProject p = j.createMatrixProject();
        AxisList axisList = new AxisList(new TextAxis("axis1", "value1", "value2"));
        p.setAxes(axisList);
        p.getPublishersList().add(new GroovyPostbuildRecorder(SCRIPT_FOR_MATRIX, null, 2, true));
        
        MatrixBuild b = p.scheduleBuild2(0).get();
        j.assertBuildStatusSuccess(b);
        
        assertEquals("parent", b.getAction(GroovyPostbuildAction.class).getText());
        assertEquals("value1", b.getRun(new Combination(axisList, "value1")).getAction(GroovyPostbuildAction.class).getText());
        assertEquals("value2", b.getRun(new Combination(axisList, "value2")).getAction(GroovyPostbuildAction.class).getText());
    }
    
    @Test
    public void testMatrixProjectWithoutParent() throws Exception {
        MatrixProject p = j.createMatrixProject();
        AxisList axisList = new AxisList(new TextAxis("axis1", "value1", "value2"));
        p.setAxes(axisList);
        p.getPublishersList().add(new GroovyPostbuildRecorder(SCRIPT_FOR_MATRIX, null, 2, false));
        
        MatrixBuild b = p.scheduleBuild2(0).get();
        j.assertBuildStatusSuccess(b);
        
        assertNull(b.getAction(GroovyPostbuildAction.class));
        assertEquals("value1", b.getRun(new Combination(axisList, "value1")).getAction(GroovyPostbuildAction.class).getText());
        assertEquals("value2", b.getRun(new Combination(axisList, "value2")).getAction(GroovyPostbuildAction.class).getText());
    }
}
