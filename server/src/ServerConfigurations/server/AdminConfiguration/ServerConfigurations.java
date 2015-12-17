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

package ServerConfigurations.server.AdminConfiguration;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IIIEII on 27.05.15.
 */
@XStreamAlias(Controller.CONFIGURATIONS_TAG)
public class ServerConfigurations {

  @XStreamImplicit
  private List<ServerConfiguration> configurations = new ArrayList<ServerConfiguration>();

  @XStreamImplicit
  private List<Template> templates = new ArrayList<Template>();

  public List<ServerConfiguration> getConfigurations() {
    if (this.configurations == null)
      this.configurations = new ArrayList<ServerConfiguration>();
    return this.configurations;
  }
  public ServerConfiguration getConfigurationByName(String configurationName) {
    for (ServerConfiguration configuration : this.getConfigurations()) {
      if (configuration.getName().contentEquals(configurationName)) {
        return configuration;
      }
    }
    return null;
  }
  public void setConfiguration(ServerConfiguration newConfiguration) {
    boolean found = false;
    for (ServerConfiguration configuration : this.getConfigurations()) {
      if (configuration.getName().contentEquals(newConfiguration.getName())) {
        configuration.setTemplateName(newConfiguration.getTemplateName());
        configuration.setProperties(newConfiguration.getProperties());
        found = true;
      }
    }
    if (!found) {
      this.getConfigurations().add(newConfiguration);
    }
  }
  public void deleteConfiguration(ServerConfiguration configuration) {
    this.getConfigurations().remove(configuration);
  }

  public List<Map> getConfigurationsList() {
    return getConfigurationsList(false);
  }

  public List<Map> getConfigurationsListWithPasswords() {
    return getConfigurationsList(true);
  }

  public List<Map> getConfigurationsList(Boolean withPasswords) {
    List<Map> configurationsList = new ArrayList<Map>();
    for (ServerConfiguration configuration : getConfigurations()) {
      configurationsList.add(configuration.getConfigurationMap(withPasswords));
    }
    return configurationsList;
  }

  public List<Template> getTemplates() {
    if (this.templates == null)
      this.templates = new ArrayList<Template>();
    return this.templates;
  }
  public Template getTemplateByName(String name) {
    for (Template template : this.getTemplates()) {
      if (template.getName().contentEquals(name)) {
        return template;
      }
    }
    return null;
  }
  public void setTemplate(Template newTemplate) {
    boolean found = false;
    for (Template template : this.getTemplates()) {
      if (template.getName().contentEquals(newTemplate.getName())) {
        template.clearProperties();
        for (TemplateProperty property : newTemplate.getProperties()) {
          template.setProperty(property);
        }
        found = true;
      }
    }
    if (!found) {
      this.getTemplates().add(newTemplate);
    }
  }
  public void deleteTemplate(Template template) {
    this.getTemplates().remove(template);
  }
  public List<Map> getTemplatesList() {
    List<Map> templatesList = new ArrayList<Map>();
    for (Template template : getTemplates()) {
      templatesList.add(template.getTemplateMap());
    }
    return templatesList;
  }

}
