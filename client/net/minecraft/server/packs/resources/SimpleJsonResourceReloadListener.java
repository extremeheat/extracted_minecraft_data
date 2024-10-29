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
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public abstract class SimpleJsonResourceReloadListener<T> extends SimplePreparableReloadListener<Map<ResourceLocation, T>> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final DynamicOps<JsonElement> ops;
   private final Codec<T> codec;
   private final String directory;

   protected SimpleJsonResourceReloadListener(HolderLookup.Provider var1, Codec<T> var2, String var3) {
      this((DynamicOps)var1.createSerializationContext(JsonOps.INSTANCE), var2, var3);
   }

   protected SimpleJsonResourceReloadListener(Codec<T> var1, String var2) {
      this((DynamicOps)JsonOps.INSTANCE, var1, var2);
   }

   private SimpleJsonResourceReloadListener(DynamicOps<JsonElement> var1, Codec<T> var2, String var3) {
      super();
      this.ops = var1;
      this.codec = var2;
      this.directory = var3;
   }

   protected Map<ResourceLocation, T> prepare(ResourceManager var1, ProfilerFiller var2) {
      HashMap var3 = new HashMap();
      scanDirectory(var1, this.directory, this.ops, this.codec, var3);
      return var3;
   }

   public static <T> void scanDirectory(ResourceManager var0, String var1, DynamicOps<JsonElement> var2, Codec<T> var3, Map<ResourceLocation, T> var4) {
      FileToIdConverter var5 = FileToIdConverter.json(var1);
      Iterator var6 = var5.listMatchingResources(var0).entrySet().iterator();

      while(var6.hasNext()) {
         Map.Entry var7 = (Map.Entry)var6.next();
         ResourceLocation var8 = (ResourceLocation)var7.getKey();
         ResourceLocation var9 = var5.fileToId(var8);

         try {
            BufferedReader var10 = ((Resource)var7.getValue()).openAsReader();

            try {
               var3.parse(var2, JsonParser.parseReader(var10)).ifSuccess((var2x) -> {
                  if (var4.putIfAbsent(var9, var2x) != null) {
                     throw new IllegalStateException("Duplicate data file ignored with ID " + String.valueOf(var9));
                  }
               }).ifError((var2x) -> {
                  LOGGER.error("Couldn't parse data file '{}' from '{}': {}", new Object[]{var9, var8, var2x});
               });
            } catch (Throwable var14) {
               if (var10 != null) {
                  try {
                     ((Reader)var10).close();
                  } catch (Throwable var13) {
                     var14.addSuppressed(var13);
                  }
               }

               throw var14;
            }

            if (var10 != null) {
               ((Reader)var10).close();
            }
         } catch (IllegalArgumentException | IOException | JsonParseException var15) {
            LOGGER.error("Couldn't parse data file '{}' from '{}'", new Object[]{var9, var8, var15});
         }
      }

   }

   // $FF: synthetic method
   protected Object prepare(final ResourceManager var1, final ProfilerFiller var2) {
      return this.prepare(var1, var2);
   }
}
