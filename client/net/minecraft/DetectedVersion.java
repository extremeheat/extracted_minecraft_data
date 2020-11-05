package net.minecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.bridge.game.GameVersion;
import com.mojang.bridge.game.PackType;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import net.minecraft.util.GsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DetectedVersion implements GameVersion {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final GameVersion BUILT_IN = new DetectedVersion();
   private final String id;
   private final String name;
   private final boolean stable;
   private final int worldVersion;
   private final int protocolVersion;
   private final int resourcePackVersion;
   private final int dataPackVersion;
   private final Date buildTime;
   private final String releaseTarget;

   private DetectedVersion() {
      super();
      this.id = UUID.randomUUID().toString().replaceAll("-", "");
      this.name = "20w45a";
      this.stable = false;
      this.worldVersion = 2681;
      this.protocolVersion = SharedConstants.getProtocolVersion();
      this.resourcePackVersion = 7;
      this.dataPackVersion = 6;
      this.buildTime = new Date();
      this.releaseTarget = "1.17";
   }

   private DetectedVersion(JsonObject var1) {
      super();
      this.id = GsonHelper.getAsString(var1, "id");
      this.name = GsonHelper.getAsString(var1, "name");
      this.releaseTarget = GsonHelper.getAsString(var1, "release_target");
      this.stable = GsonHelper.getAsBoolean(var1, "stable");
      this.worldVersion = GsonHelper.getAsInt(var1, "world_version");
      this.protocolVersion = GsonHelper.getAsInt(var1, "protocol_version");
      JsonObject var2 = GsonHelper.getAsJsonObject(var1, "pack_version");
      this.resourcePackVersion = GsonHelper.getAsInt(var2, "resource");
      this.dataPackVersion = GsonHelper.getAsInt(var2, "data");
      this.buildTime = Date.from(ZonedDateTime.parse(GsonHelper.getAsString(var1, "build_time")).toInstant());
   }

   public static GameVersion tryDetectVersion() {
      try {
         InputStream var0 = DetectedVersion.class.getResourceAsStream("/version.json");
         Throwable var1 = null;

         GameVersion var2;
         try {
            if (var0 != null) {
               InputStreamReader var35 = new InputStreamReader(var0);
               Throwable var3 = null;

               try {
                  Object var4;
                  try {
                     var4 = new DetectedVersion(GsonHelper.parse((Reader)var35));
                     return (GameVersion)var4;
                  } catch (Throwable var30) {
                     var4 = var30;
                     var3 = var30;
                     throw var30;
                  }
               } finally {
                  if (var35 != null) {
                     if (var3 != null) {
                        try {
                           var35.close();
                        } catch (Throwable var29) {
                           var3.addSuppressed(var29);
                        }
                     } else {
                        var35.close();
                     }
                  }

               }
            }

            LOGGER.warn("Missing version information!");
            var2 = BUILT_IN;
         } catch (Throwable var32) {
            var1 = var32;
            throw var32;
         } finally {
            if (var0 != null) {
               if (var1 != null) {
                  try {
                     var0.close();
                  } catch (Throwable var28) {
                     var1.addSuppressed(var28);
                  }
               } else {
                  var0.close();
               }
            }

         }

         return var2;
      } catch (JsonParseException | IOException var34) {
         throw new IllegalStateException("Game version information is corrupt", var34);
      }
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public String getReleaseTarget() {
      return this.releaseTarget;
   }

   public int getWorldVersion() {
      return this.worldVersion;
   }

   public int getProtocolVersion() {
      return this.protocolVersion;
   }

   public int getPackVersion(PackType var1) {
      return var1 == PackType.DATA ? this.dataPackVersion : this.resourcePackVersion;
   }

   public Date getBuildTime() {
      return this.buildTime;
   }

   public boolean isStable() {
      return this.stable;
   }
}
