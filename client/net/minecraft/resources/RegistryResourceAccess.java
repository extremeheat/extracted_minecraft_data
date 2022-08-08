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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

public interface RegistryResourceAccess {
   <E> Map<ResourceKey<E>, EntryThunk<E>> listResources(ResourceKey<? extends Registry<E>> var1);

   <E> Optional<EntryThunk<E>> getResource(ResourceKey<E> var1);

   static RegistryResourceAccess forResourceManager(final ResourceManager var0) {
      return new RegistryResourceAccess() {
         private static final String JSON = ".json";

         public <E> Map<ResourceKey<E>, EntryThunk<E>> listResources(ResourceKey<? extends Registry<E>> var1) {
            String var2 = registryDirPath(var1.location());
            HashMap var3 = Maps.newHashMap();
            var0.listResources(var2, (var0x) -> {
               return var0x.getPath().endsWith(".json");
            }).forEach((var4, var5) -> {
               String var6 = var4.getPath();
               String var7 = var6.substring(var2.length() + 1, var6.length() - ".json".length());
               ResourceKey var8 = ResourceKey.create(var1, new ResourceLocation(var4.getNamespace(), var7));
               var3.put(var8, (var3x, var4x) -> {
                  try {
                     BufferedReader var5x = var5.openAsReader();

                     DataResult var6;
                     try {
                        var6 = this.decodeElement(var3x, var4x, var5x);
                     } catch (Throwable var9) {
                        if (var5x != null) {
                           try {
                              var5x.close();
                           } catch (Throwable var8) {
                              var9.addSuppressed(var8);
                           }
                        }

                        throw var9;
                     }

                     if (var5x != null) {
                        var5x.close();
                     }

                     return var6;
                  } catch (JsonIOException | JsonSyntaxException | IOException var10) {
                     return DataResult.error("Failed to parse " + var4 + " file: " + var10.getMessage());
                  }
               });
            });
            return var3;
         }

         public <E> Optional<EntryThunk<E>> getResource(ResourceKey<E> var1) {
            ResourceLocation var2 = elementPath(var1);
            return var0.getResource(var2).map((var2x) -> {
               return (var3, var4) -> {
                  try {
                     BufferedReader var5 = var2x.openAsReader();

                     DataResult var6;
                     try {
                        var6 = this.decodeElement(var3, var4, var5);
                     } catch (Throwable var9) {
                        if (var5 != null) {
                           try {
                              var5.close();
                           } catch (Throwable var8) {
                              var9.addSuppressed(var8);
                           }
                        }

                        throw var9;
                     }

                     if (var5 != null) {
                        var5.close();
                     }

                     return var6;
                  } catch (JsonIOException | JsonSyntaxException | IOException var10) {
                     return DataResult.error("Failed to parse " + var2 + " file: " + var10.getMessage());
                  }
               };
            });
         }

         private <E> DataResult<ParsedEntry<E>> decodeElement(DynamicOps<JsonElement> var1, Decoder<E> var2, Reader var3) throws IOException {
            JsonElement var4 = JsonParser.parseReader(var3);
            return var2.parse(var1, var4).map(ParsedEntry::createWithoutId);
         }

         private static String registryDirPath(ResourceLocation var0x) {
            return var0x.getPath();
         }

         private static <E> ResourceLocation elementPath(ResourceKey<E> var0x) {
            String var10002 = var0x.location().getNamespace();
            String var10003 = registryDirPath(var0x.registry());
            return new ResourceLocation(var10002, var10003 + "/" + var0x.location().getPath() + ".json");
         }

         public String toString() {
            return "ResourceAccess[" + var0 + "]";
         }
      };
   }

   public static final class InMemoryStorage implements RegistryResourceAccess {
      private static final Logger LOGGER = LogUtils.getLogger();
      private final Map<ResourceKey<?>, Entry> entries = Maps.newIdentityHashMap();

      public InMemoryStorage() {
         super();
      }

      public <E> void add(RegistryAccess var1, ResourceKey<E> var2, Encoder<E> var3, int var4, E var5, Lifecycle var6) {
         DataResult var7 = var3.encodeStart(RegistryOps.create(JsonOps.INSTANCE, var1), var5);
         Optional var8 = var7.error();
         if (var8.isPresent()) {
            LOGGER.error("Error adding element: {}", ((DataResult.PartialResult)var8.get()).message());
         } else {
            this.entries.put(var2, new Entry((JsonElement)var7.result().get(), var4, var6));
         }

      }

      public <E> Map<ResourceKey<E>, EntryThunk<E>> listResources(ResourceKey<? extends Registry<E>> var1) {
         return (Map)this.entries.entrySet().stream().filter((var1x) -> {
            return ((ResourceKey)var1x.getKey()).isFor(var1);
         }).collect(Collectors.toMap((var0) -> {
            return (ResourceKey)var0.getKey();
         }, (var0) -> {
            Entry var10000 = (Entry)var0.getValue();
            Objects.requireNonNull(var10000);
            return var10000::parse;
         }));
      }

      public <E> Optional<EntryThunk<E>> getResource(ResourceKey<E> var1) {
         Entry var2 = (Entry)this.entries.get(var1);
         if (var2 == null) {
            DataResult var3 = DataResult.error("Unknown element: " + var1);
            return Optional.of((var1x, var2x) -> {
               return var3;
            });
         } else {
            Objects.requireNonNull(var2);
            return Optional.of(var2::parse);
         }
      }

      private static record Entry(JsonElement a, int b, Lifecycle c) {
         private final JsonElement data;
         private final int id;
         private final Lifecycle lifecycle;

         Entry(JsonElement var1, int var2, Lifecycle var3) {
            super();
            this.data = var1;
            this.id = var2;
            this.lifecycle = var3;
         }

         public <E> DataResult<ParsedEntry<E>> parse(DynamicOps<JsonElement> var1, Decoder<E> var2) {
            return var2.parse(var1, this.data).setLifecycle(this.lifecycle).map((var1x) -> {
               return RegistryResourceAccess.ParsedEntry.createWithId(var1x, this.id);
            });
         }

         public JsonElement data() {
            return this.data;
         }

         public int id() {
            return this.id;
         }

         public Lifecycle lifecycle() {
            return this.lifecycle;
         }
      }
   }

   @FunctionalInterface
   public interface EntryThunk<E> {
      DataResult<ParsedEntry<E>> parseElement(DynamicOps<JsonElement> var1, Decoder<E> var2);
   }

   public static record ParsedEntry<E>(E a, OptionalInt b) {
      private final E value;
      private final OptionalInt fixedId;

      public ParsedEntry(E var1, OptionalInt var2) {
         super();
         this.value = var1;
         this.fixedId = var2;
      }

      public static <E> ParsedEntry<E> createWithoutId(E var0) {
         return new ParsedEntry(var0, OptionalInt.empty());
      }

      public static <E> ParsedEntry<E> createWithId(E var0, int var1) {
         return new ParsedEntry(var0, OptionalInt.of(var1));
      }

      public E value() {
         return this.value;
      }

      public OptionalInt fixedId() {
         return this.fixedId;
      }
   }
}
