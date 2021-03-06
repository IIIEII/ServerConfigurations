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
import jetbrains.buildServer.messages.BuildMessage1;
import jetbrains.buildServer.messages.BuildMessagesTranslator;
import jetbrains.buildServer.messages.DefaultMessagesInfo;
import jetbrains.buildServer.serverSide.RunningBuildEx;
import jetbrains.buildServer.serverSide.SRunningBuild;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by IIIEII on 08.01.15.
 */
public class BuildLogFilter implements BuildMessagesTranslator {

  private final ServerUtil myUtil;

  public BuildLogFilter(@NotNull ServerUtil util) {
    myUtil = util;
  }

  @NotNull
  public List<BuildMessage1> translateMessages(@NotNull final SRunningBuild build,
                                               @NotNull final List<BuildMessage1> messages
  ) {
    final UserDataStorage storage = ((RunningBuildEx) build).getUserDataStorage();
    Set<String> storedPasswords = storage.getValue(myUtil.passwordsKEY);

    List<BuildMessage1> result = new ArrayList<BuildMessage1>();
    for (BuildMessage1 originalMessage : messages) {
      final Object data = originalMessage.getValue();
      if (!DefaultMessagesInfo.MSG_TEXT.equals(originalMessage.getTypeId()) || data == null || !(data instanceof String)) {
        result.add(originalMessage);
        continue;
      }
      String text = (String) data;

      if (storedPasswords != null) {
        for (String value : storedPasswords) {
          if (!value.equals(""))
            text = text.replaceAll(value, "******");
        }
      }
      result.add(DefaultMessagesInfo.createTextMessage(originalMessage, text));
    }
    return result;
  }
}
