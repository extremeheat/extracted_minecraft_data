package net.minecraft.data;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

public interface DataProvider {
   ToIntFunction<String> FIXED_ORDER_FIELDS = (ToIntFunction)Util.make(new Object2IntOpenHashMap(), (var0) -> {
      var0.put("type", 0);
      var0.put("parent", 1);
      var0.defaultReturnValue(2);
   });
   Comparator<String> KEY_COMPARATOR = Comparator.comparingInt(FIXED_ORDER_FIELDS).thenComparing((var0) -> {
      return var0;
   });
   Logger LOGGER = LogUtils.getLogger();

   CompletableFuture<?> run(CachedOutput var1);

   String getName();

   static <T> CompletableFuture<?> saveAll(CachedOutput var0, Codec<T> var1, PackOutput.PathProvider var2, Map<ResourceLocation, T> var3) {
      Objects.requireNonNull(var2);
      return saveAll(var0, var1, var2::json, var3);
   }

   static <T, E> CompletableFuture<?> saveAll(CachedOutput var0, Codec<E> var1, Function<T, Path> var2, Map<T, E> var3) {
      return saveAll(var0, (var1x) -> {
         return (JsonElement)var1.encodeStart(JsonOps.INSTANCE, var1x).getOrThrow();
      }, var2, var3);
   }

   static <T, E> CompletableFuture<?> saveAll(CachedOutput var0, Function<E, JsonElement> var1, Function<T, Path> var2, Map<T, E> var3) {
      return CompletableFuture.allOf((CompletableFuture[])var3.entrySet().stream().map((var3x) -> {
         Path var4 = (Path)var2.apply(var3x.getKey());
         JsonElement var5 = (JsonElement)var1.apply(var3x.getValue());
         return saveStable(var0, var5, var4);
      }).toArray((var0x) -> {
         return new CompletableFuture[var0x];
      }));
   }

   static <T> CompletableFuture<?> saveStable(CachedOutput var0, HolderLookup.Provider var1, Codec<T> var2, T var3, Path var4) {
      RegistryOps var5 = var1.createSerializationContext(JsonOps.INSTANCE);
      return saveStable(var0, (DynamicOps)var5, var2, var3, var4);
   }

   static <T> CompletableFuture<?> saveStable(CachedOutput var0, Codec<T> var1, T var2, Path var3) {
      return saveStable(var0, (DynamicOps)JsonOps.INSTANCE, var1, var2, var3);
   }

   private static <T> CompletableFuture<?> saveStable(CachedOutput var0, DynamicOps<JsonElement> var1, Codec<T> var2, T var3, Path var4) {
      JsonElement var5 = (JsonElement)var2.encodeStart(var1, var3).getOrThrow();
      return saveStable(var0, var5, var4);
   }

   static CompletableFuture<?> saveStable(CachedOutput var0, JsonElement var1, Path var2) {
      return CompletableFuture.runAsync(() -> {
         try {
            ByteArrayOutputStream var3 = new ByteArrayOutputStream();
            HashingOutputStream var4 = new HashingOutputStream(Hashing.sha1(), var3);
            JsonWriter var5 = new JsonWriter(new OutputStreamWriter(var4, StandardCharsets.UTF_8));

            try {
               var5.setSerializeNulls(false);
               var5.setIndent("  ");
               GsonHelper.writeValue(var5, var1, KEY_COMPARATOR);
            } catch (Throwable var9) {
               try {
                  var5.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }

               throw var9;
            }

            var5.close();
            var0.writeIfNeeded(var2, var3.toByteArray(), var4.hash());
         } catch (IOException var10) {
            LOGGER.error("Failed to save file to {}", var2, var10);
         }

      }, Util.backgroundExecutor().forName("saveStable"));
   }

   @FunctionalInterface
   public interface Factory<T extends DataProvider> {
      T create(PackOutput var1);
   }
}
