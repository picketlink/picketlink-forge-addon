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
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.result.navigation.NavigationResultBuilder;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.picketlink.tools.forge.ConfigurationOperations;
import org.picketlink.tools.forge.PicketLinkBaseFacet;

import javax.inject.Inject;
import java.util.ArrayList;

/**
 * @author Pedro Igor
 */
@FacetConstraint(PicketLinkBaseFacet.class)
public class ScaffoldSetupCommand extends AbstractProjectCommand implements UIWizard {

    public static final String JSF_PROJECT_WITH_FORM_BASED_AUTHENTICATION_AND_LOGOUT = "JSF Project with Form-based Authentication and Logout";
    @Inject
    private ProjectFactory projectFactory;

    @Inject @WithAttributes(label = "Template", required = true, description = "Select a template")
    private UISelectOne<String> scaffolds;

    @Override
    public UICommandMetadata getMetadata(UIContext context) {
        return Metadata.forCommand(ScaffoldSetupCommand.class)
            .name("PicketLink Scaffold: Setup")
            .description("Provides some useful templates for your project.")
            .category(Categories.create("picketlink"));
    }

    @Override
    public void initializeUI(UIBuilder builder) throws Exception {
        ArrayList<String> values = new ArrayList<String>();

        values.add(JSF_PROJECT_WITH_FORM_BASED_AUTHENTICATION_AND_LOGOUT);

        this.scaffolds.setValueChoices(values);

        builder.add(this.scaffolds);
    }

    @Override
    public Result execute(UIExecutionContext context) throws Exception {
        return Results.success("Scaffold was setup successfully.");
    }

    @Override
    public boolean isEnabled(UIContext context) {
        if (super.isEnabled(context)) {
            Project selectedProject = getSelectedProject(context);
            Configuration configuration = selectedProject.getFacet(ConfigurationFacet.class).getConfiguration();

            return configuration.getBoolean(ConfigurationOperations.Properties.PICKETLINK_SCAFFOLD_PROJECT.name(), true);
        }

        return false;
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
        Project selectedProject = getSelectedProject(context);

        if (selectedProject != null) {
            if (this.scaffolds.getValue().equals(JSF_PROJECT_WITH_FORM_BASED_AUTHENTICATION_AND_LOGOUT)) {
                builder.add(JSFFormBasedAuthenticationScaffoldSetupCommand.class);
            }
        }

        return builder.build();
    }
}
