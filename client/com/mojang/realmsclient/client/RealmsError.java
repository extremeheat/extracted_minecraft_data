package com.mojang.realmsclient.client;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsError {
   private static final Logger LOGGER = LogManager.getLogger();
   private final String errorMessage;
   private final int errorCode;

   private RealmsError(String var1, int var2) {
      super();
      this.errorMessage = var1;
      this.errorCode = var2;
   }

   @Nullable
   public static RealmsError parse(String var0) {
      if (Strings.isNullOrEmpty(var0)) {
         return null;
      } else {
         try {
            JsonObject var1 = JsonParser.parseString(var0).getAsJsonObject();
            String var2 = JsonUtils.getStringOr("errorMsg", var1, "");
            int var3 = JsonUtils.getIntOr("errorCode", var1, -1);
            return new RealmsError(var2, var3);
         } catch (Exception var4) {
            LOGGER.error("Could not parse RealmsError: {}", var4.getMessage());
            LOGGER.error("The error was: {}", var0);
            return null;
         }
      }
   }

   public String getErrorMessage() {
      return this.errorMessage;
   }

   public int getErrorCode() {
      return this.errorCode;
   }
}
