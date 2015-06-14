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
 * Created by IIIEII on 02.06.15.
 */
@XStreamAlias(Controller.TEMPLATE_TAG)
public class Template {

  @XStreamAlias(Controller.TEMPLATE_NAME_TAG)
  private String name = null;

  @XStreamImplicit
  private List<TemplateProperty> properties = new ArrayList<TemplateProperty>();

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  public Map<String, Object> getTemplateMap() {
    Map<String, Object> templatesMap = new LinkedHashMap<String, Object>();
    templatesMap.put(Controller.TEMPLATE_NAME_TAG, name);
    templatesMap.put(Controller.TEMPLATE_PROPS_TAG, this.getProperties());
    return templatesMap;
  };

  public List<TemplateProperty> getProperties() {
    if (this.properties == null)
      this.properties = new ArrayList<TemplateProperty>();
    return this.properties;
  }
  public TemplateProperty getPropertyByName(String name) {
    for (TemplateProperty property : this.getProperties()) {
      if (property.getName().contentEquals(name)) {
        return property;
      }
    }
    return null;
  }
  public void setProperty(TemplateProperty newProperty) {
    boolean found = false;
    for (TemplateProperty property : this.getProperties()) {
      if (property.getName().contentEquals(newProperty.getName())) {
        property.setType(newProperty.getType());
        found = true;
      }
    }
    if (!found) {
      this.getProperties().add(newProperty);
    }
  }
  public void setProperty(String name, PropertiesType type) {
    TemplateProperty property = new TemplateProperty();
    property.setName(name);
    property.setType(type);
    this.setProperty(property);
  }
  public void clearProperties() {
    this.getProperties().clear();
  }
}
