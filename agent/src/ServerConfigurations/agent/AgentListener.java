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

package ServerConfigurations.agent;

import jetbrains.buildServer.agent.*;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;
import ServerConfigurations.common.Util;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by IIIEII on 27.05.15.
 */
public class AgentListener extends AgentLifeCycleAdapter {
  public AgentListener(@NotNull EventDispatcher<AgentLifeCycleListener> dispatcher) {
    dispatcher.addListener(this);
  }

  @Override
  public void agentInitialized(@NotNull final BuildAgent agent) {
    Loggers.AGENT.info("Plugin '" + Util.PLUGIN_NAME + "'. is running.");
  }

  @Override
  public void beforeRunnerStart(@NotNull
                                BuildRunnerContext runner) {
    Iterator paramIter = runner.getConfigParameters().entrySet().iterator();
    while(paramIter.hasNext()) {
      Map.Entry<String, String> param = (Map.Entry<String, String>)paramIter.next();
      if (param.getValue().matches("^%secure:configuration\\.password[^%]+%$")) {
        runner.addConfigParameter(param.getKey(), runner.getConfigParameters().get(param.getValue().substring(1, param.getValue().length() - 1)));
      }
    }
  }
}
