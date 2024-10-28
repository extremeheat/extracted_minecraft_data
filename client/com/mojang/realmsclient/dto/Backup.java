package com.mojang.realmsclient.dto;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;

public class Backup extends ValueObject {
   private static final Logger LOGGER = LogUtils.getLogger();
   public String backupId;
   public Date lastModifiedDate;
   public long size;
   private boolean uploadedVersion;
   public Map<String, String> metadata = Maps.newHashMap();
   public Map<String, String> changeList = Maps.newHashMap();

   public Backup() {
      super();
   }

   public static Backup parse(JsonElement var0) {
      JsonObject var1 = var0.getAsJsonObject();
      Backup var2 = new Backup();

      try {
         var2.backupId = JsonUtils.getStringOr("backupId", var1, "");
         var2.lastModifiedDate = JsonUtils.getDateOr("lastModifiedDate", var1);
         var2.size = JsonUtils.getLongOr("size", var1, 0L);
         if (var1.has("metadata")) {
            JsonObject var3 = var1.getAsJsonObject("metadata");
            Set var4 = var3.entrySet();
            Iterator var5 = var4.iterator();

            while(var5.hasNext()) {
               Map.Entry var6 = (Map.Entry)var5.next();
               if (!((JsonElement)var6.getValue()).isJsonNull()) {
                  var2.metadata.put((String)var6.getKey(), ((JsonElement)var6.getValue()).getAsString());
               }
            }
         }
      } catch (Exception var7) {
         LOGGER.error("Could not parse Backup: {}", var7.getMessage());
      }

      return var2;
   }

   public boolean isUploadedVersion() {
      return this.uploadedVersion;
   }

   public void setUploadedVersion(boolean var1) {
      this.uploadedVersion = var1;
   }
}
