package net.minecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.bridge.game.GameVersion;
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
   private final String id;
   private final String name;
   private final boolean stable;
   private final int worldVersion;
   private final int protocolVersion;
   private final int packVersion;
   private final Date buildTime;
   private final String releaseTarget;

   public DetectedVersion() {
      this.id = UUID.randomUUID().toString().replaceAll("-", "");
      this.name = "1.15.1";
      this.stable = true;
      this.worldVersion = 2227;
      this.protocolVersion = 575;
      this.packVersion = 5;
      this.buildTime = new Date();
      this.releaseTarget = "1.15.1";
   }

   protected DetectedVersion(JsonObject var1) {
      this.id = GsonHelper.getAsString(var1, "id");
      this.name = GsonHelper.getAsString(var1, "name");
      this.releaseTarget = GsonHelper.getAsString(var1, "release_target");
      this.stable = GsonHelper.getAsBoolean(var1, "stable");
      this.worldVersion = GsonHelper.getAsInt(var1, "world_version");
      this.protocolVersion = GsonHelper.getAsInt(var1, "protocol_version");
      this.packVersion = GsonHelper.getAsInt(var1, "pack_version");
      this.buildTime = Date.from(ZonedDateTime.parse(GsonHelper.getAsString(var1, "build_time")).toInstant());
   }

   public static GameVersion tryDetectVersion() {
      try {
         InputStream var0 = DetectedVersion.class.getResourceAsStream("/version.json");
         Throwable var1 = null;

         Object var4;
         try {
            if (var0 == null) {
               LOGGER.warn("Missing version information!");
               DetectedVersion var35 = new DetectedVersion();
               return var35;
            }

            InputStreamReader var2 = new InputStreamReader(var0);
            Throwable var3 = null;

            try {
               var4 = new DetectedVersion(GsonHelper.parse((Reader)var2));
            } catch (Throwable var30) {
               var4 = var30;
               var3 = var30;
               throw var30;
            } finally {
               if (var2 != null) {
                  if (var3 != null) {
                     try {
                        var2.close();
                     } catch (Throwable var29) {
                        var3.addSuppressed(var29);
                     }
                  } else {
                     var2.close();
                  }
               }

            }
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

         return (GameVersion)var4;
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

   public int getPackVersion() {
      return this.packVersion;
   }

   public Date getBuildTime() {
      return this.buildTime;
   }

   public boolean isStable() {
      return this.stable;
   }
}
