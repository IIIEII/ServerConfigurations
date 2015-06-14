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
import ServerConfigurations.server.ProjectConfiguration.ProjectConfigurations;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildServer;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import com.thoughtworks.xstream.XStream;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Created by IIIEII on 27.05.15.
 */
public class Controller extends BaseController {

  public static final String CONFIGURATIONS_TAG = "configurations";
  public static final String CONFIGURATION_TAG = "configuration";
  public static final String CONFIGURATION_TEMPLATE_TAG = "templateName";
  public static final String CONFIGURATION_NAME_TAG = "name";
  public static final String CONFIGURATION_PROPS_TAG = "properties";
  public static final String CONFIGURATION_PROP_TAG = "property";
  public static final String CONFIGURATION_PROP_NAME_TAG = "name";
  public static final String CONFIGURATION_PROP_TYPE_TAG = "type";
  public static final String CONFIGURATION_PROP_VALUE_TAG = "value";

  public static final String TEMPLATES_TAG = "templates";
  public static final String TEMPLATE_TAG = "template";
  public static final String TEMPLATE_NAME_TAG = "name";
  public static final String TEMPLATE_PROPS_TAG = "properties";
  public static final String TEMPLATE_PROP_TAG = "templateProperty";
  public static final String TEMPLATE_PROP_NAME_TAG = "name";
  public static final String TEMPLATE_PROP_TYPE_TAG = "type";

  private static final String CONTROLLER_PATH = "/admin/configureServerConfigurations.html";
  private static final String CONFIG_FILE = "serverConfigurations.xml";
  private static final String LIST_PAGE = "AdminConfigurationsListPage.jsp";
  private static final String EDIT_PAGE = "AdminConfigurationEditPage.jsp";
  private static final String EDIT_TEMPL_PAGE = "AdminTemplateEditPage.jsp";
  private static final String ERROR_PAGE = "ErrorPage.jsp";
  private String configFilePath;
  private String listPagePath;
  private String editPagePath;
  private String editTemplPagePath;
  private String errorPagePath;
  public static ServerConfigurations configurations;
  private final ProjectManager myProjectManager;
  private ProjectSettingsManager projectSettingsManager;

  public Controller(@NotNull SBuildServer server,
                    @NotNull PluginDescriptor descriptor,
                    @NotNull ServerPaths serverPaths,
                    @NotNull WebControllerManager manager,
                    @NotNull ServerConfigurations configurations,
                    @NotNull final ProjectManager projectManager,
                    @NotNull ProjectSettingsManager projectSettingsManager) throws IOException {
    manager.registerController(CONTROLLER_PATH, this);
    Controller.configurations = configurations;
    this.myProjectManager = projectManager;
    this.projectSettingsManager = projectSettingsManager;
    this.configFilePath = (new File(serverPaths.getConfigDir(), CONFIG_FILE)).getCanonicalPath();
    this.listPagePath = descriptor.getPluginResourcesPath(LIST_PAGE);
    this.editPagePath = descriptor.getPluginResourcesPath(EDIT_PAGE);
    this.editTemplPagePath = descriptor.getPluginResourcesPath(EDIT_TEMPL_PAGE);
    this.errorPagePath = descriptor.getPluginResourcesPath(ERROR_PAGE);
  }

