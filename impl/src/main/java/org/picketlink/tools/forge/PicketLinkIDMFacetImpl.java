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

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.facets.constraints.FacetConstraints;
import org.jboss.forge.addon.maven.projects.MavenFacet;

import static org.picketlink.tools.forge.MavenDependencies.IDM_MODULE_REQUIRED_DEPENDENCIES;

/**
 * <p>Default implementation for {@link PicketLinkIDMFacet}.</p>
 *
 * @author Pedro Igor
 *
 * @see PicketLinkBaseFacet
 */
@FacetConstraints(
    value = {
        @FacetConstraint(value = MavenFacet.class)
    }
)
public class PicketLinkIDMFacetImpl extends AbstractPicketLinkFacet implements PicketLinkIDMFacet {

    @Override
    protected Dependency[] getRequiredDependencies() {
        return IDM_MODULE_REQUIRED_DEPENDENCIES;
    }

    @Override
    protected String getFacetDescription() {
        return "PicketLink IDM. This module provides enables Identity Management to your project.";
    }
}
