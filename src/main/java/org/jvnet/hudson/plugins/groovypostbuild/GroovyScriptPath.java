package org.jvnet.hudson.plugins.groovypostbuild;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;

/**
 * @author <a href="mailto:nicolas.deloof@cloudbees.com">Nicolas De loof</a>
 */
public class GroovyScriptPath extends AbstractDescribableImpl<GroovyScriptPath> {

    private final File path;

    @DataBoundConstructor
    public GroovyScriptPath(String path) {
        this.path = new File(path).getAbsoluteFile();
    }

    public File getPath() {
        return path;
    }

    @Extension
    public static class GroovyScriptPathDescriptor extends Descriptor<GroovyScriptPath> {

        @Override
        public String getDisplayName() {
            return "";
        }
    }
}
