package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import java.util.List;
import org.slf4j.Logger;

public record RealmsServerList(List<RealmsServer> servers) implements ReflectionBasedSerialization {
   private static final Logger LOGGER = LogUtils.getLogger();

   public RealmsServerList(List<RealmsServer> var1) {
      super();
      this.servers = var1;
   }

   public static RealmsServerList parse(GuardedSerializer var0, String var1) {
      try {
         RealmsServerList var2 = (RealmsServerList)var0.fromJson(var1, RealmsServerList.class);
         if (var2 != null) {
            var2.servers.forEach(RealmsServer::finalize);
            return var2;
         }

         LOGGER.error("Could not parse McoServerList: {}", var1);
      } catch (Exception var3) {
         LOGGER.error("Could not parse McoServerList", var3);
      }

      return new RealmsServerList(List.of());
   }

   @SerializedName("servers")
   public List<RealmsServer> servers() {
      return this.servers;
   }
}
