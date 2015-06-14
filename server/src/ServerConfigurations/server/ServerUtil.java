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

import ServerConfigurations.common.Util;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BASE64DecoderStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BASE64EncoderStream;
import jetbrains.buildServer.UserDataKey;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Set;

/**
 * Created by IIIEII on 12.01.15.
 */
public class ServerUtil {
  public final UserDataKey<String> currentBuildNumberKEY;
  public final UserDataKey<Set> passwordsKEY;
  private static final String key = "4a6XUJr0kRezhJlFeyZkSg==";
  private static Cipher ecipher;
  private static Cipher dcipher;


  public ServerUtil () {
    currentBuildNumberKEY = new UserDataKey<String>(String.class, Util.PLUGIN_NAME + ".currentBuildNumber");
    passwordsKEY = new UserDataKey<Set>(Set.class, Util.PLUGIN_NAME + ".passwords");
    try {
      ecipher = Cipher.getInstance("AES");
      ecipher.init(Cipher.ENCRYPT_MODE, getKey());
      dcipher = Cipher.getInstance("AES");
      dcipher.init(Cipher.DECRYPT_MODE, getKey());
    } catch (Exception e) { }
  }

  private static SecretKey getKey () {
    byte[] encodedKey = BASE64DecoderStream.decode(key.getBytes());
    return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
  }

  public static String encrypt(String str) {
    try {
      byte[] utf8 = str.getBytes("UTF8");
      byte[] enc = ecipher.doFinal(utf8);
      enc = BASE64EncoderStream.encode(enc);
      return new String(enc);
    } catch (Exception e) { }
    return "";
  }

  public static String decrypt(String str) {
    try {
      byte[] dec = BASE64DecoderStream.decode(str.getBytes());
      byte[] utf8 = dcipher.doFinal(dec);
      return new String(utf8, "UTF8");
    } catch (Exception e) { }
    return "";
  }
}
