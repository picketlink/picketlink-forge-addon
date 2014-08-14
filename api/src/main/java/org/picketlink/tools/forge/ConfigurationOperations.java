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

import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;

/**
 * @author Pedro Igor
 */
public interface ConfigurationOperations {

    public static final String DEFAULT_TOP_LEVEL_PACKAGE = "security";

    /**
     * <p>Creates a {@link org.jboss.forge.addon.parser.java.resources.JavaResource} providing all the necessary
     * configuration.</p>
     *
     * @param selectedProject
     * @return
     */
    JavaResource newIdentityManagementConfiguration(Project selectedProject);

    /**
     * <p>Creates a {@link org.jboss.forge.addon.parser.java.resources.JavaResource} providing all the necessary configuration.
     * This method also expects a specific configuration.</p>
     *  @param selectedProject
     * @param configuration
     */
    JavaResource newConfiguration(Project selectedProject, StringBuilder configuration);

    public enum Properties {

        PICKETLINK_IDENTITY_WITHOUT_BASIC_IDENTITY_MODEL,
        PICKETLINK_IDENTITY_STORE_TYPE,
        PICKETLINK_SCAFFOLD_PROJECT,
        PICKETLINK_TOP_LEVEL_PACKAGE_NAME
    }
}
