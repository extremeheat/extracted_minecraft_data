package com.mojang.realmsclient.dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

public record Ops(Set<String> ops) {
   private static final Logger LOGGER = LogUtils.getLogger();

   public Ops(Set<String> var1) {
      super();
      this.ops = var1;
   }

   public static Ops parse(String var0) {
      HashSet var1 = new HashSet();

      try {
         JsonObject var2 = LenientJsonParser.parse(var0).getAsJsonObject();
         JsonElement var3 = var2.get("ops");
         if (var3.isJsonArray()) {
            for(JsonElement var5 : var3.getAsJsonArray()) {
               var1.add(var5.getAsString());
            }
         }
      } catch (Exception var6) {
         LOGGER.error("Could not parse Ops", var6);
      }

      return new Ops(var1);
   }
}
