package net.minecraft.client.resources;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class AssetIndex {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Map<String, File> rootFiles = Maps.newHashMap();
   private final Map<ResourceLocation, File> namespacedFiles = Maps.newHashMap();

   protected AssetIndex() {
      super();
   }

   public AssetIndex(File var1, String var2) {
      super();
      File var3 = new File(var1, "objects");
      File var4 = new File(var1, "indexes/" + var2 + ".json");
      BufferedReader var5 = null;

      try {
         var5 = Files.newReader(var4, StandardCharsets.UTF_8);
         JsonObject var6 = GsonHelper.parse(var5);
         JsonObject var7 = GsonHelper.getAsJsonObject(var6, "objects", null);
         if (var7 != null) {
            for(Entry var9 : var7.entrySet()) {
               JsonObject var10 = (JsonObject)var9.getValue();
               String var11 = (String)var9.getKey();
               String[] var12 = var11.split("/", 2);
               String var13 = GsonHelper.getAsString(var10, "hash");
               File var14 = new File(var3, var13.substring(0, 2) + "/" + var13);
               if (var12.length == 1) {
                  this.rootFiles.put(var12[0], var14);
               } else {
                  this.namespacedFiles.put(new ResourceLocation(var12[0], var12[1]), var14);
               }
            }
         }
      } catch (JsonParseException var19) {
         LOGGER.error("Unable to parse resource index file: {}", var4);
      } catch (FileNotFoundException var20) {
         LOGGER.error("Can't find the resource index file: {}", var4);
      } finally {
         IOUtils.closeQuietly(var5);
      }
   }

   @Nullable
   public File getFile(ResourceLocation var1) {
      return this.namespacedFiles.get(var1);
   }

   @Nullable
   public File getRootFile(String var1) {
      return this.rootFiles.get(var1);
   }

   public Collection<ResourceLocation> getFiles(String var1, String var2, Predicate<ResourceLocation> var3) {
      return this.namespacedFiles.keySet().stream().filter(var3x -> {
         String var4 = var3x.getPath();
         return var3x.getNamespace().equals(var2) && !var4.endsWith(".mcmeta") && var4.startsWith(var1 + "/") && var3.test(var3x);
      }).collect(Collectors.toList());
   }
}
