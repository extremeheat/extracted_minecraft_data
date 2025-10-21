package com.mojang.realmsclient.dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import org.slf4j.Logger;

public class Backup extends ValueObject {
   private static final Logger LOGGER = LogUtils.getLogger();
   public final String backupId;
   public final Instant lastModified;
   public final long size;
   public boolean uploadedVersion;
   public final Map<String, String> metadata;
   public final Map<String, String> changeList = new HashMap();

   private Backup(String var1, Instant var2, long var3, Map<String, String> var5) {
      super();
      this.backupId = var1;
      this.lastModified = var2;
      this.size = var3;
      this.metadata = var5;
   }

   public ZonedDateTime lastModifiedDate() {
      return ZonedDateTime.ofInstant(this.lastModified, ZoneId.systemDefault());
   }

   @Nullable
   public static Backup parse(JsonElement var0) {
      JsonObject var1 = var0.getAsJsonObject();

      try {
         String var2 = JsonUtils.getStringOr("backupId", var1, "");
         Instant var3 = JsonUtils.getDateOr("lastModifiedDate", var1);
         long var4 = JsonUtils.getLongOr("size", var1, 0L);
         HashMap var6 = new HashMap();
         if (var1.has("metadata")) {
            JsonObject var7 = var1.getAsJsonObject("metadata");

            for(Map.Entry var10 : var7.entrySet()) {
               if (!((JsonElement)var10.getValue()).isJsonNull()) {
                  var6.put((String)var10.getKey(), ((JsonElement)var10.getValue()).getAsString());
               }
            }
         }

         return new Backup(var2, var3, var4, var6);
      } catch (Exception var11) {
         LOGGER.error("Could not parse Backup", var11);
         return null;
      }
   }
}
