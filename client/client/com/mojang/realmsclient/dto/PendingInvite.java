package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Date;
import java.util.UUID;
import net.minecraft.Util;
import org.slf4j.Logger;

public class PendingInvite extends ValueObject {
   private static final Logger LOGGER = LogUtils.getLogger();
   public String invitationId;
   public String realmName;
   public String realmOwnerName;
   public UUID realmOwnerUuid;
   public Date date;

   public PendingInvite() {
      super();
   }

   public static PendingInvite parse(JsonObject var0) {
      PendingInvite var1 = new PendingInvite();

      try {
         var1.invitationId = JsonUtils.getStringOr("invitationId", var0, "");
         var1.realmName = JsonUtils.getStringOr("worldName", var0, "");
         var1.realmOwnerName = JsonUtils.getStringOr("worldOwnerName", var0, "");
         var1.realmOwnerUuid = JsonUtils.getUuidOr("worldOwnerUuid", var0, Util.NIL_UUID);
         var1.date = JsonUtils.getDateOr("date", var0);
      } catch (Exception var3) {
         LOGGER.error("Could not parse PendingInvite: {}", var3.getMessage());
      }

      return var1;
   }
}
