package org.picketlink.tools.forge.ui.idm;

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.configuration.facets.ConfigurationFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.facets.constraints.FacetConstraints;
import org.jboss.forge.addon.javaee.jpa.JPAFacet;
import org.jboss.forge.addon.javaee.jpa.ui.setup.JPASetupWizard;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.result.navigation.NavigationResultBuilder;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.picketlink.tools.forge.ConfigurationOperations;
import org.picketlink.tools.forge.PicketLinkBaseFacet;

import javax.inject.Inject;
import java.util.concurrent.Callable;

import static java.util.Arrays.asList;

/**
 * <p>This command is used to properly configure the project before any other command is executed. It provides all the necessary
 * configuration in order to properly enable PicketLink to a project.</p>
 *
 * @author Pedro Igor
 */
@FacetConstraints(value = {
    @FacetConstraint(value = PicketLinkBaseFacet.class),
    @FacetConstraint(value = JavaSourceFacet.class)
})
public class IdentityManagementSetupWizard extends AbstractProjectCommand implements UIWizard {

    private static final String DEFAULT_IDENTITY_CONFIGURATION_NAME = "default.config";

    @Inject
    private ProjectFactory projectFactory;

    @Inject
    @WithAttributes(label = "Identity Store Type", required = true, description = "The identity store to be used")
    private UISelectOne<IdentityStoreType> identityStoreType;

    @Inject
    @WithAttributes(label = "Do not configure Basic Identity Model.", required = true, description = "Indicates if the project should not use the Basic Identity Model.", defaultValue = "false")
    private UIInput<Boolean> withoutBasicIdentityModel;

    @Override
    public void initializeUI(final UIBuilder builder) throws Exception {
        final Project selectedProject = getSelectedProject(builder.getUIContext());

        this.identityStoreType.setValueChoices(asList(IdentityStoreType.values()));

        this.identityStoreType.setDefaultValue(new Callable<IdentityStoreType>() {
            @Override
            public IdentityStoreType call() throws Exception {
                String projectIdentityStoreType = getProjectIdentityStoreType(selectedProject);

                if (projectIdentityStoreType != null) {
                    return IdentityStoreType.valueOf(projectIdentityStoreType);
                }

                return IdentityStoreType.jpa;
            }
        });

        this.identityStoreType.setEnabled(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return getProjectIdentityStoreType(selectedProject) == null;
            }
        });

        builder.add(this.identityStoreType);

        builder.add(this.withoutBasicIdentityModel);
    }

    @Override
    public Result execute(UIExecutionContext context) throws Exception {
        Project selectedProject = getSelectedProject(context.getUIContext());
        Configuration configuration = getConfiguration(selectedProject);

        configuration.setProperty(ConfigurationOperations.Properties.PICKETLINK_IDENTITY_STORE_TYPE.name(), this.identityStoreType.getValue().name());
        configuration.setProperty(ConfigurationOperations.Properties.PICKETLINK_IDENTITY_WITHOUT_BASIC_IDENTITY_MODEL.name(), this.withoutBasicIdentityModel.getValue());

        return Results.success("PicketLink Identity Management has been installed. Using Identity Store [" + getProjectIdentityStoreType(selectedProject) + "].");
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

        if (selectedProject.hasFacet(PicketLinkBaseFacet.class)) {
            if (IdentityStoreType.jpa.equals(this.identityStoreType.getValue())) {
                String projectIdentityStoreType = getProjectIdentityStoreType(selectedProject);
                boolean isJpaStoreSelected = projectIdentityStoreType == null || IdentityStoreType.jpa.name().equals(projectIdentityStoreType);

                if (isJpaStoreSelected) {
                    if (!selectedProject.hasFacet(JPAFacet.class)) {
                        builder.add(JPASetupWizard.class);
                    }

                    builder.add(JPAIdentityStoreSetupWizardStep.class);
                }
            }

            builder.add(BasicIdentityModelSetupWizardStep.class);
        }

        return builder.build();
    }

    private Configuration getConfiguration(Project selectedProject) {
        return selectedProject.getFacet(ConfigurationFacet.class).getConfiguration();
    }
    private String getProjectIdentityStoreType(Project selectedProject) {
        return getConfiguration(selectedProject).getString(ConfigurationOperations.Properties.PICKETLINK_IDENTITY_STORE_TYPE.name());
    }
}