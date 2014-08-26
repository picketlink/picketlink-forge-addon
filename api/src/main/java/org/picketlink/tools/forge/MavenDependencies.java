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

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.DependencyResolver;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.dependencies.builder.DependencyQueryBuilder;
import org.jboss.forge.addon.dependencies.util.NonSnapshotDependencyFilter;

import javax.inject.Inject;
import java.util.List;

/**
 * <p>A set of constants for each PicketLink Maven Dependency.</p>
 *
 * @author Pedro Igor
 */
public class MavenDependencies {

    public static final String PICKETLINK_VERSION_MAVEN_PROPERTY = "version.picketlink";

    public static final Dependency PICKETLINK_BOM_DEPENDENCY =
        DependencyBuilder
            .create("org.picketlink:picketlink-javaee-6.0")
            .setScopeType("import")
            .setPackaging("pom")
            .setVersion("${" + PICKETLINK_VERSION_MAVEN_PROPERTY + "}");

    public static final Dependency PICKETLINK_DEPENDENCY =
        DependencyBuilder
            .create("org.picketlink:picketlink")
            .setScopeType("compile")
            .setPackaging("jar");

    public static final Dependency PICKETLINK_API_DEPENDENCY =
        DependencyBuilder
            .create("org.picketlink:picketlink-api")
            .setScopeType("compile")
            .setPackaging("jar");

    public static final Dependency PICKETLINK_IMPL_DEPENDENCY =
        DependencyBuilder
            .create("org.picketlink:picketlink-impl")
            .setScopeType("runtime")
            .setPackaging("jar");

    public static final Dependency PICKETLINK_IDM_API_DEPENDENCY =
        DependencyBuilder
            .create("org.picketlink:picketlink-idm-api")
            .setScopeType("compile")
            .setPackaging("jar");

    public static final Dependency PICKETLINK_IDM_IMPL_DEPENDENCY =
        DependencyBuilder
            .create("org.picketlink:picketlink-idm-impl")
            .setScopeType("runtime")
            .setPackaging("jar");

    public static final Dependency PICKETLINK_IDM_SIMPLE_SCHEMA_DEPENDENCY =
        DependencyBuilder
            .create("org.picketlink:picketlink-idm-simple-schema")
            .setScopeType("runtime")
            .setPackaging("jar");

    public static final Dependency[] BASE_MODULE_REQUIRED_DEPENDENCIES = new Dependency[]{PICKETLINK_DEPENDENCY};
    public static final Dependency[] IDM_MODULE_REQUIRED_DEPENDENCIES = new Dependency[]{PICKETLINK_DEPENDENCY};

    @Inject
    private DependencyResolver dependencyResolver;

    public List<Coordinate> resolveVersions(boolean showSnapshots) {
        DependencyQueryBuilder query = DependencyQueryBuilder.create(PICKETLINK_API_DEPENDENCY.getCoordinate().getGroupId() + ":" + PICKETLINK_API_DEPENDENCY.getCoordinate().getArtifactId());

        if (!showSnapshots) {
            query.setFilter(new NonSnapshotDependencyFilter());
        }

        return this.dependencyResolver.resolveVersions(query);
    }

    public Coordinate resolveLatestVersion() {
        List<Coordinate> availableVersions = resolveVersions(true);

        if (!availableVersions.isEmpty()) {
            Coordinate latestVersion = availableVersions.get(availableVersions.size() - 1);

            for (int i = availableVersions.size() - 1; i >= 0; i--) {
                String version = availableVersions.get(i).getVersion();

                if (version != null && version.toUpperCase().contains("2.7.0.Beta1")) {
                    latestVersion = availableVersions.get(i);
                    break;
                }
            }

            return latestVersion;
        }

        throw new IllegalStateException("Could not resolve latest version from maven repository.");
    }
}
