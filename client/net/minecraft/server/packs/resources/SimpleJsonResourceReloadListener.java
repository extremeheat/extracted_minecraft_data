package net.minecraft.server.packs.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public abstract class SimpleJsonResourceReloadListener<T> extends SimplePreparableReloadListener<Map<ResourceLocation, T>> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final DynamicOps<JsonElement> ops;
   private final Codec<T> codec;
   private final FileToIdConverter lister;

   protected SimpleJsonResourceReloadListener(HolderLookup.Provider var1, Codec<T> var2, ResourceKey<? extends Registry<T>> var3) {
      this((DynamicOps)var1.createSerializationContext(JsonOps.INSTANCE), var2, (FileToIdConverter)FileToIdConverter.registry(var3));
   }

   protected SimpleJsonResourceReloadListener(Codec<T> var1, FileToIdConverter var2) {
      this((DynamicOps)JsonOps.INSTANCE, var1, (FileToIdConverter)var2);
   }

   private SimpleJsonResourceReloadListener(DynamicOps<JsonElement> var1, Codec<T> var2, FileToIdConverter var3) {
      super();
      this.ops = var1;
      this.codec = var2;
      this.lister = var3;
   }

   protected Map<ResourceLocation, T> prepare(ResourceManager var1, ProfilerFiller var2) {
      HashMap var3 = new HashMap();
      scanDirectory(var1, (FileToIdConverter)this.lister, this.ops, this.codec, var3);
      return var3;
   }

   public static <T> void scanDirectory(ResourceManager var0, ResourceKey<? extends Registry<T>> var1, DynamicOps<JsonElement> var2, Codec<T> var3, Map<ResourceLocation, T> var4) {
      scanDirectory(var0, FileToIdConverter.registry(var1), var2, var3, var4);
   }

   public static <T> void scanDirectory(ResourceManager var0, FileToIdConverter var1, DynamicOps<JsonElement> var2, Codec<T> var3, Map<ResourceLocation, T> var4) {
      Iterator var5 = var1.listMatchingResources(var0).entrySet().iterator();

      while(var5.hasNext()) {
         Map.Entry var6 = (Map.Entry)var5.next();
         ResourceLocation var7 = (ResourceLocation)var6.getKey();
         ResourceLocation var8 = var1.fileToId(var7);

         try {
            BufferedReader var9 = ((Resource)var6.getValue()).openAsReader();

            try {
               var3.parse(var2, JsonParser.parseReader(var9)).ifSuccess((var2x) -> {
                  if (var4.putIfAbsent(var8, var2x) != null) {
                     throw new IllegalStateException("Duplicate data file ignored with ID " + String.valueOf(var8));
                  }
               }).ifError((var2x) -> {
                  LOGGER.error("Couldn't parse data file '{}' from '{}': {}", new Object[]{var8, var7, var2x});
               });
            } catch (Throwable var13) {
               if (var9 != null) {
                  try {
                     ((Reader)var9).close();
                  } catch (Throwable var12) {
                     var13.addSuppressed(var12);
                  }
               }

               throw var13;
            }

            if (var9 != null) {
               ((Reader)var9).close();
            }
         } catch (IllegalArgumentException | IOException | JsonParseException var14) {
            LOGGER.error("Couldn't parse data file '{}' from '{}'", new Object[]{var8, var7, var14});
         }
      }

   }

   // $FF: synthetic method
   protected Object prepare(final ResourceManager var1, final ProfilerFiller var2) {
      return this.prepare(var1, var2);
   }
}
