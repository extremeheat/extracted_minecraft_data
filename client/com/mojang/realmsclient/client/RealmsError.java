package com.mojang.realmsclient.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
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

   public static RealmsError create(String var0) {
      try {
         JsonParser var1 = new JsonParser();
         JsonObject var2 = var1.parse(var0).getAsJsonObject();
         String var3 = JsonUtils.getStringOr("errorMsg", var2, "");
         int var4 = JsonUtils.getIntOr("errorCode", var2, -1);
         return new RealmsError(var3, var4);
      } catch (Exception var5) {
         LOGGER.error("Could not parse RealmsError: " + var5.getMessage());
         LOGGER.error("The error was: " + var0);
         return new RealmsError("Failed to parse response from server", -1);
      }
   }

   public String getErrorMessage() {
      return this.errorMessage;
   }

   public int getErrorCode() {
      return this.errorCode;
   }
}
