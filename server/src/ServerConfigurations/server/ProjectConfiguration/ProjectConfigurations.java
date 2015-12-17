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

package ServerConfigurations.server.ProjectConfiguration;

import ServerConfigurations.common.Util;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IIIEII on 30.05.15.
 */
public class ProjectConfigurations implements jetbrains.buildServer.serverSide.settings.ProjectSettings {

  private List<ProjectConfiguration> configurations = new ArrayList<ProjectConfiguration>();

  public void dispose() {

  }
  public void readFrom(Element rootElement) {
    this.getConfigurations().clear();
    List<Element> configurationsElm = rootElement.getChildren(Controller.PROJECT_CONFIGURATION_TAG);
    for (Element configurationElm : configurationsElm) {
      ProjectConfiguration configuration = new ProjectConfiguration();
      configuration.setName(configurationElm.getAttributeValue(Controller.PROJECT_CONFIGURATION_NAME_TAG, ""));
      configuration.setPrefix(configurationElm.getAttributeValue(Controller.PROJECT_CONFIGURATION_PREFIX_TAG, ""));
      configuration.setBranchFilter(configurationElm.getAttributeValue(Controller.PROJECT_CONFIGURATION_BRANCHFILTER_TAG, ""));
      this.getConfigurations().add(configuration);
    }
  }
  public void writeTo(Element parentElement) {
    parentElement.removeChildren(Util.PLUGIN_NAME);
    for (ProjectConfiguration configuration : this.getConfigurations()) {
      Element configurationElm = new Element(Controller.PROJECT_CONFIGURATION_TAG);
      configurationElm.setAttribute(Controller.PROJECT_CONFIGURATION_NAME_TAG, String.valueOf(configuration.getName()));
      configurationElm.setAttribute(Controller.PROJECT_CONFIGURATION_PREFIX_TAG, String.valueOf(configuration.getPrefix()));
      configurationElm.setAttribute(Controller.PROJECT_CONFIGURATION_BRANCHFILTER_TAG, String.valueOf(configuration.getBranchFilter()));
      parentElement.addContent(configurationElm);
    }
  }

  public List<ProjectConfiguration> getConfigurations() {
    if (this.configurations == null)
      this.configurations = new ArrayList<ProjectConfiguration>();
    return this.configurations;
  }
  public List<Map> getConfigurationsList() {
    List<Map> configurationsList = new ArrayList<Map>();
    for (ProjectConfiguration configuration : this.getConfigurations()) {
      configurationsList.add(configuration.getConfigurationMap());
    }
    return configurationsList;
  }
  public ProjectConfiguration getConfigurationByPrefixBranchFilter(String configurationPrefix, String branchFilter) {
    for (ProjectConfiguration configuration : this.getConfigurations()) {
      if (configuration.getPrefix().contentEquals(configurationPrefix) && configuration.getBranchFilter().contentEquals(branchFilter)) {
        return configuration;
      }
    }
    return null;
  }
  public ProjectConfiguration getConfigurationByName(String configurationName) {
    for (ProjectConfiguration configuration : this.getConfigurations()) {
      if (configuration.getName().contentEquals(configurationName)) {
        return configuration;
      }
    }
    return null;
  }
  public ProjectConfiguration newConfiguration() {
    ProjectConfiguration configuration = new ProjectConfiguration();
    this.getConfigurations().add(configuration);
    return configuration;
  }
  public void deleteConfiguration(ProjectConfiguration configuration) {
    this.getConfigurations().remove(configuration);
  }

}
