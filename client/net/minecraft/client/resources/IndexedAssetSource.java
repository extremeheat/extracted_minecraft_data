package net.minecraft.client.resources;

import com.google.common.base.Splitter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import net.minecraft.server.packs.linkfs.LinkFileSystem;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

public class IndexedAssetSource {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Splitter PATH_SPLITTER = Splitter.on('/');

   public IndexedAssetSource() {
      super();
   }

   public static Path createIndexFs(final Path assetsDirectory, final String index) {
      Path objectsDirectory = assetsDirectory.resolve("objects");
      LinkFileSystem.Builder builder = LinkFileSystem.builder();
      Path indexFile = assetsDirectory.resolve("indexes/" + index + ".json");

      try {
         BufferedReader reader = Files.newBufferedReader(indexFile, StandardCharsets.UTF_8);

         try {
            JsonObject root = GsonHelper.parse((Reader)reader);
            JsonObject objects = GsonHelper.getAsJsonObject(root, "objects", (JsonObject)null);
            if (objects != null) {
               for(Map.Entry<String, JsonElement> entry : objects.entrySet()) {
                  JsonObject object = (JsonObject)entry.getValue();
                  String filename = (String)entry.getKey();
                  List<String> path = PATH_SPLITTER.splitToList(filename);
                  String hash = GsonHelper.getAsString(object, "hash");
                  String var10001 = hash.substring(0, 2);
                  Path file = objectsDirectory.resolve(var10001 + "/" + hash);
                  builder.put(path, file);
               }
            }
         } catch (Throwable var16) {
            if (reader != null) {
               try {
                  reader.close();
               } catch (Throwable var15) {
                  var16.addSuppressed(var15);
               }
            }

            throw var16;
         }

         if (reader != null) {
            reader.close();
         }
      } catch (JsonParseException var17) {
         LOGGER.error("Unable to parse resource index file: {}", indexFile);
      } catch (IOException var18) {
         LOGGER.error("Can't open the resource index file: {}", indexFile);
      }

      return builder.build("index-" + index).getPath("/");
   }
}
