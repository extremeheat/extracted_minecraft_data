package net.minecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.DataVersion;
import org.slf4j.Logger;

public class DetectedVersion {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final WorldVersion BUILT_IN = createBuiltIn(UUID.randomUUID().toString().replaceAll("-", ""), "Development Version");

   public DetectedVersion() {
      super();
   }

   public static WorldVersion createBuiltIn(String var0, String var1) {
      return createBuiltIn(var0, var1, true);
   }

   public static WorldVersion createBuiltIn(String var0, String var1, boolean var2) {
      return new WorldVersion.Simple(var0, var1, new DataVersion(4671, "main"), SharedConstants.getProtocolVersion(), PackFormat.of(75, 0), PackFormat.of(94, 1), new Date(), var2);
   }

   private static WorldVersion createFromJson(JsonObject var0) {
      JsonObject var1 = GsonHelper.getAsJsonObject(var0, "pack_version");
      return new WorldVersion.Simple(GsonHelper.getAsString(var0, "id"), GsonHelper.getAsString(var0, "name"), new DataVersion(GsonHelper.getAsInt(var0, "world_version"), GsonHelper.getAsString(var0, "series_id", "main")), GsonHelper.getAsInt(var0, "protocol_version"), PackFormat.of(GsonHelper.getAsInt(var1, "resource_major"), GsonHelper.getAsInt(var1, "resource_minor")), PackFormat.of(GsonHelper.getAsInt(var1, "data_major"), GsonHelper.getAsInt(var1, "data_minor")), Date.from(ZonedDateTime.parse(GsonHelper.getAsString(var0, "build_time")).toInstant()), GsonHelper.getAsBoolean(var0, "stable"));
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

               InputStreamReader var1 = new InputStreamReader(var0, StandardCharsets.UTF_8);

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
