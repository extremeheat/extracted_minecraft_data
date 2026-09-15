package com.mojang.realmsclient.dto;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

public record InviteCodeList(List<InviteCode> inviteCodes) {
   private static final Logger LOGGER = LogUtils.getLogger();

   public InviteCodeList {
      super();
   }

   public static InviteCodeList parse(final String json) {
      List<InviteCode> codes = new ArrayList();

      try {
         JsonElement root = LenientJsonParser.parse(json);

         for(JsonElement element : getInviteCodesArray(root)) {
            if (!element.isJsonObject()) {
               LOGGER.error("Invite code list contained a non-object entry: {}", element);
            } else {
               InviteCode code = InviteCode.parse(element.getAsJsonObject());
               if (code != null) {
                  codes.add(code);
               }
            }
         }
      } catch (Exception e) {
         LOGGER.error("Could not parse InviteCodeList", e);
      }

      return new InviteCodeList(List.copyOf(codes));
   }

   private static JsonArray getInviteCodesArray(final JsonElement root) {
      if (root.isJsonArray()) {
         return root.getAsJsonArray();
      } else {
         JsonObject object = root.getAsJsonObject();
         return object.has("result") && object.get("result").isJsonArray() ? object.getAsJsonArray("result") : new JsonArray();
      }
   }
}
