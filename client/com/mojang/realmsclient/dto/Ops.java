package com.mojang.realmsclient.dto;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Set;
import net.minecraft.util.LenientJsonParser;

public class Ops extends ValueObject {
   public Set<String> ops = Sets.newHashSet();

   public Ops() {
      super();
   }

   public static Ops parse(String var0) {
      Ops var1 = new Ops();

      try {
         JsonObject var2 = LenientJsonParser.parse(var0).getAsJsonObject();
         JsonElement var3 = var2.get("ops");
         if (var3.isJsonArray()) {
            for(JsonElement var5 : var3.getAsJsonArray()) {
               var1.ops.add(var5.getAsString());
            }
         }
      } catch (Exception var6) {
      }

      return var1;
   }
}
