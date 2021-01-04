package net.minecraft.server.packs.resources;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class SimpleJsonResourceReloadListener extends SimplePreparableReloadListener<Map<ResourceLocation, JsonObject>> {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int PATH_SUFFIX_LENGTH = ".json".length();
   private final Gson gson;
   private final String directory;

   public SimpleJsonResourceReloadListener(Gson var1, String var2) {
      super();
      this.gson = var1;
      this.directory = var2;
   }

   protected Map<ResourceLocation, JsonObject> prepare(ResourceManager var1, ProfilerFiller var2) {
      HashMap var3 = Maps.newHashMap();
      int var4 = this.directory.length() + 1;
      Iterator var5 = var1.listResources(this.directory, (var0) -> {
         return var0.endsWith(".json");
      }).iterator();

      while(var5.hasNext()) {
         ResourceLocation var6 = (ResourceLocation)var5.next();
         String var7 = var6.getPath();
         ResourceLocation var8 = new ResourceLocation(var6.getNamespace(), var7.substring(var4, var7.length() - PATH_SUFFIX_LENGTH));

         try {
            Resource var9 = var1.getResource(var6);
            Throwable var10 = null;

            try {
               InputStream var11 = var9.getInputStream();
               Throwable var12 = null;

               try {
                  BufferedReader var13 = new BufferedReader(new InputStreamReader(var11, StandardCharsets.UTF_8));
                  Throwable var14 = null;

                  try {
                     JsonObject var15 = (JsonObject)GsonHelper.fromJson(this.gson, (Reader)var13, (Class)JsonObject.class);
                     if (var15 != null) {
                        JsonObject var16 = (JsonObject)var3.put(var8, var15);
                        if (var16 != null) {
                           throw new IllegalStateException("Duplicate data file ignored with ID " + var8);
                        }
                     } else {
                        LOGGER.error("Couldn't load data file {} from {} as it's null or empty", var8, var6);
                     }
                  } catch (Throwable var62) {
                     var14 = var62;
                     throw var62;
                  } finally {
                     if (var13 != null) {
                        if (var14 != null) {
                           try {
                              var13.close();
                           } catch (Throwable var61) {
                              var14.addSuppressed(var61);
                           }
                        } else {
                           var13.close();
                        }
                     }

                  }
               } catch (Throwable var64) {
                  var12 = var64;
                  throw var64;
               } finally {
                  if (var11 != null) {
                     if (var12 != null) {
                        try {
                           var11.close();
                        } catch (Throwable var60) {
                           var12.addSuppressed(var60);
                        }
                     } else {
                        var11.close();
                     }
                  }

               }
            } catch (Throwable var66) {
               var10 = var66;
               throw var66;
            } finally {
               if (var9 != null) {
                  if (var10 != null) {
                     try {
                        var9.close();
                     } catch (Throwable var59) {
                        var10.addSuppressed(var59);
                     }
                  } else {
                     var9.close();
                  }
               }

            }
         } catch (IllegalArgumentException | IOException | JsonParseException var68) {
            LOGGER.error("Couldn't parse data file {} from {}", var8, var6, var68);
         }
      }

      return var3;
   }

   // $FF: synthetic method
   protected Object prepare(ResourceManager var1, ProfilerFiller var2) {
      return this.prepare(var1, var2);
   }
}
