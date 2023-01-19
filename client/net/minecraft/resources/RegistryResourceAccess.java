package net.minecraft.resources;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.DataResult.PartialResult;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

public interface RegistryResourceAccess {
   <E> Map<ResourceKey<E>, RegistryResourceAccess.EntryThunk<E>> listResources(ResourceKey<? extends Registry<E>> var1);

   <E> Optional<RegistryResourceAccess.EntryThunk<E>> getResource(ResourceKey<E> var1);

   static RegistryResourceAccess forResourceManager(final ResourceManager var0) {
      return new RegistryResourceAccess() {
         private static final String JSON = ".json";

         @Override
         public <E> Map<ResourceKey<E>, RegistryResourceAccess.EntryThunk<E>> listResources(ResourceKey<? extends Registry<E>> var1) {
            String var2 = registryDirPath(var1.location());
            HashMap var3 = Maps.newHashMap();
            var0.listResources(var2, var0xx -> var0xx.getPath().endsWith(".json")).forEach((var4, var5) -> {
               String var6 = var4.getPath();
               String var7 = var6.substring(var2.length() + 1, var6.length() - ".json".length());
               ResourceKey var8 = ResourceKey.create(var1, new ResourceLocation(var4.getNamespace(), var7));
               var3.put(var8, (RegistryResourceAccess.EntryThunk<>)(var3xx, var4x) -> {
                  try {
                     DataResult var6x;
                     try (BufferedReader var5x = var5.openAsReader()) {
                        var6x = this.decodeElement(var3xx, var4x, var5x);
                     }

                     return var6x;
                  } catch (JsonIOException | JsonSyntaxException | IOException var10) {
                     return DataResult.error("Failed to parse " + var4 + " file: " + var10.getMessage());
                  }
               });
            });
            return var3;
         }

         @Override
         public <E> Optional<RegistryResourceAccess.EntryThunk<E>> getResource(ResourceKey<E> var1) {
            ResourceLocation var2 = elementPath(var1);
            return var0.getResource(var2).map(var2x -> (var3, var4) -> {
                  try {
                     DataResult var6;
                     try (BufferedReader var5 = var2x.openAsReader()) {
                        var6 = this.decodeElement(var3, var4, var5);
                     }

                     return var6;
                  } catch (JsonIOException | JsonSyntaxException | IOException var10) {
                     return DataResult.error("Failed to parse " + var2 + " file: " + var10.getMessage());
                  }
               });
         }

         private <E> DataResult<RegistryResourceAccess.ParsedEntry<E>> decodeElement(DynamicOps<JsonElement> var1, Decoder<E> var2, Reader var3) throws IOException {
            JsonElement var4 = JsonParser.parseReader(var3);
            return var2.parse(var1, var4).map(RegistryResourceAccess.ParsedEntry::createWithoutId);
         }

         private static String registryDirPath(ResourceLocation var0x) {
            return var0x.getPath();
         }

         private static <E> ResourceLocation elementPath(ResourceKey<E> var0x) {
            return new ResourceLocation(var0x.location().getNamespace(), registryDirPath(var0x.registry()) + "/" + var0x.location().getPath() + ".json");
         }

         @Override
         public String toString() {
            return "ResourceAccess[" + var0 + "]";
         }
      };
   }

   @FunctionalInterface
   public interface EntryThunk<E> {
      DataResult<RegistryResourceAccess.ParsedEntry<E>> parseElement(DynamicOps<JsonElement> var1, Decoder<E> var2);
   }

   public static final class InMemoryStorage implements RegistryResourceAccess {
      private static final Logger LOGGER = LogUtils.getLogger();
      private final Map<ResourceKey<?>, RegistryResourceAccess.InMemoryStorage.Entry> entries = Maps.newIdentityHashMap();

      public InMemoryStorage() {
         super();
      }

      public <E> void add(RegistryAccess var1, ResourceKey<E> var2, Encoder<E> var3, int var4, E var5, Lifecycle var6) {
         DataResult var7 = var3.encodeStart(RegistryOps.create(JsonOps.INSTANCE, var1), var5);
         Optional var8 = var7.error();
         if (var8.isPresent()) {
            LOGGER.error("Error adding element: {}", ((PartialResult)var8.get()).message());
         } else {
            this.entries.put(var2, new RegistryResourceAccess.InMemoryStorage.Entry((JsonElement)var7.result().get(), var4, var6));
         }
      }

      @Override
      public <E> Map<ResourceKey<E>, RegistryResourceAccess.EntryThunk<E>> listResources(ResourceKey<? extends Registry<E>> var1) {
         return this.entries
            .entrySet()
            .stream()
            .filter(var1x -> var1x.getKey().isFor(var1))
            .collect(Collectors.toMap(var0 -> (ResourceKey)var0.getKey(), var0 -> var0.getValue()::parse));
      }

      @Override
      public <E> Optional<RegistryResourceAccess.EntryThunk<E>> getResource(ResourceKey<E> var1) {
         RegistryResourceAccess.InMemoryStorage.Entry var2 = this.entries.get(var1);
         if (var2 == null) {
            DataResult var3 = DataResult.error("Unknown element: " + var1);
            return Optional.of((var1x, var2x) -> var3);
         } else {
            return Optional.of(var2::parse);
         }
      }

      static record Entry(JsonElement a, int b, Lifecycle c) {
         private final JsonElement data;
         private final int id;
         private final Lifecycle lifecycle;

         Entry(JsonElement var1, int var2, Lifecycle var3) {
            super();
            this.data = var1;
            this.id = var2;
            this.lifecycle = var3;
         }

         public <E> DataResult<RegistryResourceAccess.ParsedEntry<E>> parse(DynamicOps<JsonElement> var1, Decoder<E> var2) {
            return var2.parse(var1, this.data).setLifecycle(this.lifecycle).map(var1x -> RegistryResourceAccess.ParsedEntry.createWithId((int)var1x, this.id));
         }
      }
   }

   public static record ParsedEntry<E>(E a, OptionalInt b) {
      private final E value;
      private final OptionalInt fixedId;

      public ParsedEntry(E var1, OptionalInt var2) {
         super();
         this.value = (E)var1;
         this.fixedId = var2;
      }

      public static <E> RegistryResourceAccess.ParsedEntry<E> createWithoutId(E var0) {
         return new RegistryResourceAccess.ParsedEntry<>((E)var0, OptionalInt.empty());
      }

      public static <E> RegistryResourceAccess.ParsedEntry<E> createWithId(E var0, int var1) {
         return new RegistryResourceAccess.ParsedEntry<>((E)var0, OptionalInt.of(var1));
      }
   }
}
