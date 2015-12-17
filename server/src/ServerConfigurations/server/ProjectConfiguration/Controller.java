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
import ServerConfigurations.server.AdminConfiguration.ServerConfigurations;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.serverSide.auth.AccessDeniedException;
import jetbrains.buildServer.serverSide.auth.AuthUtil;
import jetbrains.buildServer.serverSide.settings.ProjectSettingsManager;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.web.util.SessionUser;
import jetbrains.buildServer.web.util.WebAuthUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * Created by IIIEII on 27.05.15.
 */
public class Controller extends BaseController {

  private static final String CONTROLLER_PATH = "/admin/configureProjectServerConfigurations.html";
  private static final String LIST_PAGE = "EditProjectConfigurationsListPage.jsp";
  private static final String EDIT_PAGE = "EditProjectConfigurationEditPage.jsp";
  public static final String PROJECT_CONFIGURATION_TAG = "Configuration";
  public static final String PROJECT_CONFIGURATION_NAME_TAG = "name";
  public static final String PROJECT_CONFIGURATION_PREFIX_TAG = "prefix";
  public static final String PROJECT_CONFIGURATION_BRANCHFILTER_TAG = "branchFilter";
  private String listPagePath;
  private String editPagePath;
  private ServerConfigurations configurations;
  private final ProjectManager myProjectManager;
  private ProjectSettingsManager projectSettingsManager;

  public Controller(@NotNull SBuildServer server,
                    @NotNull PluginDescriptor descriptor,
                    @NotNull ServerPaths serverPaths,
                    @NotNull WebControllerManager manager,
                    @NotNull final ProjectManager projectManager,
                    @NotNull ProjectSettingsManager projectSettingsManager,
                    @NotNull ServerConfigurations configurations) throws IOException {
    manager.registerController(CONTROLLER_PATH, this);
    this.configurations = configurations;
    this.myProjectManager = projectManager;
    this.projectSettingsManager = projectSettingsManager;
    this.listPagePath = descriptor.getPluginResourcesPath(LIST_PAGE);
    this.editPagePath = descriptor.getPluginResourcesPath(EDIT_PAGE);
  }

