/*
 * The MIT License
 *
 * Copyright (c) 2024, Badge Plugin Authors
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
package com.jenkinsci.plugins.badge.action;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class AbstractActionTest {

    @Test
    void isJenkinsSymbolRef(@SuppressWarnings("unused") JenkinsRule r) {
        assertTrue(AbstractAction.isJenkinsSymbolRef("symbol-cube"));
        assertFalse(AbstractAction.isJenkinsSymbolRef("info.gif"));
    }

    @Test
    void getJenkinsColorClass(@SuppressWarnings("unused") JenkinsRule r) {
        assertNull(AbstractAction.getJenkinsColorClass(null));
        assertNull(AbstractAction.getJenkinsColorClass(""));
        assertNull(AbstractAction.getJenkinsColorClass("teal"));
        assertNull(AbstractAction.getJenkinsColorClass("burlywood"));
        assertNull(AbstractAction.getJenkinsColorClass("light-"));
        assertNull(AbstractAction.getJenkinsColorClass("dark-"));
        assertEquals("jenkins-!-color-blue", AbstractAction.getJenkinsColorClass("blue"));
        assertEquals("jenkins-!-color-light-blue", AbstractAction.getJenkinsColorClass("light-blue"));
        assertEquals("jenkins-!-color-dark-blue", AbstractAction.getJenkinsColorClass("dark-blue"));
        assertEquals("jenkins-!-danger-color", AbstractAction.getJenkinsColorClass("danger"));
    }
}
