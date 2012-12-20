package org.example;

import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.example.published.PublishedService;
import org.example.simple.SimpleService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.AddonDependency;
import org.jboss.forge.container.AddonId;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.RegisteredAddon;
import org.jboss.forge.container.services.RemoteInstance;
import org.jboss.forge.container.services.ServiceRegistry;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class AddonMultipleDependencyVersionTest
{
   @Deployment(order = 1)
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"))
               .addAsAddonDependencies(
                        AddonDependency.create(AddonId.from("dep", "1")),
                        AddonDependency.create(AddonId.from("dep", "2"))
               );

      return archive;
   }

   @Deployment(name = "dep,1", testable = false, order = 2)
   public static ForgeArchive getDeploymentDep1()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addClass(SimpleService.class)
               .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));

      return archive;
   }

   @Deployment(name = "dep,2", testable = false, order = 3)
   public static ForgeArchive getDeploymentDep2()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addClasses(SimpleService.class, PublishedService.class)
               .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));

      return archive;
   }

   @Inject
   private AddonRegistry registry;

   @Test
   public void testVersionLookup() throws Exception
   {
      Map<RegisteredAddon, ServiceRegistry> services = registry.getServiceRegistries();
      int count = 0;
      for (Entry<RegisteredAddon, ServiceRegistry> entry : services.entrySet())
      {
         for (Class<?> service : entry.getValue().getServices())
         {
            if (service.getName().equals(SimpleService.class.getName()))
            {
               RemoteInstance<?> instance = entry.getValue().getRemoteInstance(service);
               Object serviceInstance = instance.get();
               Assert.assertNotNull(serviceInstance);
               Object result = serviceInstance.getClass().getMethod("isStartupObserved").invoke(serviceInstance);
               Assert.assertTrue((Boolean) result);
               count++;
            }
         }
      }
      Assert.assertEquals(2, count);
   }

}