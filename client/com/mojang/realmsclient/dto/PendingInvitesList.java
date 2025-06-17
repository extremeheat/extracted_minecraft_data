package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.List;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

public class PendingInvitesList extends ValueObject {
   private static final Logger LOGGER = LogUtils.getLogger();
   public List<PendingInvite> pendingInvites = Lists.newArrayList();

   public PendingInvitesList() {
      super();
   }

   public static PendingInvitesList parse(String var0) {
      PendingInvitesList var1 = new PendingInvitesList();

      try {
         JsonObject var2 = LenientJsonParser.parse(var0).getAsJsonObject();
         if (var2.get("invites").isJsonArray()) {
            for(JsonElement var4 : var2.get("invites").getAsJsonArray()) {
               var1.pendingInvites.add(PendingInvite.parse(var4.getAsJsonObject()));
            }
         }
      } catch (Exception var5) {
         LOGGER.error("Could not parse PendingInvitesList: {}", var5.getMessage());
      }

      return var1;
   }
}
