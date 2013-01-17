/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.resource;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.services.Exported;
import org.jboss.forge.container.services.ExportedInstance;
import org.jboss.forge.resource.events.ResourceEvent;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 * @author Mike Brock <cbrock@redhat.com>
 */
@Exported
@Singleton
public class ResourceFactory
{
   @Inject
   private BeanManager manager;

   @Inject
   private AddonRegistry registry;

   @SuppressWarnings({ "unchecked", "rawtypes" })
   public <E, T extends Resource<E>> T create(final Class<T> type, final E underlyingResource)
   {
      synchronized (this)
      {
         for (ExportedInstance<ResourceGenerator> instance : getRegisteredResourceGenerators())
         {
            ResourceGenerator generator = instance.get();
            if (generator.handles(type, underlyingResource))
            {
               if (type.isAssignableFrom(generator.getResourceType(type, underlyingResource)))
               {
                  Resource<?> resource = generator.getResource(this, type, underlyingResource);
                  return (T) resource;
               }
            }
            instance.release(generator);
         }
      }
      return null;
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   public <E> Resource<E> create(E underlyingResource)
   {
      synchronized (this)
      {
         for (ExportedInstance<ResourceGenerator> instance : getRegisteredResourceGenerators())
         {
            ResourceGenerator generator = instance.get();
            if (generator.handles(Resource.class, underlyingResource))
            {
               Resource<?> resource = generator.getResource(this, Resource.class, underlyingResource);
               return (Resource<E>) resource;
            }
            instance.release(generator);
         }
      }
      return null;
   }

   @SuppressWarnings("rawtypes")
   private Iterable<ExportedInstance<ResourceGenerator>> getRegisteredResourceGenerators()
   {
      return registry.getExportedInstances(ResourceGenerator.class);
   }

   public ResourceFactory fireEvent(ResourceEvent event)
   {
      manager.fireEvent(event, new Annotation[] {});
      return this;
   }

}