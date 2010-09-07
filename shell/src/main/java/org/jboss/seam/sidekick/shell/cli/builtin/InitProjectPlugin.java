/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.seam.sidekick.shell.cli.builtin;

import java.io.File;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.seam.sidekick.project.ProjectModelException;
import org.jboss.seam.sidekick.project.model.MavenProject;
import org.jboss.seam.sidekick.shell.CurrentProjectHolder;
import org.jboss.seam.sidekick.shell.Shell;
import org.jboss.seam.sidekick.shell.plugins.events.PostStartup;
import org.jboss.seam.sidekick.shell.plugins.plugins.Help;
import org.jboss.seam.sidekick.shell.plugins.plugins.Plugin;
import org.jboss.weld.environment.se.bindings.Parameters;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Help("Responsible for initializing and maintaining the current active project.")
public class InitProjectPlugin implements Plugin
{
   @Inject
   private Shell shell;

   @Inject
   private CurrentProjectHolder cp;

   @Inject
   @Parameters
   private List<String> parameters;

   public void initProject(@Observes final PostStartup event)
   {
      shell.printlnVerbose("Parameters: " + parameters);

      File targetDirectory = shell.getCurrentDirectory();
      shell.printlnVerbose("Using project path: [" + targetDirectory.getAbsolutePath() + "]");

      shell.setPrompt("sidekick> ");
      if (targetDirectory.exists())
      {
         try
         {
            MavenProject currentProject = new MavenProject(targetDirectory);
            if (currentProject.exists())
            {
               shell.setPrompt(currentProject.getPOM().getArtifactId() + "> ");
            }
            cp.setCurrentProject(currentProject);
         }
         catch (ProjectModelException e)
         {
            shell.println("**WARNING** The directory [" + targetDirectory.getAbsolutePath() + "] does not contain a project.");
         }
      }
      else
      {
         shell.println("**WARNING** The directory [" + targetDirectory.getAbsolutePath() + "] does not exist...");
      }
   }
}