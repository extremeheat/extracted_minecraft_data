package net.minecraft.tags;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagCollection<T> {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = new Gson();
   private static final int PATH_SUFFIX_LENGTH = ".json".length();
   private Map<ResourceLocation, Tag<T>> tags = ImmutableMap.of();
   private final Function<ResourceLocation, Optional<T>> idToValue;
   private final String directory;
   private final boolean ordered;
   private final String name;

   public TagCollection(Function<ResourceLocation, Optional<T>> var1, String var2, boolean var3, String var4) {
      super();
      this.idToValue = var1;
      this.directory = var2;
      this.ordered = var3;
      this.name = var4;
   }

   @Nullable
   public Tag<T> getTag(ResourceLocation var1) {
      return (Tag)this.tags.get(var1);
   }

   public Tag<T> getTagOrEmpty(ResourceLocation var1) {
      Tag var2 = (Tag)this.tags.get(var1);
      return var2 == null ? new Tag(var1) : var2;
   }

   public Collection<ResourceLocation> getAvailableTags() {
      return this.tags.keySet();
   }

   public Collection<ResourceLocation> getMatchingTags(T var1) {
      ArrayList var2 = Lists.newArrayList();
      Iterator var3 = this.tags.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         if (((Tag)var4.getValue()).contains(var1)) {
            var2.add(var4.getKey());
         }
      }

      return var2;
   }

   public CompletableFuture<Map<ResourceLocation, Tag.Builder<T>>> prepare(ResourceManager var1, Executor var2) {
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
                              LOGGER.error("Couldn't load {} tag list {} from {} in data pack {} as it's empty or null", this.name, var6, var4, var8.getSourceName());
                           } else {
                              ((Tag.Builder)var2.computeIfAbsent(var6, (var1x) -> {
                                 return (Tag.Builder)Util.make(Tag.Builder.tag(), (var1) -> {
                                    var1.keepOrder(this.ordered);
                                 });
                              })).addFromJson(this.idToValue, var13);
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

   public void load(Map<ResourceLocation, Tag.Builder<T>> var1) {
      HashMap var2 = Maps.newHashMap();

      while(!var1.isEmpty()) {
         boolean var3 = false;
         Iterator var4 = var1.entrySet().iterator();

         while(var4.hasNext()) {
            Entry var5 = (Entry)var4.next();
            Tag.Builder var6 = (Tag.Builder)var5.getValue();
            var2.getClass();
            if (var6.canBuild(var2::get)) {
               var3 = true;
               ResourceLocation var7 = (ResourceLocation)var5.getKey();
               var2.put(var7, var6.build(var7));
               var4.remove();
            }
         }

         if (!var3) {
            var1.forEach((var1x, var2x) -> {
               LOGGER.error("Couldn't load {} tag {} as it either references another tag that doesn't exist, or ultimately references itself", this.name, var1x);
            });
            break;
         }
      }

      var1.forEach((var1x, var2x) -> {
         Tag var10000 = (Tag)var2.put(var1x, var2x.build(var1x));
      });
      this.replace(var2);
   }

   protected void replace(Map<ResourceLocation, Tag<T>> var1) {
      this.tags = ImmutableMap.copyOf(var1);
   }

   public Map<ResourceLocation, Tag<T>> getAllTags() {
      return this.tags;
   }
}
