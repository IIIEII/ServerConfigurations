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

  public PropertiesProvider(@NotNull ExtensionHolder extensionHolder,
                            @NotNull final ProjectManager projectManager,
                            @NotNull ProjectSettingsManager projectSettingsManager,
                            @NotNull ServerConfigurations configurations,
                            @NotNull ServerUtil util) {
    this.configurations = configurations;
    this.projectSettingsManager = projectSettingsManager;
    this.myUtil = util;

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
    ProjectConfigurations projectSettings = (ProjectConfigurations) this.projectSettingsManager.getSettings(build.getProjectId(), Util.PLUGIN_NAME);

    TreeSet<String> parameters = new TreeSet<String>();
    for(ProjectConfiguration projectConfiguration : projectSettings.getConfigurations()) {
      String configurationName = projectConfiguration.getName();
      ServerConfiguration configuration = this.configurations.getConfigurationByName(configurationName);
      if (configuration != null) {
        parameters.add(projectConfiguration.getPrefix() + ".name");
        parameters.add(projectConfiguration.getPrefix() + ".server.address");
        for (ServerConfigurationProperty property : configuration.getProperties()) {
          parameters.add(projectConfiguration.getPrefix() + "." + property.getName());
        }
      }
    }
    return parameters;
  }

  public void updateParameters(@NotNull BuildStartContext buildStartContext)
  {
    // This extension point is called before parameters are sent to a build agent. Build context can be used to alter parameters of a build before data is sent to a build agent
    SRunningBuild runningBuild = buildStartContext.getBuild();
    ProjectConfigurations projectSettings = (ProjectConfigurations) this.projectSettingsManager.getSettings(runningBuild.getProjectId(), Util.PLUGIN_NAME);

    Set<String> storedPasswords = new HashSet<String>();
    final UserDataStorage storage = ((RunningBuildEx) runningBuild).getUserDataStorage();
    // Store password values to clean up build log
    if (storage.getValue(myUtil.currentBuildNumberKEY) == runningBuild.getBuildNumber()) {
      storedPasswords = storage.getValue(myUtil.passwordsKEY);
    } else {
      storage.setValue(myUtil.currentBuildNumberKEY, runningBuild.getBuildNumber());
    }
    storage.setValue(myUtil.passwordsKEY, storedPasswords);
    for(ProjectConfiguration projectConfiguration : projectSettings.getConfigurations()) {
      String serverName = projectConfiguration.getName();
      ServerConfiguration configuration = this.configurations.getConfigurationByName(serverName);
      buildStartContext.addSharedParameter(projectConfiguration.getPrefix() + ".name", configuration.getName());
      Template template = this.configurations.getTemplateByName(configuration.getTemplateName());
      for (ServerConfigurationProperty property : configuration.getProperties()) {
        String name = projectConfiguration.getPrefix() + "." + property.getName();
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
