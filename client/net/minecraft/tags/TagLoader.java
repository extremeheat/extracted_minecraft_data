package net.minecraft.tags;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagLoader<T> {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = new Gson();
   private static final int PATH_SUFFIX_LENGTH = ".json".length();
   private final Function<ResourceLocation, Optional<T>> idToValue;
   private final String directory;
   private final String name;

   public TagLoader(Function<ResourceLocation, Optional<T>> var1, String var2, String var3) {
      super();
      this.idToValue = var1;
      this.directory = var2;
      this.name = var3;
   }

   public CompletableFuture<Map<ResourceLocation, Tag.Builder>> prepare(ResourceManager var1, Executor var2) {
      return CompletableFuture.supplyAsync(() -> {
         HashMap var2 = Maps.newHashMap();
         Iterator var3 = var1.listResources(this.directory, (var0) -> {
            return var0.endsWith(".json");
         }).iterator();

         while(var3.hasNext()) {
            ResourceLocation var4 = (ResourceLocation)var3.next();
            String var5 = var4.getPath();
            ResourceLocation var6 = new ResourceLocation(var4.getNamespace(), var5.substring(this.directory.length() + 1, var5.length() - PATH_SUFFIX_LENGTH));

            try {
               Iterator var7 = var1.getResources(var4).iterator();

               while(var7.hasNext()) {
                  Resource var8 = (Resource)var7.next();

                  try {
                     InputStream var9 = var8.getInputStream();
                     Throwable var10 = null;

                     try {
                        BufferedReader var11 = new BufferedReader(new InputStreamReader(var9, StandardCharsets.UTF_8));
                        Throwable var12 = null;

                        try {
                           JsonObject var13 = (JsonObject)GsonHelper.fromJson(GSON, (Reader)var11, (Class)JsonObject.class);
                           if (var13 == null) {
                              LOGGER.error("Couldn't load {} tag list {} from {} in data pack {} as it is empty or null", this.name, var6, var4, var8.getSourceName());
                           } else {
                              ((Tag.Builder)var2.computeIfAbsent(var6, (var0) -> {
                                 return Tag.Builder.tag();
                              })).addFromJson(var13, var8.getSourceName());
                           }
                        } catch (Throwable var53) {
                           var12 = var53;
                           throw var53;
                        } finally {
                           if (var11 != null) {
                              if (var12 != null) {
                                 try {
                                    var11.close();
                                 } catch (Throwable var52) {
                                    var12.addSuppressed(var52);
                                 }
                              } else {
                                 var11.close();
                              }
                           }

                        }
                     } catch (Throwable var55) {
                        var10 = var55;
                        throw var55;
                     } finally {
                        if (var9 != null) {
                           if (var10 != null) {
                              try {
                                 var9.close();
                              } catch (Throwable var51) {
                                 var10.addSuppressed(var51);
                              }
                           } else {
                              var9.close();
                           }
                        }

                     }
                  } catch (RuntimeException | IOException var57) {
                     LOGGER.error("Couldn't read {} tag list {} from {} in data pack {}", this.name, var6, var4, var8.getSourceName(), var57);
                  } finally {
                     IOUtils.closeQuietly(var8);
                  }
               }
            } catch (IOException var59) {
               LOGGER.error("Couldn't read {} tag list {} from {}", this.name, var6, var4, var59);
            }
         }

         return var2;
      }, var2);
   }

   public TagCollection<T> load(Map<ResourceLocation, Tag.Builder> var1) {
      HashMap var2 = Maps.newHashMap();
      Function var3 = var2::get;
      Function var4 = (var1x) -> {
         return ((Optional)this.idToValue.apply(var1x)).orElse((Object)null);
      };

      while(!var1.isEmpty()) {
         boolean var5 = false;
         Iterator var6 = var1.entrySet().iterator();

         while(var6.hasNext()) {
            Entry var7 = (Entry)var6.next();
            Optional var8 = ((Tag.Builder)var7.getValue()).build(var3, var4);
            if (var8.isPresent()) {
               var2.put(var7.getKey(), var8.get());
               var6.remove();
               var5 = true;
            }
         }

         if (!var5) {
            break;
         }
      }

      var1.forEach((var3x, var4x) -> {
         LOGGER.error("Couldn't load {} tag {} as it is missing following references: {}", this.name, var3x, var4x.getUnresolvedEntries(var3, var4).map(Objects::toString).collect(Collectors.joining(",")));
      });
      return TagCollection.of(var2);
   }
}
