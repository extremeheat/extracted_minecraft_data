package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import java.util.List;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

public class BackupList extends ValueObject {
   private static final Logger LOGGER = LogUtils.getLogger();
   public List<Backup> backups;

   public BackupList() {
      super();
   }

   public static BackupList parse(String var0) {
      BackupList var1 = new BackupList();
      var1.backups = Lists.newArrayList();

      try {
         JsonElement var2 = LenientJsonParser.parse(var0).getAsJsonObject().get("backups");
         if (var2.isJsonArray()) {
            for(JsonElement var4 : var2.getAsJsonArray()) {
               var1.backups.add(Backup.parse(var4));
            }
         }
      } catch (Exception var5) {
         LOGGER.error("Could not parse BackupList: {}", var5.getMessage());
      }

      return var1;
   }
}
