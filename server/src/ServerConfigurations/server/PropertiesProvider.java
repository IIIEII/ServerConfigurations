/*
 * Copyright 2000-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ServerConfigurations.server;

import ServerConfigurations.common.Util;
import ServerConfigurations.server.AdminConfiguration.*;
import ServerConfigurations.server.ProjectConfiguration.*;
import jetbrains.buildServer.ExtensionHolder;
import jetbrains.buildServer.UserDataStorage;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.parameters.AbstractParameterDescriptionProvider;
import jetbrains.buildServer.serverSide.parameters.BuildParametersProvider;
import jetbrains.buildServer.serverSide.parameters.ParameterDescriptionProvider;
import jetbrains.buildServer.serverSide.settings.ProjectSettingsManager;
import jetbrains.buildServer.vcs.spec.BranchFilterImpl;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by IIIEII on 31.05.15.
 */
public class PropertiesProvider extends AbstractParameterDescriptionProvider
          implements BuildParametersProvider, BuildStartContextProcessor, ParameterDescriptionProvider {

  private ServerConfigurations configurations;
  private ProjectSettingsManager projectSettingsManager;
  private final ServerUtil myUtil;
  private ProjectManager projectManager;

  public PropertiesProvider(@NotNull ExtensionHolder extensionHolder,
                            @NotNull final ProjectManager projectManager,
                            @NotNull ProjectSettingsManager projectSettingsManager,
                            @NotNull ServerConfigurations configurations,
                            @NotNull ServerUtil util) {

    this.configurations = configurations;
    this.projectSettingsManager = projectSettingsManager;
    this.myUtil = util;
    this.projectManager = projectManager;


    extensionHolder.registerExtension(
            BuildParametersProvider.class, PropertiesProvider.class.getName(), this
    );
    extensionHolder.registerExtension(
            ParameterDescriptionProvider.class, PropertiesProvider.class.getName(), this
    );
    extensionHolder.registerExtension(
            BuildStartContextProcessor.class, PropertiesProvider.class.getName(), this
    );
  }

  @NotNull
  public Map<String, String> getParameters(@NotNull SBuild build, boolean emulationMode)
  {
    return new HashMap<String, String>();
  }

  @NotNull
  public Collection<String> getParametersAvailableOnAgent(@NotNull final SBuild build)
  {
    // Returns collection of parameters (names) always available on the agent for the specified build.
    TreeSet<String> parameters = new TreeSet<String>();

    String projectId = build.getProjectId();
    Set<String> prefixes = new HashSet<String>();

    while(projectId != null) {
      ProjectConfigurations projectSettings = (ProjectConfigurations) this.projectSettingsManager.getSettings(projectId, Util.PLUGIN_NAME);
      for (ProjectConfiguration projectConfiguration : projectSettings.getConfigurations()) {
        String prefix = projectConfiguration.getPrefix();
        if (!prefixes.contains(prefix)) {
          prefixes.add(prefix);
          String configurationName = projectConfiguration.getName();
          ServerConfiguration configuration = this.configurations.getConfigurationByName(configurationName);
          if (configuration != null) {
            parameters.add(prefix + ".name");
//            parameters.add(prefix + ".server.address");
            for (ServerConfigurationProperty property : configuration.getProperties()) {
              parameters.add(prefix + "." + property.getName());
            }
          }
        }
      }
      projectId = projectManager.findProjectById(projectId).getParentProjectId();
    }
    return parameters;
  }

  public void updateParameters(@NotNull BuildStartContext buildStartContext)
  {
    // This extension point is called before parameters are sent to a build agent. Build context can be used to alter parameters of a build before data is sent to a build agent
    SRunningBuild runningBuild = buildStartContext.getBuild();

    Set<String> storedPasswords = new HashSet<String>();
    final UserDataStorage storage = ((RunningBuildEx) runningBuild).getUserDataStorage();
    // Store password values to clean up build log
    if (storage.getValue(myUtil.currentBuildNumberKEY) == runningBuild.getBuildNumber()) {
      storedPasswords = storage.getValue(myUtil.passwordsKEY);
    } else {
      storage.setValue(myUtil.currentBuildNumberKEY, runningBuild.getBuildNumber());
    }
    storage.setValue(myUtil.passwordsKEY, storedPasswords);

    String projectId = runningBuild.getProjectId();
    Set<String> prefixes = new HashSet<String>();
    while(projectId != null) {
      ProjectConfigurations projectSettings = (ProjectConfigurations) this.projectSettingsManager.getSettings(projectId, Util.PLUGIN_NAME);
        for(ProjectConfiguration projectConfiguration : projectSettings.getConfigurations()) {
          String serverName = projectConfiguration.getName();
          String prefix = projectConfiguration.getPrefix();
          String branchFilterText = projectConfiguration.getBranchFilter();
          Boolean checkBranch = true;
          if (runningBuild.getBranch() != null) {
            if (branchFilterText.contentEquals(""))
              branchFilterText = "+:*";
            BranchFilterImpl branchFilter = new BranchFilterImpl(branchFilterText);
            checkBranch = branchFilter.accept(runningBuild.getBranch());
          } else {
            if (!branchFilterText.contentEquals("") && !branchFilterText.contentEquals("+:*"))
              checkBranch = false;
          }
          if (checkBranch && !prefixes.contains(prefix)) {
            prefixes.add(prefix);
            ServerConfiguration configuration = this.configurations.getConfigurationByName(serverName);
            buildStartContext.addSharedParameter(prefix + ".name", configuration.getName());
            Template template = this.configurations.getTemplateByName(configuration.getTemplateName());
            for (ServerConfigurationProperty property : configuration.getProperties()) {
              String name = prefix + "." + property.getName();
              boolean isSet = false;
              if (template != null) {
                TemplateProperty templateProperty = template.getPropertyByName(property.getName());
                if (templateProperty != null) {
                  // Hide password values from build parameters tab
                  if (templateProperty.getType().equals(PropertiesType.PASSWORD)) {
                    buildStartContext.addSharedParameter(name, "%%secure:configuration.password." + name + "%%");
                    buildStartContext.addSharedParameter("secure:configuration.password." + name, property.getValue(templateProperty.getType()));
                    storedPasswords.add(property.getValue(templateProperty.getType()));
                  } else {
                    buildStartContext.addSharedParameter(name, property.getValue(templateProperty.getType()));
                  }
                  isSet = true;
                }
              }
              if (!isSet) {
                buildStartContext.addSharedParameter(name, property.getValue(PropertiesType.STRING));
              }
            }
          }
        }
      projectId = projectManager.findProjectById(projectId).getParentProjectId();
    }
  }

  @Override
  public String describe(@NotNull String parameterName)
  {
    return null;
  }

  @Override
  public boolean isVisible(String paramName) {
    return true;
  }
}
