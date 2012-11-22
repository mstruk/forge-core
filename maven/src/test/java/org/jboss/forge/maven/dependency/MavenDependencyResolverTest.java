/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.dependency;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.jboss.forge.addon.dependency.Coordinate;
import org.jboss.forge.addon.dependency.Dependency;
import org.jboss.forge.addon.dependency.DependencyQuery;
import org.jboss.forge.addon.dependency.DependencyRepository;
import org.jboss.forge.addon.dependency.builder.CoordinateBuilder;
import org.jboss.forge.addon.dependency.builder.DependencyQueryBuilder;
import org.jboss.forge.addon.dependency.spi.DependencyResolver;
import org.jboss.forge.maven.container.MavenContainer;
import org.jboss.forge.maven.container.MavenDependencyResolver;
import org.jboss.forge.maven.dependency.filter.PackagingDependencyFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class MavenDependencyResolverTest
{
   private DependencyResolver resolver;

   @Before
   public void setUp()
   {
      resolver = new MavenDependencyResolver(new MavenContainer());
   }

   @Test
   public void testResolveNonJarArtifact() throws Exception
   {

      CoordinateBuilder coordinate = CoordinateBuilder.create("org.jboss.forge:forge-example-plugin:2.0.0-SNAPSHOT")
               .setPackaging("far");
      DependencyQueryBuilder query = DependencyQueryBuilder.create(coordinate).setFilter(
               new PackagingDependencyFilter("far"));
      Set<Dependency> artifacts = resolver.resolveDependencies(query);
      Assert.assertFalse(artifacts.isEmpty());
      Assert.assertEquals(1, artifacts.size());
      Dependency dependency = artifacts.iterator().next();
      Assert.assertEquals("far", dependency.getCoordinate().getPackaging());
      Assert.assertNotNull(dependency.getScopeType());
      Assert.assertTrue(dependency.isOptional());
   }

   @Test
   public void testResolveVersions() throws Exception
   {

      DependencyQuery query = DependencyQueryBuilder.create(CoordinateBuilder
               .create("org.jboss.forge:forge-distribution").setPackaging("zip")).setRepositories(
               new DependencyRepository("jboss", "https://repository.jboss.org/nexus/content/groups/public/"));
      List<Coordinate> versions = resolver.resolveVersions(query);
      Assert.assertNotNull(versions);
      Assert.assertFalse(versions.isEmpty());
   }

   @Test
   public void testResolveVersionsDependency() throws Exception
   {
      DependencyQuery query = DependencyQueryBuilder.create(CoordinateBuilder.create("org.hibernate:hibernate-core"));
      List<Coordinate> versions = resolver.resolveVersions(query);
      Assert.assertNotNull(versions);
      Assert.assertFalse(versions.isEmpty());
   }

   @Test
   public void testResolveArtifact() throws Exception
   {
      DependencyQuery query = DependencyQueryBuilder.create("org.jboss.forge:forge-example-plugin:far::2.0.0-SNAPSHOT");
      File artifact = resolver.resolveArtifact(query);
      Assert.assertNotNull(artifact);
      Assert.assertTrue("Artifact does not exist: " + artifact, artifact.exists());
   }
}