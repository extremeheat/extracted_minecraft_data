package com.mojang.realmsclient.dto;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

public record BackupList(List<Backup> backups) {
   private static final Logger LOGGER = LogUtils.getLogger();

   public BackupList(List<Backup> var1) {
      super();
      this.backups = var1;
   }

   public static BackupList parse(String var0) {
      ArrayList var1 = new ArrayList();

      try {
         JsonElement var2 = LenientJsonParser.parse(var0).getAsJsonObject().get("backups");
         if (var2.isJsonArray()) {
            for(JsonElement var4 : var2.getAsJsonArray()) {
               Backup var5 = Backup.parse(var4);
               if (var5 != null) {
                  var1.add(var5);
               }
            }
         }
      } catch (Exception var6) {
         LOGGER.error("Could not parse BackupList", var6);
      }

      return new BackupList(var1);
   }
}
