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
package org.picketlink.tools.forge.ui.scafolld;

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.configuration.facets.ConfigurationFacet;
import org.jboss.forge.addon.javaee.cdi.ui.CDISetupCommand;
import org.jboss.forge.addon.javaee.ejb.ui.EJBSetupWizard;
import org.jboss.forge.addon.javaee.faces.ui.FacesSetupWizard;
import org.jboss.forge.addon.javaee.servlet.ui.ServletSetupWizard;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.scaffold.ui.ScaffoldSetupWizard;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.result.navigation.NavigationResultBuilder;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.picketlink.tools.forge.ConfigurationOperations;
import org.picketlink.tools.forge.ui.SetupWizard;
import org.picketlink.tools.forge.ui.idm.IdentityManagementSetupWizard;

import javax.inject.Inject;

import static org.picketlink.tools.forge.util.ResourceUtil.createJavaResourceIfNecessary;
import static org.picketlink.tools.forge.util.ResourceUtil.createSecurityInitializerIfNecessary;
import static org.picketlink.tools.forge.util.ResourceUtil.createWebResourceIfNecessary;

/**
 * @author Pedro Igor
 */
public class JSFFormBasedAuthenticationScaffoldSetupCommand extends AbstractProjectCommand implements UIWizardStep {

    @Inject
    private ProjectFactory projectFactory;

    @Override
    public void initializeUI(UIBuilder builder) throws Exception {

    }

    @Override
    public Result execute(UIExecutionContext context) throws Exception {
        applyTemplate(context);
        return Results.success("Templates successfully applied.");
    }

    private void applyTemplate(UIExecutionContext context) {
        Project selectedProject = getSelectedProject(context);

        createSecurityInitializerIfNecessary(selectedProject);
        createJavaResourceIfNecessary(selectedProject, "LoginController.java", "/scaffold/jsfformauthc/classes/LoginController.java");

        createWebResourceIfNecessary(selectedProject, "index.html", "/scaffold/jsfformauthc/index.html");
        createWebResourceIfNecessary(selectedProject, "home.xhtml", "/scaffold/jsfformauthc/home.xhtml");
        createWebResourceIfNecessary(selectedProject, "loginWithBean.xhtml", "/scaffold/jsfformauthc/loginWithBean.xhtml");
        createWebResourceIfNecessary(selectedProject, "protected/private.xhtml", "/scaffold/jsfformauthc/protected/private.xhtml");

        Configuration configuration = selectedProject.getFacet(ConfigurationFacet.class).getConfiguration();

        configuration.setProperty(ConfigurationOperations.Properties.PICKETLINK_SCAFFOLD_PROJECT.name(), true);
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
        NavigationResultBuilder builder = NavigationResultBuilder.create();

        builder.add(CDISetupCommand.class);
        builder.add(FacesSetupWizard.class);
        builder.add(EJBSetupWizard.class);
        builder.add(SetupWizard.class);
        builder.add(ServletSetupWizard.class);
        builder.add(IdentityManagementSetupWizard.class);
        builder.add(ScaffoldSetupWizard.class);

        return builder.build();
    }
}
