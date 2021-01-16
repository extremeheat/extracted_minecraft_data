package com.mojang.authlib;

import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import java.util.Arrays;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnvironmentParser {
   @Nullable
   private static String environmentOverride;
   private static final String PROP_PREFIX = "minecraft.api.";
   private static final Logger LOGGER = LogManager.getLogger();
   public static final String PROP_ENV = "minecraft.api.env";
   public static final String PROP_AUTH_HOST = "minecraft.api.auth.host";
   public static final String PROP_ACCOUNT_HOST = "minecraft.api.account.host";
   public static final String PROP_SESSION_HOST = "minecraft.api.session.host";
   public static final String PROP_SERVICES_HOST = "minecraft.api.services.host";

   public EnvironmentParser() {
      super();
   }

   public static void setEnvironmentOverride(String var0) {
      environmentOverride = var0;
   }

   public static Optional<Environment> getEnvironmentFromProperties() {
      String var0 = environmentOverride != null ? environmentOverride : System.getProperty("minecraft.api.env");
      Optional var10000 = YggdrasilEnvironment.fromString(var0);
      Environment.class.getClass();
      Optional var1 = var10000.map(Environment.class::cast);
      return var1.isPresent() ? var1 : fromHostNames();
   }

   private static Optional<Environment> fromHostNames() {
      String var0 = System.getProperty("minecraft.api.auth.host");
      String var1 = System.getProperty("minecraft.api.account.host");
      String var2 = System.getProperty("minecraft.api.session.host");
      String var3 = System.getProperty("minecraft.api.services.host");
      if (var0 != null && var1 != null && var2 != null) {
         return Optional.of(Environment.create(var0, var1, var2, var3, "properties"));
      } else {
         if (var0 != null || var1 != null || var2 != null) {
            LOGGER.info("Ignoring hosts properties. All need to be set: " + Arrays.asList("minecraft.api.auth.host", "minecraft.api.account.host", "minecraft.api.session.host"));
         }

         return Optional.empty();
      }
   }
}
