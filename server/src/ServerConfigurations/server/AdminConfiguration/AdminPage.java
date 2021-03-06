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

import ServerConfigurations.common.Util;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.web.openapi.*;

import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;

/**
 * Created by IIIEII on 27.05.15.
 */
public class AdminPage extends jetbrains.buildServer.controllers.admin.AdminPage {

  private static final String AFTER_PAGE_ID = "auth";
  private static final String BEFORE_PAGE_ID = "email";
  private static final String PAGE = "AdminPage.jsp";

  private static final String TAB_TITLE = "Server Configurations";

  public AdminPage(@NotNull SBuildServer server,
                   @NotNull PagePlaces pagePlaces,
                   @NotNull PluginDescriptor descriptor) {
    super(pagePlaces);
    setPluginName(Util.PLUGIN_NAME);
    setIncludeUrl(descriptor.getPluginResourcesPath(PAGE));
    addCssFile(descriptor.getPluginResourcesPath("css/ServerConfigurationsAdminPage.css"));
    addJsFile(descriptor.getPluginResourcesPath("js/ServerConfigurationsAdminPage.js"));
    addCssFile("/css/forms.css");
    setTabTitle(TAB_TITLE);
    ArrayList<String> after = new ArrayList<String>();
    after.add(AFTER_PAGE_ID);
    ArrayList<String> before = new ArrayList<String>();
    before.add(BEFORE_PAGE_ID);
    setPosition(PositionConstraint.between(after, before));
    register();
  }

  @Override
  public boolean isAvailable(@NotNull HttpServletRequest request) {
    return super.isAvailable(request) && checkHasGlobalPermission(request, Permission.CHANGE_SERVER_SETTINGS);
  }

  @NotNull
  public String getGroup() {
    return SERVER_RELATED_GROUP;
  }
}
