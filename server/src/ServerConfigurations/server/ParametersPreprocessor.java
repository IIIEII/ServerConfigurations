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

import jetbrains.buildServer.UserDataStorage;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.parameters.AbstractBuildParametersProvider;
import jetbrains.buildServer.serverSide.parameters.ParameterFactory;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by IIIEII on 12.01.15.
 */
public class ParametersPreprocessor extends AbstractBuildParametersProvider implements jetbrains.buildServer.serverSide.ParametersPreprocessor {

  private final SBuildServer myServer;
  private final ParameterFactory myParameterFactory;
  private final ServerUtil myUtil;

  public ParametersPreprocessor(@NotNull SBuildServer server, @NotNull ParameterFactory parameterFactory, @NotNull ServerUtil util) {
    myServer = server;
    myParameterFactory = parameterFactory;
    myUtil = util;
  }

  public void fixRunBuildParameters(@NotNull final SRunningBuild build,
                                    @NotNull final Map<String, String> runParameters,
                                    @NotNull final Map<String, String> buildParams) {

//    String projectId = build.getValueResolver().resolve("%system.server.configuration.projectId%").getResult();
//    String serverName = build.getValueResolver().resolve("%system.server.configuration.name%").getResult();
//
//    if (projectId != null && serverName != null && 1 == 0) {
//      // Получаем проект с конфигурациями серверов
//      SProject project = myServer.getProjectManager().findProjectByExternalId(projectId);
//
//      if (project != null) {
//        // Получаем билд с конфигурацией нужного сервера
//        SBuildType configurationBuildType = project.findBuildTypeByName(serverName);
//        if (configurationBuildType != null) {
//
//          // Собираем набор значений паролей для скрывания из лога
//          Set<String> storedPasswords = new HashSet<String>();
//          final UserDataStorage storage = ((RunningBuildEx) build).getUserDataStorage();
//          if (storage.getValue(myUtil.currentBuildNumberKEY) == build.getBuildNumber()) {
//            // В рамках одного билда накапливаем значения
//            storedPasswords = storage.getValue(myUtil.passwordsKEY);
//          } else {
//            storage.setValue(myUtil.currentBuildNumberKEY, build.getBuildNumber());
//          }
//
//          Collection<Parameter> params = configurationBuildType.getOwnParametersCollection();
//          Map<String, String> paramsData = myParameterFactory.extractBuildParameters(params);
//          /*int i = 0;
//          for (String key : paramsData.keySet()) {
//            buildParams.put(key, paramsData.get(key));
//            if (((Parameter) (params.toArray()[i++])).getControlDescription().getParameterType().equals("password")) {
//              storedPasswords.add(paramsData.get(key));
//            }
//          }*/
//          storage.setValue(myUtil.passwordsKEY, storedPasswords);
//          Iterator paramIter = params.iterator();
//          while(paramIter.hasNext()) {
//            Parameter param = (Parameter)paramIter.next();
//            buildParams.put(param.getName(), paramsData.get(param.getName()));
//            ControlDescription controlDescription = param.getControlDescription();
//            if (controlDescription != null && controlDescription.getParameterType().equals("password")) {
//              storedPasswords.add(paramsData.get(param.getName()));
//            }
//          }
//        }
//      }
//    }
  }
}
