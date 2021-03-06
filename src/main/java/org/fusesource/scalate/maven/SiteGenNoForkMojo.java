/**
 * Copyright (C) 2009-2011 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fusesource.scalate.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * This goal functions the same as the 'sitegen' goal but does not fork the build and is suitable for attaching to the build lifecycle.
 *
 * @author <a href="http://macstrac.blogspot.com">James Strachan</a>
 *
 * @goal sitegen-no-fork
 * @phase verify
 * @requiresProject
 * @requiresDependencyResolution test
 */
public class SiteGenNoForkMojo extends AbstractMojo {

    /**
     * @required
     * @readonly
     * @parameter expression="${project}"
     */
    MavenProject project;

    /**
     * The directory Scalate will use to compile templates.
     *
     * @parameter expression="${project.build.directory}/sitegen-workdir"
     */
    File workingDirectory;

    /**
     * The directory where the website template files are located.
     *
     * @parameter expression="${project.basedir}/src"
     */
    File webappDirectory;

    /**
     * The directory where the website will be generated into.
     *
     * @parameter expression="${project.build.directory}/sitegen"
     */
    File targetDirectory;

    /**
     * Disable the sitegen goal.
     *
     * @parameter expression="${scalate.sitegen.skip}"
     */
    String skip = "false";

    /**
     * The test project classpath elements.
     *
     * @parameter expression="${project.testClasspathElements}"
     */
    List testClassPathElements;

    /**
     * Properties to pass into the templates.
     *
     * @parameter
     */
    Map<String,String> templateProperties;

    /**
     * The class name of the Boot class to use.
     *
     * @parameter
     */
    String bootClassName;

    public void execute() throws MojoExecutionException, MojoFailureException {
        // Use reflection to invoke since the the scala support class is compiled after the java classes.
        try {
            Object o = getClass().getClassLoader().loadClass("org.fusesource.scalate.maven.SiteGenNoForkMojoSupport").newInstance();
            Method apply = o.getClass().getMethod("apply", new Class[]{SiteGenNoForkMojo.class});
            apply.invoke(o, this);
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if( targetException instanceof MojoFailureException) {
                throw (MojoFailureException)targetException;
            }
            if( targetException instanceof MojoExecutionException) {
                throw (MojoExecutionException)targetException;
            }
            throw new MojoExecutionException("Unexpected failure.", e.getTargetException());
        } catch (Throwable e) {
            throw new MojoExecutionException("Unexpected failure.", e);
        }
    }
}
