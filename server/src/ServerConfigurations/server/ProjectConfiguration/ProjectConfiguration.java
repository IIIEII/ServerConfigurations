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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IIIEII on 30.05.15.
 */
public class ProjectConfiguration {

  private String name;
  private String prefix;
  private String branchFilter;

  public String getName() {
    return this.name;
  }
  public void setName(String name) {
    this.name = name;
  }

  public String getPrefix() {
    return this.prefix;
  }
  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String getBranchFilter() {
    return branchFilter;
  }
  public void setBranchFilter(String branchFilter) {
    this.branchFilter = branchFilter;
  }

  public Map<String,String> getConfigurationMap() {
    HashMap<String,String> map = new HashMap<String, String>();
    map.put(Controller.PROJECT_CONFIGURATION_NAME_TAG, this.name);
    map.put(Controller.PROJECT_CONFIGURATION_PREFIX_TAG, this.prefix);
    map.put(Controller.PROJECT_CONFIGURATION_BRANCHFILTER_TAG, this.branchFilter);
    return map;
  }

}
