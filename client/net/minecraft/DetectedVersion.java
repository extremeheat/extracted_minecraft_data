package net.minecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.DataVersion;
import org.slf4j.Logger;

public class DetectedVersion {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final WorldVersion BUILT_IN = createFromConstants();

   public DetectedVersion() {
      super();
   }

   private static WorldVersion createFromConstants() {
      return new WorldVersion.Simple(UUID.randomUUID().toString().replaceAll("-", ""), "1.21.7 Release Candidate 2", new DataVersion(4437, "main"), SharedConstants.getProtocolVersion(), 64, 81, new Date(), false);
   }

   private static WorldVersion createFromJson(JsonObject var0) {
      JsonObject var1 = GsonHelper.getAsJsonObject(var0, "pack_version");
      return new WorldVersion.Simple(GsonHelper.getAsString(var0, "id"), GsonHelper.getAsString(var0, "name"), new DataVersion(GsonHelper.getAsInt(var0, "world_version"), GsonHelper.getAsString(var0, "series_id", "main")), GsonHelper.getAsInt(var0, "protocol_version"), GsonHelper.getAsInt(var1, "resource"), GsonHelper.getAsInt(var1, "data"), Date.from(ZonedDateTime.parse(GsonHelper.getAsString(var0, "build_time")).toInstant()), GsonHelper.getAsBoolean(var0, "stable"));
   }

   public static WorldVersion tryDetectVersion() {
      try {
         InputStream var0 = DetectedVersion.class.getResourceAsStream("/version.json");

         WorldVersion var9;
         label63: {
            WorldVersion var2;
            try {
               if (var0 == null) {
                  LOGGER.warn("Missing version information!");
                  var9 = BUILT_IN;
                  break label63;
               }

               InputStreamReader var1 = new InputStreamReader(var0);

               try {
                  var2 = createFromJson(GsonHelper.parse((Reader)var1));
               } catch (Throwable var6) {
                  try {
                     var1.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }

                  throw var6;
               }

               var1.close();
            } catch (Throwable var7) {
               if (var0 != null) {
                  try {
                     var0.close();
                  } catch (Throwable var4) {
                     var7.addSuppressed(var4);
                  }
               }

               throw var7;
            }

            if (var0 != null) {
               var0.close();
            }

            return var2;
         }

         if (var0 != null) {
            var0.close();
         }

         return var9;
      } catch (JsonParseException | IOException var8) {
         throw new IllegalStateException("Game version information is corrupt", var8);
      }
   }
}
