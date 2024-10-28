package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;

public class ServerActivity extends ValueObject {
   public String profileUuid;
   public long joinTime;
   public long leaveTime;

   public ServerActivity() {
      super();
   }

   public static ServerActivity parse(JsonObject var0) {
      ServerActivity var1 = new ServerActivity();

      try {
         var1.profileUuid = JsonUtils.getStringOr("profileUuid", var0, (String)null);
         var1.joinTime = JsonUtils.getLongOr("joinTime", var0, -9223372036854775808L);
         var1.leaveTime = JsonUtils.getLongOr("leaveTime", var0, -9223372036854775808L);
      } catch (Exception var3) {
      }

      return var1;
   }
}
