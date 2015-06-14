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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IIIEII on 27.05.15.
 */
@XStreamAlias(Controller.CONFIGURATION_TAG)
public class ServerConfiguration {

  @XStreamAlias(Controller.CONFIGURATION_NAME_TAG)
  private String name = null;

  @XStreamAlias(Controller.CONFIGURATION_TEMPLATE_TAG)
  private String templateName = null;

  @XStreamImplicit
  private List<ServerConfigurationProperty> properties = new ArrayList<ServerConfigurationProperty>();

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTemplateName() {
    return templateName;
  }
  public Template getTemplate() {
    return Controller.configurations.getTemplateByName(templateName);
  }
  public void setTemplateName(String templateName) {
    this.templateName = templateName;
    this.syncTemplateProperties();
  }
  private void syncTemplateProperties() {
    Template template = this.getTemplate();
    if (template != null) {
      List<ServerConfigurationProperty> propsToRemove = new ArrayList<ServerConfigurationProperty>();
      for (ServerConfigurationProperty property : this.getProperties() ) {
        if (template.getPropertyByName(property.getName()) == null) {
          propsToRemove.add(property);
        }
      }
      for (ServerConfigurationProperty property : propsToRemove) {
        this.getProperties().remove(property);
      }
      for (TemplateProperty templateProperty : template.getProperties()) {
        if (this.getPropertyByName(templateProperty.getName()) == null) {
          this.setProperty(templateProperty.getName(), "", templateProperty.getType());
        }
      }
    }
  }

  public List<ServerConfigurationProperty> getProperties() {
    if (this.properties == null)
      this.properties = new ArrayList<ServerConfigurationProperty>();
    return this.properties;
  }
  public ServerConfigurationProperty getPropertyByName(String name) {
    for (ServerConfigurationProperty property : this.getProperties()) {
      if (property.getName().contentEquals(name)) {
        return property;
      }
    }
    return null;
  }
  public void setProperties(List<ServerConfigurationProperty> properties) {
    Template template = this.getTemplate();
    if (template != null) {
      if (properties != null) {
        for (ServerConfigurationProperty property : properties) {
          TemplateProperty templateProperty = template.getPropertyByName(property.getName());
          if (templateProperty != null) {
            this.setProperty(property.getName(), property.getValue(templateProperty.getType()), templateProperty.getType());
          } else {
            this.setProperty(property.getName(), property.getValue(PropertiesType.STRING), PropertiesType.STRING);
          }
        }
      }
    }
  }
  public void setProperties(Map<String,String> properties) {
    Template template = this.getTemplate();
    if (template != null) {
      if (properties != null) {
        for (Map.Entry<String,String> property : properties.entrySet()) {
          TemplateProperty templateProperty = template.getPropertyByName(property.getKey());
          if (templateProperty != null) {
            this.setProperty(property.getKey(), property.getValue(), templateProperty.getType());
          } else {
            this.setProperty(property.getKey(), property.getValue(), PropertiesType.STRING);
          }
        }
      }
    }
  }
  public void setProperty(String name, String value, PropertiesType type) {
    ServerConfigurationProperty property = this.getPropertyByName(name);
    if (property != null) {
      property.setValue(value, type);
    } else {
      property = new ServerConfigurationProperty(name, value, type);
      this.getProperties().add(property);
    }
  }
  public String getPropertyValue(String name, PropertiesType type) {
    ServerConfigurationProperty property = this.getPropertyByName(name);
    if (property == null) {
      return null;
    }
    return property.getValue(type);
  }
  public void clearProperties() {
    this.getProperties().clear();
  }

  public Map<String, Object> getConfigurationMap() {
    Template template = Controller.configurations.getTemplateByName(templateName);
    Map<String, Object> configuration = new LinkedHashMap<String, Object>();
    configuration.put(Controller.CONFIGURATION_NAME_TAG, name);
    if (template != null) {
      configuration.put(Controller.CONFIGURATION_TEMPLATE_TAG, templateName);
      List<Map> propertiesList = new ArrayList<Map>();
      for (ServerConfigurationProperty property : this.getProperties()) {
        Map<String, String> propertyMap = new LinkedHashMap<String, String>();
        TemplateProperty templateProperty = template.getPropertyByName(property.getName());
        if (templateProperty != null) {
          propertyMap.put(Controller.CONFIGURATION_PROP_NAME_TAG, property.getName());
          propertyMap.put(Controller.CONFIGURATION_PROP_TYPE_TAG, templateProperty.getType().toString());
          if (templateProperty.getType().equals(PropertiesType.PASSWORD)) {
            propertyMap.put(Controller.CONFIGURATION_PROP_VALUE_TAG, "");
          } else {
            propertyMap.put(Controller.CONFIGURATION_PROP_VALUE_TAG, this.getPropertyValue(property.getName(), templateProperty.getType()));
          }
        }
        propertiesList.add(propertyMap);
      }
      configuration.put(Controller.CONFIGURATION_PROPS_TAG, propertiesList);
    }
    return configuration;
  }
}
