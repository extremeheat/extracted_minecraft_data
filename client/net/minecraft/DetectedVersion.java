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

   public static WorldVersion createBuiltIn(final String id, final String name) {
      return createBuiltIn(id, name, false);
   }

   public static WorldVersion createBuiltIn(final String id, final String name, final boolean stable) {
      return new WorldVersion.Simple(id, name, new DataVersion(4778, "main"), SharedConstants.getProtocolVersion(), PackFormat.of(82, 0), PackFormat.of(99, 3), new Date(), stable);
   }

   private static WorldVersion createFromJson(final JsonObject root) {
      JsonObject packVersion = GsonHelper.getAsJsonObject(root, "pack_version");
      return new WorldVersion.Simple(GsonHelper.getAsString(root, "id"), GsonHelper.getAsString(root, "name"), new DataVersion(GsonHelper.getAsInt(root, "world_version"), GsonHelper.getAsString(root, "series_id", "main")), GsonHelper.getAsInt(root, "protocol_version"), PackFormat.of(GsonHelper.getAsInt(packVersion, "resource_major"), GsonHelper.getAsInt(packVersion, "resource_minor")), PackFormat.of(GsonHelper.getAsInt(packVersion, "data_major"), GsonHelper.getAsInt(packVersion, "data_minor")), Date.from(ZonedDateTime.parse(GsonHelper.getAsString(root, "build_time")).toInstant()), GsonHelper.getAsBoolean(root, "stable"));
   }

   public static WorldVersion tryDetectVersion() {
      try {
         InputStream stream = DetectedVersion.class.getResourceAsStream("/version.json");

         WorldVersion var9;
         label63: {
            WorldVersion var2;
            try {
               if (stream == null) {
                  LOGGER.warn("Missing version information!");
                  var9 = BUILT_IN;
                  break label63;
               }

               InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);

               try {
                  var2 = createFromJson(GsonHelper.parse((Reader)reader));
               } catch (Throwable var6) {
                  try {
                     reader.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }

                  throw var6;
               }

               reader.close();
            } catch (Throwable var7) {
               if (stream != null) {
                  try {
                     stream.close();
                  } catch (Throwable var4) {
                     var7.addSuppressed(var4);
                  }
               }

               throw var7;
            }

            if (stream != null) {
               stream.close();
            }

            return var2;
         }

         if (stream != null) {
            stream.close();
         }

         return var9;
      } catch (JsonParseException | IOException e) {
         throw new IllegalStateException("Game version information is corrupt", e);
      }
   }
}
