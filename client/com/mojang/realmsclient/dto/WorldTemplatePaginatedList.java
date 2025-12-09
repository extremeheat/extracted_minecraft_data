package com.mojang.realmsclient.dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

public record WorldTemplatePaginatedList(List<WorldTemplate> templates, int page, int size, int total) {
   private static final Logger LOGGER = LogUtils.getLogger();

   public WorldTemplatePaginatedList(int var1) {
      this(List.of(), 0, var1, -1);
   }

   public WorldTemplatePaginatedList(List<WorldTemplate> var1, int var2, int var3, int var4) {
      super();
      this.templates = var1;
      this.page = var2;
      this.size = var3;
      this.total = var4;
   }

   public boolean isLastPage() {
      return this.page * this.size >= this.total && this.page > 0 && this.total > 0 && this.size > 0;
   }

   public static WorldTemplatePaginatedList parse(String var0) {
      ArrayList var1 = new ArrayList();
      int var2 = 0;
      int var3 = 0;
      int var4 = 0;

      try {
         JsonObject var5 = LenientJsonParser.parse(var0).getAsJsonObject();
         if (var5.get("templates").isJsonArray()) {
            for(JsonElement var7 : var5.get("templates").getAsJsonArray()) {
               WorldTemplate var8 = WorldTemplate.parse(var7.getAsJsonObject());
               if (var8 != null) {
                  var1.add(var8);
               }
            }
         }

         var2 = JsonUtils.getIntOr("page", var5, 0);
         var3 = JsonUtils.getIntOr("size", var5, 0);
         var4 = JsonUtils.getIntOr("total", var5, 0);
      } catch (Exception var9) {
         LOGGER.error("Could not parse WorldTemplatePaginatedList", var9);
      }

      return new WorldTemplatePaginatedList(var1, var2, var3, var4);
   }
}
