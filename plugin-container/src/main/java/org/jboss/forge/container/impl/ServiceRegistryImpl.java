package org.jboss.forge.container.impl;

import java.util.Set;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.container.impl.services.RemoteInstanceImpl;
import org.jboss.forge.container.impl.util.Sets;
import org.jboss.forge.container.services.RemoteInstance;
import org.jboss.forge.container.services.ServiceRegistry;

@Singleton
public class ServiceRegistryImpl implements ServiceRegistry
{
   private Set<Class<?>> services = Sets.getConcurrentSet();

   @Inject
   private BeanManager manager;

   @Override
   public <T> void addService(Class<T> clazz)
   {
      services.add(clazz);
   }

   @Override
   public <T> RemoteInstance<T> getRemoteInstance(Class<T> clazz)
   {
      return new RemoteInstanceImpl<T>(manager, clazz);
   }

   @Override
   public Set<Class<?>> getServices()
   {
      return services;
   }

   @Override
   public boolean hasService(Class<?> serviceType)
   {
      return services.contains(serviceType);
   }

   @Override
   public String toString()
   {
      return services.toString();
   }
}