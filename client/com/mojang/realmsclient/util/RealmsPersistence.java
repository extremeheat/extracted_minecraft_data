package com.mojang.realmsclient.util;

import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.GuardedSerializer;
import com.mojang.realmsclient.dto.ReflectionBasedSerialization;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

public class RealmsPersistence {
   private static final String FILE_NAME = "realms_persistence.json";
   private static final GuardedSerializer GSON = new GuardedSerializer();
   private static final Logger LOGGER = LogUtils.getLogger();

   public RealmsPersistence() {
      super();
   }

   public RealmsPersistenceData read() {
      return readFile();
   }

   public void save(RealmsPersistenceData var1) {
      writeFile(var1);
   }

   public static RealmsPersistenceData readFile() {
      File var0 = getPathToData();

      try {
         String var1 = FileUtils.readFileToString(var0, StandardCharsets.UTF_8);
         RealmsPersistenceData var2 = (RealmsPersistenceData)GSON.fromJson(var1, RealmsPersistenceData.class);
         if (var2 != null) {
            return var2;
         }
      } catch (FileNotFoundException var3) {
      } catch (Exception var4) {
         LOGGER.warn("Failed to read Realms storage {}", var0, var4);
      }

      return new RealmsPersistenceData();
   }

   public static void writeFile(RealmsPersistenceData var0) {
      File var1 = getPathToData();

      try {
         FileUtils.writeStringToFile(var1, GSON.toJson(var0), StandardCharsets.UTF_8);
      } catch (IOException var3) {
      }

   }

   private static File getPathToData() {
      return new File(Minecraft.getInstance().gameDirectory, "realms_persistence.json");
   }

   public static class RealmsPersistenceData implements ReflectionBasedSerialization {
      @SerializedName("newsLink")
      public String newsLink;
      @SerializedName("hasUnreadNews")
      public boolean hasUnreadNews;

      public RealmsPersistenceData() {
         super();
      }
   }
}
