package net.minecraft.resources;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.DataResult.PartialResult;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistryReadOps<T> extends DelegatingOps<T> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RegistryReadOps.ResourceAccess resources;
   private final RegistryAccess.RegistryHolder registryHolder;
   private final Map<ResourceKey<? extends Registry<?>>, RegistryReadOps.ReadCache<?>> readCache;
   private final RegistryReadOps<JsonElement> jsonOps;

   public static <T> RegistryReadOps<T> create(DynamicOps<T> var0, ResourceManager var1, RegistryAccess.RegistryHolder var2) {
      return create(var0, RegistryReadOps.ResourceAccess.forResourceManager(var1), var2);
   }

   public static <T> RegistryReadOps<T> create(DynamicOps<T> var0, RegistryReadOps.ResourceAccess var1, RegistryAccess.RegistryHolder var2) {
      RegistryReadOps var3 = new RegistryReadOps(var0, var1, var2, Maps.newIdentityHashMap());
      RegistryAccess.load(var2, var3);
      return var3;
   }

   private RegistryReadOps(DynamicOps<T> var1, RegistryReadOps.ResourceAccess var2, RegistryAccess.RegistryHolder var3, IdentityHashMap<ResourceKey<? extends Registry<?>>, RegistryReadOps.ReadCache<?>> var4) {
      super(var1);
      this.resources = var2;
      this.registryHolder = var3;
      this.readCache = var4;
      this.jsonOps = var1 == JsonOps.INSTANCE ? this : new RegistryReadOps(JsonOps.INSTANCE, var2, var3, var4);
   }

   protected <E> DataResult<Pair<Supplier<E>, T>> decodeElement(T var1, ResourceKey<? extends Registry<E>> var2, Codec<E> var3, boolean var4) {
      Optional var5 = this.registryHolder.registry(var2);
      if (!var5.isPresent()) {
         return DataResult.error("Unknown registry: " + var2);
      } else {
         WritableRegistry var6 = (WritableRegistry)var5.get();
         DataResult var7 = ResourceLocation.CODEC.decode(this.delegate, var1);
         if (!var7.result().isPresent()) {
            return !var4 ? DataResult.error("Inline definitions not allowed here") : var3.decode(this, var1).map((var0) -> {
               return var0.mapFirst((var0x) -> {
                  return () -> {
                     return var0x;
                  };
               });
            });
         } else {
            Pair var8 = (Pair)var7.result().get();
            ResourceLocation var9 = (ResourceLocation)var8.getFirst();
            return this.readAndRegisterElement(var2, var6, var3, var9).map((var1x) -> {
               return Pair.of(var1x, var8.getSecond());
            });
         }
      }
   }

   public <E> DataResult<MappedRegistry<E>> decodeElements(MappedRegistry<E> var1, ResourceKey<? extends Registry<E>> var2, Codec<E> var3) {
      Collection var4 = this.resources.listResources(var2);
      DataResult var5 = DataResult.success(var1, Lifecycle.stable());
      String var6 = var2.location().getPath() + "/";
      Iterator var7 = var4.iterator();

      while(var7.hasNext()) {
         ResourceLocation var8 = (ResourceLocation)var7.next();
         String var9 = var8.getPath();
         if (!var9.endsWith(".json")) {
            LOGGER.warn("Skipping resource {} since it is not a json file", var8);
         } else if (!var9.startsWith(var6)) {
            LOGGER.warn("Skipping resource {} since it does not have a registry name prefix", var8);
         } else {
            String var10 = var9.substring(var6.length(), var9.length() - ".json".length());
            ResourceLocation var11 = new ResourceLocation(var8.getNamespace(), var10);
            var5 = var5.flatMap((var4x) -> {
               return this.readAndRegisterElement(var2, var4x, var3, var11).map((var1) -> {
                  return var4x;
               });
            });
         }
      }

      return var5.setPartial(var1);
   }

   private <E> DataResult<Supplier<E>> readAndRegisterElement(ResourceKey<? extends Registry<E>> var1, WritableRegistry<E> var2, Codec<E> var3, ResourceLocation var4) {
      ResourceKey var5 = ResourceKey.create(var1, var4);
      RegistryReadOps.ReadCache var6 = this.readCache(var1);
      DataResult var7 = (DataResult)var6.values.get(var5);
      if (var7 != null) {
         return var7;
      } else {
         com.google.common.base.Supplier var8 = Suppliers.memoize(() -> {
            Object var2x = var2.get(var5);
            if (var2x == null) {
               throw new RuntimeException("Error during recursive registry parsing, element resolved too early: " + var5);
            } else {
               return var2x;
            }
         });
         var6.values.put(var5, DataResult.success(var8));
         DataResult var9 = this.resources.parseElement(this.jsonOps, var1, var5, var3);
         Optional var10 = var9.result();
         if (var10.isPresent()) {
            Pair var11 = (Pair)var10.get();
            var2.registerOrOverride((OptionalInt)var11.getSecond(), var5, var11.getFirst(), var9.lifecycle());
         }

         DataResult var12;
         if (!var10.isPresent() && var2.get(var5) != null) {
            var12 = DataResult.success(() -> {
               return var2.get(var5);
            }, Lifecycle.stable());
         } else {
            var12 = var9.map((var2x) -> {
               return () -> {
                  return var2.get(var5);
               };
            });
         }

         var6.values.put(var5, var12);
         return var12;
      }
   }

   private <E> RegistryReadOps.ReadCache<E> readCache(ResourceKey<? extends Registry<E>> var1) {
      return (RegistryReadOps.ReadCache)this.readCache.computeIfAbsent(var1, (var0) -> {
         return new RegistryReadOps.ReadCache();
      });
   }

   protected <E> DataResult<Registry<E>> registry(ResourceKey<? extends Registry<E>> var1) {
      return (DataResult)this.registryHolder.registry(var1).map((var0) -> {
         return DataResult.success(var0, var0.elementsLifecycle());
      }).orElseGet(() -> {
         return DataResult.error("Unknown registry: " + var1);
      });
   }

   public interface ResourceAccess {
      Collection<ResourceLocation> listResources(ResourceKey<? extends Registry<?>> var1);

      <E> DataResult<Pair<E, OptionalInt>> parseElement(DynamicOps<JsonElement> var1, ResourceKey<? extends Registry<E>> var2, ResourceKey<E> var3, Decoder<E> var4);

      static RegistryReadOps.ResourceAccess forResourceManager(final ResourceManager var0) {
         return new RegistryReadOps.ResourceAccess() {
            public Collection<ResourceLocation> listResources(ResourceKey<? extends Registry<?>> var1) {
               return var0.listResources(var1.location().getPath(), (var0x) -> {
                  return var0x.endsWith(".json");
               });
            }

            public <E> DataResult<Pair<E, OptionalInt>> parseElement(DynamicOps<JsonElement> var1, ResourceKey<? extends Registry<E>> var2, ResourceKey<E> var3, Decoder<E> var4) {
               ResourceLocation var5 = var3.location();
               ResourceLocation var6 = new ResourceLocation(var5.getNamespace(), var2.location().getPath() + "/" + var5.getPath() + ".json");

               try {
                  Resource var7 = var0.getResource(var6);
                  Throwable var8 = null;

                  DataResult var13;
                  try {
                     InputStreamReader var9 = new InputStreamReader(var7.getInputStream(), StandardCharsets.UTF_8);
                     Throwable var10 = null;

                     try {
                        JsonParser var11 = new JsonParser();
                        JsonElement var12 = var11.parse(var9);
                        var13 = var4.parse(var1, var12).map((var0x) -> {
                           return Pair.of(var0x, OptionalInt.empty());
                        });
                     } catch (Throwable var38) {
                        var10 = var38;
                        throw var38;
                     } finally {
                        if (var9 != null) {
                           if (var10 != null) {
                              try {
                                 var9.close();
                              } catch (Throwable var37) {
                                 var10.addSuppressed(var37);
                              }
                           } else {
                              var9.close();
                           }
                        }

                     }
                  } catch (Throwable var40) {
                     var8 = var40;
                     throw var40;
                  } finally {
                     if (var7 != null) {
                        if (var8 != null) {
                           try {
                              var7.close();
                           } catch (Throwable var36) {
                              var8.addSuppressed(var36);
                           }
                        } else {
                           var7.close();
                        }
                     }

                  }

                  return var13;
               } catch (JsonIOException | JsonSyntaxException | IOException var42) {
                  return DataResult.error("Failed to parse " + var6 + " file: " + var42.getMessage());
               }
            }

            public String toString() {
               return "ResourceAccess[" + var0 + "]";
            }
         };
      }

      public static final class MemoryMap implements RegistryReadOps.ResourceAccess {
         private final Map<ResourceKey<?>, JsonElement> data = Maps.newIdentityHashMap();
         private final Object2IntMap<ResourceKey<?>> ids = new Object2IntOpenCustomHashMap(Util.identityStrategy());
         private final Map<ResourceKey<?>, Lifecycle> lifecycles = Maps.newIdentityHashMap();

         public MemoryMap() {
            super();
         }

         public <E> void add(RegistryAccess.RegistryHolder var1, ResourceKey<E> var2, Encoder<E> var3, int var4, E var5, Lifecycle var6) {
            DataResult var7 = var3.encodeStart(RegistryWriteOps.create(JsonOps.INSTANCE, var1), var5);
            Optional var8 = var7.error();
            if (var8.isPresent()) {
               RegistryReadOps.LOGGER.error("Error adding element: {}", ((PartialResult)var8.get()).message());
            } else {
               this.data.put(var2, var7.result().get());
               this.ids.put(var2, var4);
               this.lifecycles.put(var2, var6);
            }
         }

         public Collection<ResourceLocation> listResources(ResourceKey<? extends Registry<?>> var1) {
            return (Collection)this.data.keySet().stream().filter((var1x) -> {
               return var1x.isFor(var1);
            }).map((var1x) -> {
               return new ResourceLocation(var1x.location().getNamespace(), var1.location().getPath() + "/" + var1x.location().getPath() + ".json");
            }).collect(Collectors.toList());
         }

         public <E> DataResult<Pair<E, OptionalInt>> parseElement(DynamicOps<JsonElement> var1, ResourceKey<? extends Registry<E>> var2, ResourceKey<E> var3, Decoder<E> var4) {
            JsonElement var5 = (JsonElement)this.data.get(var3);
            return var5 == null ? DataResult.error("Unknown element: " + var3) : var4.parse(var1, var5).setLifecycle((Lifecycle)this.lifecycles.get(var3)).map((var2x) -> {
               return Pair.of(var2x, OptionalInt.of(this.ids.getInt(var3)));
            });
         }
      }
   }

   static final class ReadCache<E> {
      private final Map<ResourceKey<E>, DataResult<Supplier<E>>> values;

      private ReadCache() {
         super();
         this.values = Maps.newIdentityHashMap();
      }

      // $FF: synthetic method
      ReadCache(Object var1) {
         this();
      }
   }
}
