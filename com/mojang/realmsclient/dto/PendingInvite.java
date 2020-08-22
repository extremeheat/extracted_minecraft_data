package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PendingInvite extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public String invitationId;
   public String worldName;
   public String worldOwnerName;
   public String worldOwnerUuid;
   public Date date;

   public static PendingInvite parse(JsonObject var0) {
      PendingInvite var1 = new PendingInvite();

      try {
         var1.invitationId = JsonUtils.getStringOr("invitationId", var0, "");
         var1.worldName = JsonUtils.getStringOr("worldName", var0, "");
         var1.worldOwnerName = JsonUtils.getStringOr("worldOwnerName", var0, "");
         var1.worldOwnerUuid = JsonUtils.getStringOr("worldOwnerUuid", var0, "");
         var1.date = JsonUtils.getDateOr("date", var0);
      } catch (Exception var3) {
         LOGGER.error("Could not parse PendingInvite: " + var3.getMessage());
      }

      return var1;
   }
}
