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
package org.picketlink.tools.forge.util;

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.configuration.facets.ConfigurationFacet;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.picketlink.tools.forge.ConfigurationOperations;

import java.io.File;

/**
 * @author Pedro Igor
 */
public class ResourceUtil {

    public static JavaResource createSecurityInitializerIfNecessary(Project selectedProject) {
        return createJavaResourceIfNecessary(selectedProject, "SecurityInitializer.java", "/scaffold/common/classes/SecurityInitializer.java");
    }

    public static void createWebResourceIfNecessary(Project selectedProject, String name, String resourcePath) {
        WebResourcesFacet resourcesFacet = selectedProject.getFacet(WebResourcesFacet.class);
        FileResource<?> webResource = resourcesFacet.getWebResource(name);

        if (!webResource.exists()) {
            webResource.createNewFile();
            webResource.setContents(ResourceUtil.class.getResourceAsStream(resourcePath));
        }
    }

    public static JavaResource createJavaResourceIfNecessary(Project selectedProject, String name, String resourcePath) {
        Configuration configuration = selectedProject.getFacet(ConfigurationFacet.class).getConfiguration();
        String securityPackageName = configuration.getString(ConfigurationOperations.Properties.PICKETLINK_TOP_LEVEL_PACKAGE_NAME.name());

        return createJavaResourceIfNecessary(selectedProject, securityPackageName, name, resourcePath);
    }

    public static JavaResource createJavaResourceIfNecessary(Project selectedProject, String packageName, String name, String resourcePath) {
        JavaSourceFacet javaFacet = selectedProject.getFacet(JavaSourceFacet.class);
        DirectoryResource basePackageDirectory = javaFacet.getSourceDirectory().getOrCreateChildDirectory(packageName.replace('.', File.separatorChar));
        JavaResource javaResource = basePackageDirectory.getChildOfType(JavaResource.class, name);

        if (!javaResource.exists()) {
            javaResource.createNewFile();
            javaResource.setContents(ResourceUtil.class.getResourceAsStream(resourcePath));
            javaResource.setContents(javaResource.getContents()
                .replaceAll("__package_name", packageName));
        }

        return javaResource;
    }

}
