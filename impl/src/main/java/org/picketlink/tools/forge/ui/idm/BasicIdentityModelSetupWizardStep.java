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
package org.picketlink.tools.forge.ui.idm;

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.configuration.facets.ConfigurationFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.javaee.ejb.ui.EJBSetupWizard;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.picketlink.tools.forge.ConfigurationOperations;
import org.picketlink.tools.forge.MavenDependencies;
import org.picketlink.tools.forge.PicketLinkIDMFacet;

import javax.inject.Inject;

import static org.picketlink.tools.forge.util.ResourceUtil.createSecurityInitializerIfNecessary;

/**
 * @author Pedro Igor
 */
@FacetConstraint(value = PicketLinkIDMFacet.class)
public class BasicIdentityModelSetupWizardStep extends AbstractProjectCommand implements UIWizardStep {

    @Inject
    private ProjectFactory projectFactory;

    @Inject
    private DependencyInstaller dependencyInstaller;

    @Override
    public void initializeUI(UIBuilder builder) throws Exception {
    }

    @Override
    public Result execute(UIExecutionContext context) throws Exception {
        Project selectedProject = getSelectedProject(context);
        Configuration configuration = selectedProject.getFacet(ConfigurationFacet.class).getConfiguration();
        Boolean withoutBasicIdentityModel = configuration.getBoolean(ConfigurationOperations.Properties.PICKETLINK_IDENTITY_WITHOUT_BASIC_IDENTITY_MODEL.name());

        if (!withoutBasicIdentityModel) {
            createSecurityInitializerIfNecessary(selectedProject);

            String identityStoreType = configuration.getString(ConfigurationOperations.Properties.PICKETLINK_IDENTITY_STORE_TYPE.name());

            if (IdentityStoreType.jpa.name().equals(identityStoreType)) {
                this.dependencyInstaller.install(selectedProject, MavenDependencies.PICKETLINK_IDM_SIMPLE_SCHEMA_DEPENDENCY);
            }

            return Results.success("Basic Identity Model has been configured.");
        } else {
            DependencyFacet dependencyFacet = selectedProject.getFacet(DependencyFacet.class);

            dependencyFacet.removeDependency(MavenDependencies.PICKETLINK_IDM_SIMPLE_SCHEMA_DEPENDENCY);

            JavaSourceFacet javaSourceFacet = selectedProject.getFacet(JavaSourceFacet.class);

            String securityPackage = configuration.getString(ConfigurationOperations.Properties.PICKETLINK_TOP_LEVEL_PACKAGE_NAME.name());
            DirectoryResource securitySourceDir = javaSourceFacet.getSourceDirectory()
                .getOrCreateChildDirectory(securityPackage.replace('.', '/'));
            Resource<?> securityInitializer = securitySourceDir.getChild("SecurityInitializer.java");

            if (securityInitializer.exists()) {
                securityInitializer.delete();
            }
        }

        return Results.success();
    }

    @Override
    protected boolean isProjectRequired() {
        return true;
    }

    @Override
    protected ProjectFactory getProjectFactory() {
        return this.projectFactory;
    }

    @Override
    public NavigationResult next(UINavigationContext context) throws Exception {
        return context.navigateTo(EJBSetupWizard.class);
    }
}