  @Override
  public ModelAndView doHandle(HttpServletRequest request, HttpServletResponse response) {
    try {
      SUser user = SessionUser.getUser(request);
      if (this.isAvailable(user)) {

        String action = request.getParameter("action");
        if(action == null || action.trim().length() == 0)
          action = "list";
        else
          action = action.trim();
        boolean isPost = this.isPost(request);

        if(action.equals("list")) { // List configurations and templates

          loadConfiguration(); //TODO: remove(DEBUG!!!)

          ModelAndView modelAndView = new ModelAndView(this.listPagePath);
          modelAndView.getModel().put("pluginName", Util.PLUGIN_NAME);
          modelAndView.getModel().put("controllerPath", CONTROLLER_PATH);
          modelAndView.getModel().put(CONFIGURATIONS_TAG, Controller.configurations.getConfigurationsList());
          modelAndView.getModel().put(TEMPLATES_TAG, Controller.configurations.getTemplatesList());
          return modelAndView;

        } else if (action.equals("edit")) { // Edit configuration form

          String configurationName = request.getParameter("name");
          if (configurationName != null) {
            ServerConfiguration configuration = Controller.configurations.getConfigurationByName(configurationName);
            if (configuration != null) {
              configuration.setTemplateName(configuration.getTemplateName());
              this.saveConfiguration();
              ModelAndView modelAndView = new ModelAndView(this.editPagePath);
              modelAndView.getModel().put("pluginName", Util.PLUGIN_NAME);
              modelAndView.getModel().put("controllerPath", CONTROLLER_PATH);
              modelAndView.getModel().put(TEMPLATES_TAG, Controller.configurations.getTemplatesList());
              for (Map.Entry<String, Object> entry : configuration.getConfigurationMap().entrySet()) {
                modelAndView.getModel().put(entry.getKey(), entry.getValue());
              }
              return modelAndView;
            } else {
              getOrCreateMessages(request).addMessage("serverConfigurationMessage", "Cannot find server configuration with name '" + configurationName + "'");
            }
          }
          return new ModelAndView(this.errorPagePath);

        } else if (action.equals("new")) { // Create new configuration form

          ModelAndView modelAndView = new ModelAndView(this.editPagePath);
          modelAndView.getModel().put("pluginName", Util.PLUGIN_NAME);
          modelAndView.getModel().put("controllerPath", CONTROLLER_PATH);
          modelAndView.getModel().put(TEMPLATES_TAG, Controller.configurations.getTemplatesList());
          return modelAndView;

        } else if (action.equals("save") && isPost) { // Save configuration

          // Check required fields
          if (!this.hasRequestParameter(request, "name") && !this.hasRequestParameter(request, "initialName")) {
            return this.sendError(request, response, "Name field cannot be empty");
          }
          if (!this.hasRequestParameter(request, "templateName")) {
            return this.sendError(request, response, "Template field cannot be empty");
          }
          if (this.hasRequestParameter(request, "name") && this.hasRequestParameter(request, "initialName")) {
            return this.sendError(request, response, "Cannot change name for configuration");
          }
          // Find/create configuration
          ServerConfiguration serverConfiguration = null;
          if (!this.hasRequestParameter(request, "initialName")) {
            if (Controller.configurations.getConfigurationByName(request.getParameter("name")) != null) {
              return this.sendError(request, response, "There is another configuration with such name, please choose another");
            } else {
              serverConfiguration = new ServerConfiguration();
              serverConfiguration.setName(request.getParameter("name"));
              Controller.configurations.setConfiguration(serverConfiguration);
            }
          } else {
            serverConfiguration = Controller.configurations.getConfigurationByName(request.getParameter("initialName"));
          }
          // Set configuration data
          serverConfiguration.setTemplateName(request.getParameter("templateName"));
          String[] propKeys = request.getParameterMap().get("key");
          String[] propValues = request.getParameterMap().get("value");
          String[] propChanged = request.getParameterMap().get("changed");
          if (propKeys != null && propValues != null && propChanged != null) {
            Map<String,String> properties = new LinkedHashMap<String, String>();
            for (Integer i = 0; i < propKeys.length; i++) {
              if (propValues.length > i && propChanged.length > i && propChanged[i].equals("true")) {
                properties.put(propKeys[i], propValues[i]);
              }
            }
            serverConfiguration.setProperties(properties);
          } else {
            serverConfiguration.clearProperties();
          }
          this.saveConfiguration();
          return this.sendMessage(request, response, "Server configuration saved successfully");

        } else if (action.equals("delete")) { // Delete configuration

          String configurationName = request.getParameter("name");

          for (String projectId : this.myProjectManager.getProjectIds()) {
            ProjectConfigurations projectSettings = (ProjectConfigurations) this.projectSettingsManager.getSettings(projectId, Util.PLUGIN_NAME);
            if (projectSettings.getConfigurationByName(configurationName) != null) {
              getOrCreateMessages(request).addMessage("serverConfigurationMessage", "You cannot delete this server configuration, it is used in projects");
              return new ModelAndView(new RedirectView("/admin/admin.html?item=" + Util.PLUGIN_NAME, true));
            }
          }

          ServerConfiguration serverConfiguration = Controller.configurations.getConfigurationByName(configurationName);
          Controller.configurations.deleteConfiguration(serverConfiguration);
          this.saveConfiguration();
          return new ModelAndView(new RedirectView("/admin/admin.html?item=" + Util.PLUGIN_NAME, true));

        } else if (action.equals("newTemplate")) { // New template form

          ModelAndView modelAndView = new ModelAndView(this.editTemplPagePath);
          modelAndView.getModel().put("pluginName", Util.PLUGIN_NAME);
          modelAndView.getModel().put("controllerPath", CONTROLLER_PATH);
          modelAndView.getModel().put("types", Arrays.asList(PropertiesType.values()));
          return modelAndView;

        } else if (action.equals("editTemplate")) { // Edit template form

          String templateName = request.getParameter("name");
          if (templateName != null) {
            Template template = Controller.configurations.getTemplateByName(templateName);
            if (template != null) {
              ModelAndView modelAndView = new ModelAndView(this.editTemplPagePath);
              modelAndView.getModel().put("pluginName", Util.PLUGIN_NAME);
              modelAndView.getModel().put("controllerPath", CONTROLLER_PATH);
              modelAndView.getModel().put("types", Arrays.asList(PropertiesType.values()));
              for (Map.Entry<String, Object> entry : template.getTemplateMap().entrySet()) {
                modelAndView.getModel().put(entry.getKey(), entry.getValue());
              }
              return modelAndView;
            } else {
              getOrCreateMessages(request).addMessage("serverConfigurationMessage", "Cannot find template with name '" + templateName + "'");
            }
          }
          return new ModelAndView(this.errorPagePath);

        } else if (action.equals("saveTemplate") && isPost) { // Save template

          // Check required fields
          if (!this.hasRequestParameter(request, "name") && !this.hasRequestParameter(request, "initialName")) {
            return this.sendError(request, response, "Name field cannot be empty");
          }
          if (this.hasRequestParameter(request, "name") && this.hasRequestParameter(request, "initialName")) {
            return this.sendError(request, response, "Cannot change name for template");
          }
          String[] propNames = request.getParameterMap().get("key");
          String[] propTypes = request.getParameterMap().get("type");
          if (propNames != null) {
            for (Integer i = 0; i < propNames.length; i++) {
              if (propNames[i].trim().equals("")) {
                return this.sendError(request, response, "Property name field cannot be empty");
              }
            }
          }
          // Find/create template
          Template template = null;
          if (this.hasRequestParameter(request, "initialName")) {
            template = Controller.configurations.getTemplateByName(request.getParameter("initialName"));
          } else {
            template = new Template();
            template.setName(request.getParameter("name"));
            Controller.configurations.setTemplate(template);
          }
          // Set template data
          template.clearProperties();
          if (propNames != null && propTypes != null) {
            for (Integer i = 0; i < propNames.length; i++) {
              if (propTypes.length > i)
                template.setProperty(propNames[i], PropertiesType.valueOf(propTypes[i]));
            }
          }
          this.saveConfiguration();
          return this.sendMessage(request, response, "Template saved successfully");

        } else if (action.equals("deleteTemplate")) { // Delete template

          String templateName = request.getParameter("name");

          for (ServerConfiguration configuration : configurations.getConfigurations()) {
            if (configuration.getTemplateName().equals(templateName)) {
              getOrCreateMessages(request).addMessage("serverConfigurationMessage", "You cannot delete this template, it is used in server configurations");
              return new ModelAndView(new RedirectView("/admin/admin.html?item=" + Util.PLUGIN_NAME, true));
            }
          }

          Template template = Controller.configurations.getTemplateByName(templateName);
          Controller.configurations.deleteTemplate(template);
          this.saveConfiguration();
          return new ModelAndView(new RedirectView("/admin/admin.html?item=" + Util.PLUGIN_NAME, true));

        }
      } else {
        WebAuthUtil.addAccessDeniedMessage(
                request, new AccessDeniedException(
                        user,
                        "You must be an System Administrator or Global ProjectConfiguration Administrator to manage server configurations."
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

  public void initialise() {
    try {
      File file = new File(this.configFilePath);
      if (file.exists()) {
        this.loadConfiguration();
      } else {
        this.saveConfiguration();
      }
    } catch (Exception e) {
      Loggers.SERVER.error(Util.PLUGIN_NAME + ": Could not load configuration", e);
    }
  }

  public void loadConfiguration() throws IOException {
    XStream xstream = new XStream();
    xstream.setClassLoader(Controller.configurations.getClass().getClassLoader());
    xstream.setClassLoader(ServerConfiguration.class.getClassLoader());
    xstream.setClassLoader(ServerConfigurationProperty.class.getClassLoader());
    xstream.setClassLoader(Template.class.getClassLoader());
    xstream.setClassLoader(TemplateProperty.class.getClassLoader());
    xstream.processAnnotations(ServerConfigurations.class);
    FileReader fileReader = new FileReader(this.configFilePath);
    ServerConfigurations configurations = (ServerConfigurations) xstream.fromXML(fileReader);
    fileReader.close();

    // Copy the values, because we need it on the original shared (bean),
    // which is a singleton
    if (configurations.getConfigurations() != null) {
      for (ServerConfiguration configuration : configurations.getConfigurations()) {
        Controller.configurations.setConfiguration(configuration);
      }
    }
    if (configurations.getTemplates() != null) {
      for (Template template: configurations.getTemplates()) {
        Controller.configurations.setTemplate(template);
      }
    }
    saveConfiguration();
  }

  public void saveConfiguration() throws IOException {
    XStream xstream = new XStream();
    xstream.processAnnotations(Controller.configurations.getClass());
    File file = new File(this.configFilePath);
    file.createNewFile();
    FileOutputStream fileOutputStream = new FileOutputStream(file);
    xstream.toXML(Controller.configurations, fileOutputStream);
    fileOutputStream.flush();
    fileOutputStream.close();
  }

  private boolean isAvailable(@NotNull SUser user) {
    return AuthUtil.isSystemAdmin(user) || AuthUtil.hasPermissionToManageAllProjects(user);
  }

  private boolean isAvailable(@NotNull HttpServletRequest request) {
    SUser user = SessionUser.getUser(request);
    return this.isAvailable(user);
  }

  private String join(String[] arr, String glue) {
    String result = "";
    for (Integer i = 0; i < arr.length; i++) {
      result += arr[i];
      if (i < arr.length - 1)
        result += glue;
    }
    return result;
  }

}
