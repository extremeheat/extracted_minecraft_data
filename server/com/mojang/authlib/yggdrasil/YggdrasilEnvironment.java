package com.mojang.authlib.yggdrasil;

import com.mojang.authlib.Environment;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public enum YggdrasilEnvironment implements Environment {
   PROD("https://authserver.mojang.com", "https://api.mojang.com", "https://sessionserver.mojang.com", "https://api.minecraftservices.com"),
   STAGING("https://yggdrasil-auth-staging.mojang.com", "https://api-staging.mojang.com", "https://yggdrasil-auth-session-staging.mojang.zone", "https://api-staging.minecraftservices.com");

   private final String authHost;
   private final String accountsHost;
   private final String sessionHost;
   private final String servicesHost;

   private YggdrasilEnvironment(String var3, String var4, String var5, String var6) {
      this.authHost = var3;
      this.accountsHost = var4;
      this.sessionHost = var5;
      this.servicesHost = var6;
   }

   public String getAuthHost() {
      return this.authHost;
   }

   public String getAccountsHost() {
      return this.accountsHost;
   }

   public String getSessionHost() {
      return this.sessionHost;
   }

   public String getServicesHost() {
      return this.servicesHost;
   }

   public String getName() {
      return this.name();
   }

   public String asString() {
      return (new StringJoiner(", ", "", "")).add("authHost='" + this.authHost + "'").add("accountsHost='" + this.accountsHost + "'").add("sessionHost='" + this.sessionHost + "'").add("servicesHost='" + this.servicesHost + "'").add("name='" + this.getName() + "'").toString();
   }

   public static Optional<YggdrasilEnvironment> fromString(@Nullable String var0) {
      return Stream.of(values()).filter((var1) -> {
         return var0 != null && var0.equalsIgnoreCase(var1.name());
      }).findFirst();
   }
}
