package com.jenkinsci.plugins.badge.action;

import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import static org.junit.jupiter.api.Assertions.*;

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