  @Override
  public ModelAndView doHandle(HttpServletRequest request, HttpServletResponse response) {
    try {
      SUser user = SessionUser.getUser(request);
      SProject project = getProject(request);
      if (project != null && this.isAvailable(user, project)) {

        ProjectConfigurations projectSettings = (ProjectConfigurations) this.projectSettingsManager.getSettings(project.getProjectId(), Util.PLUGIN_NAME);

        String action = request.getParameter("action");
        if(action == null || action.trim().length() == 0)
          action = "list";
        else
          action = action.trim();
        boolean isPost = this.isPost(request);

        if(action.equals("list")) {

          ModelAndView modelAndView = new ModelAndView(this.listPagePath);
          modelAndView.getModel().put("pluginName", Util.PLUGIN_NAME);
          modelAndView.getModel().put("controllerPath", CONTROLLER_PATH);
          modelAndView.getModel().put("projectId", project.getExternalId());
          modelAndView.getModel().put("configurations", projectSettings.getConfigurationsList());
          return modelAndView;

        } else if (action.equals("edit")) {

          String configurationPrefix = request.getParameter("prefix");
          String configurationBranchFilter = this.hasRequestParameter(request, "branchFilter")?request.getParameter("branchFilter"):"";
          if (configurationPrefix != null) {
            ProjectConfiguration configuration = projectSettings.getConfigurationByPrefixBranchFilter(configurationPrefix, configurationBranchFilter);
            if (configuration != null) {
              ModelAndView modelAndView = new ModelAndView(this.editPagePath);
              modelAndView.getModel().put("pluginName", Util.PLUGIN_NAME);
              modelAndView.getModel().put("controllerPath", CONTROLLER_PATH);
              modelAndView.getModel().put("projectId", project.getExternalId());
              for (Map.Entry<String, String> entry : configuration.getConfigurationMap().entrySet()) {
                modelAndView.getModel().put(entry.getKey(), entry.getValue());
              }
              modelAndView.getModel().put("configurations", this.configurations.getConfigurationsList());
              return modelAndView;
            }
          }

        } else if (action.equals("new")) {

          ModelAndView modelAndView = new ModelAndView(this.editPagePath);
          modelAndView.getModel().put("pluginName", Util.PLUGIN_NAME);
          modelAndView.getModel().put("controllerPath", CONTROLLER_PATH);
          modelAndView.getModel().put("projectId", project.getExternalId());
          modelAndView.getModel().put("configurations", this.configurations.getConfigurationsList());
          return modelAndView;

        } else if (action.equals("save") && isPost) {

          // Check required fields
          if (!this.hasRequestParameter(request, "prefix") && !this.hasRequestParameter(request, "initialPrefix")) {
            return this.sendError(request, response, "Prefix field cannot be empty");
          }
          if (!this.hasRequestParameter(request, "name")) {
            return this.sendError(request, response, "Choose server configuration");
          }
          String branchFilter = this.hasRequestParameter(request, "branchFilter")?request.getParameter("branchFilter"):"";
          ProjectConfiguration configuration;
          if (!this.hasRequestParameter(request, "initialPrefix")) {
            if (projectSettings.getConfigurationByPrefixBranchFilter(request.getParameter("prefix"), branchFilter) != null) {
              return this.sendError(request, response, "There is another configuration with such prefix and branch filter, please choose another");
            } else {
              configuration = projectSettings.newConfiguration();
            }
          } else {
            configuration = projectSettings.getConfigurationByPrefixBranchFilter(request.getParameter("initialPrefix"), request.getParameter("initialBranchFilter"));
          }
          configuration.setName(request.getParameter("name"));
          configuration.setPrefix(request.getParameter("prefix"));
          configuration.setBranchFilter(branchFilter);
          project.persist();
          return this.sendMessage(request, response, "Project server configuration saved successfully");

        } else if (action.equals("delete")) {

          ProjectConfiguration configuration = projectSettings.getConfigurationByPrefixBranchFilter(request.getParameter("prefix"), request.getParameter("branchFilter"));
          projectSettings.deleteConfiguration(configuration);
          project.persist();
          return new ModelAndView(new RedirectView("/admin/editProject.html?projectId=" + project.getExternalId() + "&tab=" + Util.PLUGIN_NAME, true));

        }
      } else {
        WebAuthUtil.addAccessDeniedMessage(
                request, new AccessDeniedException(
                        user,
                        "You must be an System Administrator or ProjectConfiguration Administrator to manage server configuration."
                )
        );
        return null;
      }
    } catch (Exception e) {
      Loggers.SERVER.error(Util.PLUGIN_NAME + ": Could not handle request", e);
    }

    return null;
  }

  private ModelAndView sendError(HttpServletRequest request, HttpServletResponse response, String errorMessage) throws IOException {
    return this.sendMessage(request, response, 500, errorMessage);
  }

  private ModelAndView sendMessage(HttpServletRequest request, HttpServletResponse response, int statusCode, String message) throws IOException {
    getOrCreateMessages(request).addMessage("serverConfigurationMessage", message);
    response.sendError(statusCode);
    return null;
  }

  private ModelAndView sendMessage(HttpServletRequest request, HttpServletResponse response, String errorMessage) throws IOException {
    return this.sendMessage(request, response, 200, errorMessage);
  }

  private boolean hasRequestParameter(HttpServletRequest request, String name) {
    return (request.getParameter(name) != null && request.getParameter(name).trim().length() > 0);
  }

  private boolean isAvailable(@NotNull SUser user, @NotNull SProject project) {
    return AuthUtil.isSystemAdmin(user) || AuthUtil.hasPermissionToManageProject(user, project.getProjectId());
  }

  private boolean isAvailable(@NotNull HttpServletRequest request) {
    SUser user = SessionUser.getUser(request);
    SProject project = getProject(request);
    return this.isAvailable(user, project);
  }

  public SProject getProject(@NotNull HttpServletRequest request) {
    String projectId = request.getParameter("projectId");
    if (projectId != null) {
      return myProjectManager.findProjectByExternalId(projectId);
    }
    return null;
  }

}
