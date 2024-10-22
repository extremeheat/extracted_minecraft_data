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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
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
      this(var1.createSerializationContext(JsonOps.INSTANCE), var2, var3);
   }

   protected SimpleJsonResourceReloadListener(Codec<T> var1, String var2) {
      this(JsonOps.INSTANCE, var1, var2);
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

      for (Entry var7 : var5.listMatchingResources(var0).entrySet()) {
         ResourceLocation var8 = (ResourceLocation)var7.getKey();
         ResourceLocation var9 = var5.fileToId(var8);

         try (BufferedReader var10 = ((Resource)var7.getValue()).openAsReader()) {
            var3.parse(var2, JsonParser.parseReader(var10)).ifSuccess(var2x -> {
               if (var4.putIfAbsent(var9, var2x) != null) {
                  throw new IllegalStateException("Duplicate data file ignored with ID " + var9);
               }
            }).ifError(var2x -> LOGGER.error("Couldn't parse data file '{}' from '{}': {}", new Object[]{var9, var8, var2x}));
         } catch (IllegalArgumentException | IOException | JsonParseException var15) {
            LOGGER.error("Couldn't parse data file '{}' from '{}'", new Object[]{var9, var8, var15});
         }
      }
   }
}
