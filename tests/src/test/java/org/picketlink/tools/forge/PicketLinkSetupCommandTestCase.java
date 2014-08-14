package org.picketlink.tools.forge;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.picketlink.tools.forge.ConfigurationOperations.DEFAULT_TOP_LEVEL_PACKAGE;

@RunWith(Arquillian.class)
public class PicketLinkSetupCommandTestCase extends AbstractPicketLinkSetupCommandTestCase {

    @Deployment
    @Dependencies({
        @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness"),
        @AddonDependency(name = "org.jboss.forge.addon:maven"),
        @AddonDependency(name = "org.jboss.forge.addon:javaee"),
        @AddonDependency(name = "org.picketlink.tools.forge:picketlink-forge-addon")
    })
    public static ForgeArchive deploy() {
        return ShrinkWrap
            .create(ForgeArchive.class)
            .addClass(AbstractPicketLinkSetupCommandTestCase.class)
            .addClass(AbstractTestCase.class)
            .addBeansXML()
            .addAsAddonDependencies(
                AddonDependencyEntry.create("org.jboss.forge.addon:shell-test-harness"),
                AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                AddonDependencyEntry.create("org.jboss.forge.addon:javaee"),
                AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                AddonDependencyEntry.create("org.picketlink.tools.forge:picketlink-forge-addon"));
    }

    @Test
    public void testSetupWithoutParameters() throws Exception {
        assertSuccessfulResult(
            executeShellCommand("picketlink-setup")
        );

        assertCommandResult(getLatestVersion(), null);
    }

    @Test
    public void testSetupWithVersion() throws Exception {
        Coordinate latestVersion = getLatestVersion();
        List<Coordinate> availableVersions = this.mavenDependencies.resolveVersions(false);
        Coordinate selectedVersion = null;

        for (Coordinate version : availableVersions) {
            if (!version.equals(latestVersion) && version.getVersion().contains("2.7.0")) {
                selectedVersion = version;
                break;
            }
        }

        assertNotNull(selectedVersion);

        assertSuccessfulResult(
            executeShellCommand("picketlink-setup --version " + selectedVersion.getVersion())
        );

        assertCommandResult(selectedVersion, null);
    }

    @Test
    public void testSetupWithSnapshotVersion() throws Exception {
        List<Coordinate> availableVersions = this.mavenDependencies.resolveVersions(true);
        Coordinate selectedVersion = null;

        for (Coordinate version : availableVersions) {
            if (version.getVersion().equals("2.7.0-SNAPSHOT")) {
                selectedVersion = version;
                break;
            }
        }

        assertNotNull(selectedVersion);

        assertSuccessfulResult(
            executeShellCommand("picketlink-setup --showSnapshots --version " + selectedVersion.getVersion())
        );

        assertCommandResult(selectedVersion, null);
    }

    @Test
    public void testSetupWithTopLevelPackage() throws Exception {
        String packageName = "custom";

        assertSuccessfulResult(
            executeShellCommand("picketlink-setup --topLevelPackage " + packageName)
        );

        assertCommandResult(getLatestVersion(), packageName);
    }

    @Override
    protected void assertCommandResult(Coordinate expectedVersion, String packageName) throws Exception {
        super.assertCommandResult(expectedVersion, packageName);
        JavaSourceFacet javaFacet = getSelectedProject().getFacet(JavaSourceFacet.class);

        if (packageName == null) {
            packageName = DEFAULT_TOP_LEVEL_PACKAGE;
        }

        JavaResource resourcesJava = javaFacet.getJavaResource((javaFacet.getBasePackage() + "." + packageName).replace('.', '/') + "/Resources.java");

//        assertTrue(resourcesJava.exists());

        JavaResource securityConfigJava = javaFacet
            .getJavaResource((javaFacet.getBasePackage() + "." + packageName).replace('.', '/') + "/SecurityConfiguration.java");

//        assertFalse(securityConfigJava.exists());
    }
}