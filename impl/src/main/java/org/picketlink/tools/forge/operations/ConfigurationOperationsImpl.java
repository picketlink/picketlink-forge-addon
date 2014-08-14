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
package org.picketlink.tools.forge.operations;

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.configuration.facets.ConfigurationFacet;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.picketlink.config.SecurityConfigurationBuilder;
import org.picketlink.event.SecurityConfigurationEvent;
import org.picketlink.tools.forge.ConfigurationOperations;
import org.picketlink.tools.forge.ui.idm.IdentityStoreType;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.util.Set;

import static org.picketlink.tools.forge.ConfigurationOperations.Properties.PICKETLINK_IDENTITY_STORE_TYPE;
import static org.picketlink.tools.forge.ConfigurationOperations.Properties.PICKETLINK_TOP_LEVEL_PACKAGE_NAME;

/**
 * @author Pedro Igor
 */
public class ConfigurationOperationsImpl implements ConfigurationOperations {

    private static final String SECURITY_CONFIGURATION_CLASS_NAME = "SecurityConfiguration";
    private static final String RESOURCE_PRODUCER_CLASS_NAME = "Resources";

    @Inject
    private AttributedTypeOperations attributedTypeManager;

    @Inject
    private PersistenceOperations persistenceOperations;

    @Override
    public JavaResource newIdentityManagementConfiguration(Project selectedProject) {
        newConfiguration(selectedProject, "IdentityManagementConfiguration", createIdentityManagementConfiguration(selectedProject));
        Configuration configuration = getConfiguration(selectedProject);
        String identityStoreType = configuration.getString(PICKETLINK_IDENTITY_STORE_TYPE.name(), IdentityStoreType.jpa.name());

        if (IdentityStoreType.jpa.name().equals(identityStoreType)) {
            newResourceProducer(selectedProject);
            this.persistenceOperations.configure(selectedProject, configuration.getBoolean(Properties.PICKETLINK_IDENTITY_WITHOUT_BASIC_IDENTITY_MODEL.name()));
        }

        return null;
    }

    private void newConfiguration(Project selectedProject, String typeName, StringBuilder configuration) {
        StringBuilder methodBodyBuilder = new StringBuilder();

        if (configuration.length() > 0) {
            methodBodyBuilder
                .append("SecurityConfigurationBuilder builder = event.getBuilder();\n")
                .append("\n")
                .append("builder\n")
                .append(configuration.toString());

            if (methodBodyBuilder.charAt(methodBodyBuilder.length() - 1) != ';') {
                methodBodyBuilder.append(";");
            }
        }

        newConfigurationResource(selectedProject, typeName, methodBodyBuilder);
    }

    @Override
    public JavaResource newConfiguration(Project selectedProject, StringBuilder config) {
        newConfiguration(selectedProject, "HttpSecurityConfiguration", config);
        return null;
    }

    private JavaResource newResourceProducer(Project selectedProject) {
        Configuration configuration = getConfiguration(selectedProject);

        String identityStoreType = configuration.getString(PICKETLINK_IDENTITY_STORE_TYPE.name(), IdentityStoreType.jpa.name());

        if (!IdentityStoreType.jpa.name().equals(identityStoreType)) {
            return null;
        }

        JavaSourceFacet javaFacet = selectedProject.getFacet(JavaSourceFacet.class);

        String topLevelPackageName = getConfiguration(selectedProject).getString(PICKETLINK_TOP_LEVEL_PACKAGE_NAME.name(), DEFAULT_TOP_LEVEL_PACKAGE);
        JavaResource javaResource = javaFacet
            .getSourceDirectory()
            .getOrCreateChildDirectory(topLevelPackageName.replace('.', File.separatorChar))
            .getChildOfType(JavaResource.class, RESOURCE_PRODUCER_CLASS_NAME + ".java");
        String packageName = javaFacet.calculatePackage(javaResource);

        JavaClassSource javaSource = Roaster.create(JavaClassSource.class)
            .setName(RESOURCE_PRODUCER_CLASS_NAME)
            .setPublic()
            .setPackage(packageName);

        FieldSource<JavaClassSource> entityManagerProducerField = javaSource
            .addField()
            .setPrivate()
            .setType(EntityManager.class)
            .setName("entityManager");

        entityManagerProducerField.addAnnotation(PersistenceContext.class);
        entityManagerProducerField.addAnnotation(Produces.class);

        javaResource.setContents(javaSource);

        return javaResource;
    }

    private StringBuilder createIdentityManagementConfiguration(Project selectedProject) {
        StringBuilder config = new StringBuilder();
        Configuration configuration = getConfiguration(selectedProject);
        String identityStoreType = configuration.getString(PICKETLINK_IDENTITY_STORE_TYPE.name(), IdentityStoreType.jpa.name());

        Set<String> attributedTypes = this.attributedTypeManager.getAttributedTypes(selectedProject);

        if (!attributedTypes.isEmpty()) {
            config
                .append("\t.idmConfig()\n")
                .append("\t\t.named(\"default.config\")\n")
                .append("\t\t\t.stores()\n")
                .append("\t\t\t\t.").append(identityStoreType).append("()\n");

            config.append("\t\t\t\t\t.supportType(");

            int i = 0;

            for (String attributedTypeName : attributedTypes) {
                if (i > 0) {
                    config.append(",\n");
                }

                config.append(attributedTypeName).append(".class");

                i++;
            }

            config.append("\t\t\t\t\t)\n");

            config.append("\t\t\t\t\t.supportAllFeatures()");
        }

        return config;
    }

    private JavaResource newConfigurationResource(Project selectedProject, String typeName, StringBuilder observerMethodBody) {
        StringBuilder methodBody = observerMethodBody;

        if (methodBody.length() == 0) {
            return null;
        }

        JavaSourceFacet javaFacet = selectedProject.getFacet(JavaSourceFacet.class);

        Configuration configuration = getConfiguration(selectedProject);

        if (methodBody.length() > 0) {
            String topLevelPackageName = configuration.getString(PICKETLINK_TOP_LEVEL_PACKAGE_NAME.name(), DEFAULT_TOP_LEVEL_PACKAGE);
            JavaResource javaResource = javaFacet
                .getSourceDirectory()
                .getOrCreateChildDirectory(topLevelPackageName.replace('.', File.separatorChar))
                .getChildOfType(JavaResource.class, typeName + ".java");
            String packageName = javaFacet.calculatePackage(javaResource);

            JavaClassSource javaSource = Roaster.create(JavaClassSource.class)
                .setName(typeName)
                .setPublic()
                .setPackage(packageName);

            if (methodBody.length() > 0) {
                javaSource.addImport(SecurityConfigurationBuilder.class);

                javaSource
                    .addMethod()
                    .setName("onInit")
                    .setPublic()
                    .setReturnTypeVoid()
                    .setBody(methodBody.toString())
                    .addParameter(SecurityConfigurationEvent.class, "event")
                    .addAnnotation(Observes.class);
            }

            javaResource.setContents(javaSource);
        }

        return null;
    }

    private Configuration getConfiguration(Project selectedProject) {
        ConfigurationFacet configurationFacet = selectedProject.getFacet(ConfigurationFacet.class);
        return configurationFacet.getConfiguration();
    }
}
