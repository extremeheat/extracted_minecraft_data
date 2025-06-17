package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

public class RealmsServerList extends ValueObject implements ReflectionBasedSerialization {
   private static final Logger LOGGER = LogUtils.getLogger();
   @SerializedName("servers")
   public List<RealmsServer> servers = new ArrayList();

   public RealmsServerList() {
      super();
   }

   public static RealmsServerList parse(GuardedSerializer var0, String var1) {
      try {
         RealmsServerList var2 = (RealmsServerList)var0.fromJson(var1, RealmsServerList.class);
         if (var2 == null) {
            LOGGER.error("Could not parse McoServerList: {}", var1);
            return new RealmsServerList();
         } else {
            var2.servers.forEach(RealmsServer::finalize);
            return var2;
         }
      } catch (Exception var3) {
         LOGGER.error("Could not parse McoServerList: {}", var3.getMessage());
         return new RealmsServerList();
      }
   }
}
