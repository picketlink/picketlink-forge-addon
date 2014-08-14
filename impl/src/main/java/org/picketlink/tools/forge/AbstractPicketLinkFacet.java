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

import org.apache.maven.model.Model;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.DependencyFacet;

import javax.inject.Inject;

import static org.picketlink.tools.forge.MavenDependencies.BASE_MODULE_REQUIRED_DEPENDENCIES;
import static org.picketlink.tools.forge.MavenDependencies.PICKETLINK_BOM_DEPENDENCY;
import static org.picketlink.tools.forge.MavenDependencies.PICKETLINK_VERSION_MAVEN_PROPERTY;

/**
 * @author Pedro Igor
 */
public abstract class AbstractPicketLinkFacet extends AbstractFacet<Project> implements PicketLinkFacet {

    @Inject
    private DependencyInstaller dependencyInstaller;

    @Override
    public boolean install() {
        installMavenDependencies();
        return true;
    }

    @Override
    public boolean isInstalled() {
        return hasRequiredMavenDependencies();
    }

    @Override
    public void setPicketLinkVersion(String version) {
        MavenFacet mavenFacet = getMavenFacet();
        Model model = mavenFacet.getModel();

        model.getProperties().put(PICKETLINK_VERSION_MAVEN_PROPERTY, version);

        // need that to force the update of the properties section of the pom
        mavenFacet.setModel(model);
    }

    @Override
    public String getPicketLinkVersion() {
        MavenFacet mavenFacet = getMavenFacet();
        Model model = mavenFacet.getModel();
        Object version = model.getProperties().get(PICKETLINK_VERSION_MAVEN_PROPERTY);

        if (version == null) {
            throw new IllegalStateException("No version was set of PicketLink Facet [" + this + "].");
        }

        return version.toString();
    }

    @Override
    public String toString() {
        return getFacetDescription();
    }

    /**
     * <p>Each facet must provide a short description to be displayed when choosing a PicketLink Module to be configured.</p>
     *
     * @return
     */
    protected abstract String getFacetDescription();

    /**
     * <p>Returns all required dependencies for this facet.</p>
     *
     * @return
     */
    protected Dependency[] getRequiredDependencies() {
        return BASE_MODULE_REQUIRED_DEPENDENCIES;
    }

    private boolean hasRequiredMavenDependencies() {
        DependencyFacet dependencyFacet = getFaceted().getFacet(DependencyFacet.class);

        for (Dependency requiredDependency : getRequiredDependencies()) {
            if (!dependencyFacet.hasEffectiveDependency(requiredDependency)) {
                return false;
            }
        }

        return true;
    }

    private void installMavenDependencies() {
        this.dependencyInstaller.installManaged(getFaceted(), PICKETLINK_BOM_DEPENDENCY);

        for (Dependency requiredDependency : getRequiredDependencies()) {
            this.dependencyInstaller.install(getFaceted(), requiredDependency);
        }
    }

    private MavenFacet getMavenFacet() {
        return getFaceted().getFacet(MavenFacet.class);
    }
}
