package net.minecraft.resources;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.DataResult.PartialResult;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface RegistryResourceAccess {
   <E> Collection<ResourceKey<E>> listResources(ResourceKey<? extends Registry<E>> var1);

   <E> Optional<DataResult<RegistryResourceAccess.ParsedEntry<E>>> parseElement(DynamicOps<JsonElement> var1, ResourceKey<? extends Registry<E>> var2, ResourceKey<E> var3, Decoder<E> var4);

   static RegistryResourceAccess forResourceManager(final ResourceManager var0) {
      return new RegistryResourceAccess() {
         private static final String JSON = ".json";

         public <E> Collection<ResourceKey<E>> listResources(ResourceKey<? extends Registry<E>> var1) {
            String var2 = registryDirPath(var1);
            HashSet var3 = new HashSet();
            var0.listResources(var2, (var0x) -> {
               return var0x.endsWith(".json");
            }).forEach((var3x) -> {
               String var4 = var3x.getPath();
               String var5 = var4.substring(var2.length() + 1, var4.length() - ".json".length());
               var3.add(ResourceKey.create(var1, new ResourceLocation(var3x.getNamespace(), var5)));
            });
            return var3;
         }

         public <E> Optional<DataResult<RegistryResourceAccess.ParsedEntry<E>>> parseElement(DynamicOps<JsonElement> var1, ResourceKey<? extends Registry<E>> var2, ResourceKey<E> var3, Decoder<E> var4) {
            ResourceLocation var5 = elementPath(var2, var3);
            if (!var0.hasResource(var5)) {
               return Optional.empty();
            } else {
               try {
                  Resource var6 = var0.getResource(var5);

                  Optional var9;
                  try {
                     InputStreamReader var7 = new InputStreamReader(var6.getInputStream(), StandardCharsets.UTF_8);

                     try {
                        JsonElement var8 = JsonParser.parseReader(var7);
                        var9 = Optional.of(var4.parse(var1, var8).map(RegistryResourceAccess.ParsedEntry::createWithoutId));
                     } catch (Throwable var12) {
                        try {
                           var7.close();
                        } catch (Throwable var11) {
                           var12.addSuppressed(var11);
                        }

                        throw var12;
                     }

                     var7.close();
                  } catch (Throwable var13) {
                     if (var6 != null) {
                        try {
                           var6.close();
                        } catch (Throwable var10) {
                           var13.addSuppressed(var10);
                        }
                     }

                     throw var13;
                  }

                  if (var6 != null) {
                     var6.close();
                  }

                  return var9;
               } catch (JsonIOException | JsonSyntaxException | IOException var14) {
                  return Optional.of(DataResult.error("Failed to parse " + var5 + " file: " + var14.getMessage()));
               }
            }
         }

         private static String registryDirPath(ResourceKey<? extends Registry<?>> var0x) {
            return var0x.location().getPath();
         }

         private static <E> ResourceLocation elementPath(ResourceKey<? extends Registry<E>> var0x, ResourceKey<E> var1) {
            String var10002 = var1.location().getNamespace();
            String var10003 = registryDirPath(var0x);
            return new ResourceLocation(var10002, var10003 + "/" + var1.location().getPath() + ".json");
         }

         public String toString() {
            return "ResourceAccess[" + var0 + "]";
         }
      };
   }

   public static final class InMemoryStorage implements RegistryResourceAccess {
      private static final Logger LOGGER = LogManager.getLogger();
      private final Map<ResourceKey<?>, RegistryResourceAccess.InMemoryStorage.Entry> entries = Maps.newIdentityHashMap();

      public InMemoryStorage() {
         super();
      }

      public <E> void add(RegistryAccess.RegistryHolder var1, ResourceKey<E> var2, Encoder<E> var3, int var4, E var5, Lifecycle var6) {
         DataResult var7 = var3.encodeStart(RegistryWriteOps.create(JsonOps.INSTANCE, var1), var5);
         Optional var8 = var7.error();
         if (var8.isPresent()) {
            LOGGER.error("Error adding element: {}", ((PartialResult)var8.get()).message());
         } else {
            this.entries.put(var2, new RegistryResourceAccess.InMemoryStorage.Entry((JsonElement)var7.result().get(), var4, var6));
         }

      }

      public <E> Collection<ResourceKey<E>> listResources(ResourceKey<? extends Registry<E>> var1) {
         return (Collection)this.entries.keySet().stream().flatMap((var1x) -> {
            return var1x.cast(var1).stream();
         }).collect(Collectors.toList());
      }

      public <E> Optional<DataResult<RegistryResourceAccess.ParsedEntry<E>>> parseElement(DynamicOps<JsonElement> var1, ResourceKey<? extends Registry<E>> var2, ResourceKey<E> var3, Decoder<E> var4) {
         RegistryResourceAccess.InMemoryStorage.Entry var5 = (RegistryResourceAccess.InMemoryStorage.Entry)this.entries.get(var3);
         return var5 == null ? Optional.of(DataResult.error("Unknown element: " + var3)) : Optional.of(var4.parse(var1, var5.data).setLifecycle(var5.lifecycle).map((var1x) -> {
            return RegistryResourceAccess.ParsedEntry.createWithId(var1x, var5.field_265);
         }));
      }

      private static record Entry(JsonElement a, int b, Lifecycle c) {
         final JsonElement data;
         // $FF: renamed from: id int
         final int field_265;
         final Lifecycle lifecycle;

         Entry(JsonElement var1, int var2, Lifecycle var3) {
            super();
            this.data = var1;
            this.field_265 = var2;
            this.lifecycle = var3;
         }

         public JsonElement data() {
            return this.data;
         }

         // $FF: renamed from: id () int
         public int method_65() {
            return this.field_265;
         }

         public Lifecycle lifecycle() {
            return this.lifecycle;
         }
      }
   }

   public static record ParsedEntry<E>(E a, OptionalInt b) {
      private final E value;
      private final OptionalInt fixedId;

      public ParsedEntry(E var1, OptionalInt var2) {
         super();
         this.value = var1;
         this.fixedId = var2;
      }

      public static <E> RegistryResourceAccess.ParsedEntry<E> createWithoutId(E var0) {
         return new RegistryResourceAccess.ParsedEntry(var0, OptionalInt.empty());
      }

      public static <E> RegistryResourceAccess.ParsedEntry<E> createWithId(E var0, int var1) {
         return new RegistryResourceAccess.ParsedEntry(var0, OptionalInt.of(var1));
      }

      public E value() {
         return this.value;
      }

      public OptionalInt fixedId() {
         return this.fixedId;
      }
   }
}
