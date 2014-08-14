/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.picketlink.tools.forge;

import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.junit.After;
import org.junit.Before;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * @author Pedro Igor
 */
public abstract class AbstractTestCase {

    @Inject
    private ShellTest shellTest;

    @Inject
    private ProjectFactory projectFactory;

    private Project selectedProject;

    @Before
    public void onBefore() throws Exception {
        this.selectedProject = this.projectFactory.createTempProject(Arrays.<Class<? extends ProjectFacet>>asList(JavaSourceFacet.class));
        this.shellTest.getShell().setCurrentResource(this.selectedProject.getRoot());
    }

    @After
    public void onAfter() throws Exception {
        this.shellTest.clearScreen();
    }

    protected Result executeShellCommand(String command) {
        try {
            Result result = getShellTest()
                .execute((command),
                    100000,
                    TimeUnit.SECONDS);

            this.selectedProject = this.projectFactory.findProject(this.selectedProject.getRoot());

            return result;
        } catch (TimeoutException e) {
            fail("Command [" + command + "] timeout.");
        }

        return null;
    }

    protected void assertSuccessfulResult(Result result) {
        assertFalse(Failed.class.isInstance(result));
    }

    protected ShellTest getShellTest() {
        return this.shellTest;
    }

    protected ProjectFactory getProjectFactory() {
        return this.projectFactory;
    }

    protected Project getSelectedProject() {
        return this.selectedProject;
    }
}
