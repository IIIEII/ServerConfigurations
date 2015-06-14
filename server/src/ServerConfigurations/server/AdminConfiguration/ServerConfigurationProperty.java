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

import ServerConfigurations.server.ServerUtil;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by IIIEII on 02.06.15.
 */
@XStreamAlias(Controller.CONFIGURATION_PROP_TAG)
public class ServerConfigurationProperty {

  @XStreamAlias(Controller.CONFIGURATION_PROP_NAME_TAG)
  private String name = null;

  @XStreamAlias(Controller.CONFIGURATION_PROP_VALUE_TAG)
  private String value = null;

  public ServerConfigurationProperty(String name, String value, PropertiesType type) {
    this.name = name;
    this.setValue(value, type);
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  public String getValue(PropertiesType type) {
    if (type.equals(PropertiesType.PASSWORD)) {
      return ServerUtil.decrypt(value);
    }
    return value;
  }
  public void setValue(String value, PropertiesType type) {
    if (type.equals(PropertiesType.PASSWORD)) {
      this.value = ServerUtil.encrypt(value);
    } else {
      this.value = value;
    }
  }
}
