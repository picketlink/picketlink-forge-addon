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

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.DependencyFacet;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Pedro Igor
 */
public abstract class AbstractPicketLinkSetupCommandTestCase extends AbstractTestCase {

    @Inject
    protected MavenDependencies mavenDependencies;

    protected void assertCommandResult(Coordinate expectedVersion, String packageName) throws Exception {
        Project selectedProject = getSelectedProject();
        PicketLinkBaseFacet picketlinkFacet = selectedProject.getFacet(PicketLinkBaseFacet.class);

        assertEquals(expectedVersion.getVersion(), picketlinkFacet.getPicketLinkVersion());

        PicketLinkIDMFacet picketLinkIDMFacet = selectedProject.getFacet(PicketLinkIDMFacet.class);

        assertEquals(picketlinkFacet.getPicketLinkVersion(), picketLinkIDMFacet.getPicketLinkVersion());

        DependencyFacet dependencyFacet = selectedProject.getFacet(DependencyFacet.class);

        assertTrue(dependencyFacet.hasDirectManagedDependency(MavenDependencies.PICKETLINK_BOM_DEPENDENCY));
        assertTrue(dependencyFacet.hasEffectiveDependency(MavenDependencies.PICKETLINK_API_DEPENDENCY));
        assertTrue(dependencyFacet.hasEffectiveDependency(MavenDependencies.PICKETLINK_IMPL_DEPENDENCY));
        assertTrue(dependencyFacet.hasEffectiveDependency(MavenDependencies.PICKETLINK_IDM_API_DEPENDENCY));
        assertTrue(dependencyFacet.hasEffectiveDependency(MavenDependencies.PICKETLINK_IDM_IMPL_DEPENDENCY));
    }

    protected Coordinate getLatestVersion() {
        return this.mavenDependencies.resolveLatestVersion();
    }
}
