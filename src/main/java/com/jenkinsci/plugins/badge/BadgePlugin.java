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
package com.jenkinsci.plugins.badge;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.PersistentDescriptor;
import jenkins.model.GlobalConfiguration;
import jenkins.model.GlobalConfigurationCategory;
import org.kohsuke.stapler.DataBoundSetter;

@Extension
public class BadgePlugin extends GlobalConfiguration implements PersistentDescriptor {

    private boolean disableFormatHTML;

    /**
     * @return the singleton instance
     */
    public static @NonNull BadgePlugin get() {
        return GlobalConfiguration.all().getInstance(BadgePlugin.class);
    }

    @Override
    public @NonNull GlobalConfigurationCategory getCategory() {
        return GlobalConfigurationCategory.get(GlobalConfigurationCategory.Security.class);
    }

    /**
     * @return whether HTML formatting is disabled or not
     */
    public boolean isDisableFormatHTML() {
        return disableFormatHTML;
    }

    /**
     * Together with {@link #isDisableFormatHTML}, binds to entry in {@code config.jelly}.
     *
     * @param disableFormatHTML the new value of this field
     */
    @DataBoundSetter
    public void setDisableFormatHTML(boolean disableFormatHTML) {
        this.disableFormatHTML = disableFormatHTML;
        save();
    }
}
