package net.minecraft.client.resources;

import com.google.common.base.Splitter;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
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

   public static Path createIndexFs(Path var0, String var1) {
      Path var2 = var0.resolve("objects");
      LinkFileSystem.Builder var3 = LinkFileSystem.builder();
      Path var4 = var0.resolve("indexes/" + var1 + ".json");

      try {
         BufferedReader var5 = Files.newBufferedReader(var4, StandardCharsets.UTF_8);

         try {
            JsonObject var6 = GsonHelper.parse((Reader)var5);
            JsonObject var7 = GsonHelper.getAsJsonObject(var6, "objects", (JsonObject)null);
            if (var7 != null) {
               Iterator var8 = var7.entrySet().iterator();

               while(var8.hasNext()) {
                  Map.Entry var9 = (Map.Entry)var8.next();
                  JsonObject var10 = (JsonObject)var9.getValue();
                  String var11 = (String)var9.getKey();
                  List var12 = PATH_SPLITTER.splitToList(var11);
                  String var13 = GsonHelper.getAsString(var10, "hash");
                  String var10001 = var13.substring(0, 2);
                  Path var14 = var2.resolve(var10001 + "/" + var13);
                  var3.put(var12, var14);
               }
            }
         } catch (Throwable var16) {
            if (var5 != null) {
               try {
                  var5.close();
               } catch (Throwable var15) {
                  var16.addSuppressed(var15);
               }
            }

            throw var16;
         }

         if (var5 != null) {
            var5.close();
         }
      } catch (JsonParseException var17) {
         LOGGER.error("Unable to parse resource index file: {}", var4);
      } catch (IOException var18) {
         LOGGER.error("Can't open the resource index file: {}", var4);
      }

      return var3.build("index-" + var1).getPath("/");
   }
}
