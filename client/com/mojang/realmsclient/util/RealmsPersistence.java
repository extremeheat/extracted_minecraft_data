package com.mojang.realmsclient.util;

import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.GuardedSerializer;
import com.mojang.realmsclient.dto.ReflectionBasedSerialization;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import net.minecraft.client.Minecraft;
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
      Path var0 = getPathToData();

      try {
         String var1 = Files.readString(var0, StandardCharsets.UTF_8);
         RealmsPersistenceData var2 = (RealmsPersistenceData)GSON.fromJson(var1, RealmsPersistenceData.class);
         if (var2 != null) {
            return var2;
         }
      } catch (NoSuchFileException var3) {
      } catch (Exception var4) {
         LOGGER.warn("Failed to read Realms storage {}", var0, var4);
      }

      return new RealmsPersistenceData();
   }

   public static void writeFile(RealmsPersistenceData var0) {
      Path var1 = getPathToData();

      try {
         Files.writeString(var1, GSON.toJson((ReflectionBasedSerialization)var0), StandardCharsets.UTF_8);
      } catch (Exception var3) {
      }

   }

   private static Path getPathToData() {
      return Minecraft.getInstance().gameDirectory.toPath().resolve("realms_persistence.json");
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
