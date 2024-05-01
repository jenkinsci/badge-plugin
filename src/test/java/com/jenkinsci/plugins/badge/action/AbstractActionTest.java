package com.jenkinsci.plugins.badge.action;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import hudson.markup.RawHtmlMarkupFormatter;
import org.junit.jupiter.api.Test;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

class AbstractActionTest {

    @Test
    void isJenkinsSymbolRef() {
        assertTrue(AbstractAction.isJenkinsSymbolRef("symbol-information-circle-outline"));
        assertFalse(AbstractAction.isJenkinsSymbolRef("info.gif"));
    }

    @Test
    void getJenkinsSymbolIconPath() {
        assertNull(AbstractAction.getJenkinsSymbolIconPath(null));
        assertEquals(AbstractAction.getJenkinsSymbolIconPath("symbol-information-circle-outline"), "symbol-information-circle-outline plugin-ionicons-api");
        assertEquals(AbstractAction.getJenkinsSymbolIconPath("symbol-information-circle-outline plugin-ionicons-api"), "symbol-information-circle-outline plugin-ionicons-api");
        assertEquals(AbstractAction.getJenkinsSymbolIconPath("info.gif"), "info.gif");
    }

    @Test
    void getJenkinsColorClass() {
        assertNull(AbstractAction.getJenkinsColorClass(null));
        assertNull(AbstractAction.getJenkinsColorClass(""));
        assertNull(AbstractAction.getJenkinsColorClass("teal"));
        assertNull(AbstractAction.getJenkinsColorClass("burlywood"));
        assertNull(AbstractAction.getJenkinsColorClass("light-"));
        assertNull(AbstractAction.getJenkinsColorClass("dark-"));
        assertEquals(AbstractAction.getJenkinsColorClass("blue"), "jenkins-!-color-blue");
        assertEquals(AbstractAction.getJenkinsColorClass("light-blue"), "jenkins-!-color-light-blue");
        assertEquals(AbstractAction.getJenkinsColorClass("dark-blue"), "jenkins-!-color-dark-blue");
        assertEquals(AbstractAction.getJenkinsColorClass("danger"), "jenkins-!-danger-color");
    }

}