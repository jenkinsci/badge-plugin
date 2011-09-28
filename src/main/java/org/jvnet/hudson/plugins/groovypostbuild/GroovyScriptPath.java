package org.jvnet.hudson.plugins.groovypostbuild;

import java.io.File;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author <a href="mailto:nicolas.deloof@cloudbees.com">Nicolas De loof</a>
 */
public class GroovyScriptPath {

    private final File path;

    @DataBoundConstructor
    public GroovyScriptPath(String path) {
        this.path = new File(path).getAbsoluteFile();
    }

    public File getPath() {
        return path;
    }
}
