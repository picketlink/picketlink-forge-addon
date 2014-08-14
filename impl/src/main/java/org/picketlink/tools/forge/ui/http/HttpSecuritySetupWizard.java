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
package org.picketlink.tools.forge.ui.http;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.javaee.servlet.ui.ServletSetupWizard;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.command.PrerequisiteCommandsProvider;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.result.navigation.NavigationResultBuilder;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.picketlink.tools.forge.PicketLinkBaseFacet;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * @author Pedro Igor
 */
@FacetConstraint(value = PicketLinkBaseFacet.class)
public class HttpSecuritySetupWizard extends AbstractProjectCommand implements UIWizard, PrerequisiteCommandsProvider {

    @Inject
    private ProjectFactory projectFactory;

    @Inject @WithAttributes(label = "Authentication Scheme", required = true, description = "Select the HTTP Authentication Scheme")
    private UISelectOne<AuthenticationScheme> authcScheme;

    @Override
    public void initializeUI(UIBuilder builder) throws Exception {
        this.authcScheme.setValueChoices(Arrays.asList(AuthenticationScheme.values()));
        this.authcScheme.setDefaultValue(AuthenticationScheme.form);
        builder.add(this.authcScheme);
    }

    @Override
    public Result execute(UIExecutionContext context) throws Exception {
        return Results.success("Http Security has been enabled. Using [" + this.authcScheme.getValue() + "] authentication scheme.");
    }

    @Override
    protected ProjectFactory getProjectFactory() {
        return this.projectFactory;
    }

    @Override
    protected boolean isProjectRequired() {
        return true;
    }

    @Override
    public NavigationResult next(UINavigationContext context) throws Exception {
        if (AuthenticationScheme.form.equals(this.authcScheme.getValue())) {
            return context.navigateTo(FormAuthenticationSchemeWizardStep.class);
        }

        if (AuthenticationScheme.basic.equals(this.authcScheme.getValue())) {
            return context.navigateTo(BasicAuthenticationSchemeWizardStep.class);
        }

        if (AuthenticationScheme.digest.equals(this.authcScheme.getValue())) {
            return context.navigateTo(DigestAuthenticationSchemeWizardStep.class);
        }

        return null;
    }

    @Override
    public NavigationResult getPrerequisiteCommands(UIContext context) {
        NavigationResultBuilder builder = NavigationResultBuilder.create();

        builder.add(ServletSetupWizard.class);

        return builder.build();
    }
}
