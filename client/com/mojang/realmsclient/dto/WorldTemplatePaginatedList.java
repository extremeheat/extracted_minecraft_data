package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldTemplatePaginatedList extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public List<WorldTemplate> templates;
   public int page;
   public int size;
   public int total;

   public WorldTemplatePaginatedList() {
      super();
   }

   public WorldTemplatePaginatedList(int var1) {
      super();
      this.templates = Collections.emptyList();
      this.page = 0;
      this.size = var1;
      this.total = -1;
   }

   public static WorldTemplatePaginatedList parse(String var0) {
      WorldTemplatePaginatedList var1 = new WorldTemplatePaginatedList();
      var1.templates = Lists.newArrayList();

      try {
         JsonParser var2 = new JsonParser();
         JsonObject var3 = var2.parse(var0).getAsJsonObject();
         if (var3.get("templates").isJsonArray()) {
            Iterator var4 = var3.get("templates").getAsJsonArray().iterator();

            while(var4.hasNext()) {
               var1.templates.add(WorldTemplate.parse(((JsonElement)var4.next()).getAsJsonObject()));
            }
         }

         var1.page = JsonUtils.getIntOr("page", var3, 0);
         var1.size = JsonUtils.getIntOr("size", var3, 0);
         var1.total = JsonUtils.getIntOr("total", var3, 0);
      } catch (Exception var5) {
         LOGGER.error("Could not parse WorldTemplatePaginatedList: {}", var5.getMessage());
      }

      return var1;
   }
}
