package net.minecraft.resources;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.server.packs.resources.ResourceManager;

public class RegistryReadOps<T> extends DelegatingOps<T> {
   private final RegistryResourceAccess resources;
   private final RegistryAccess registryAccess;
   private final Map<ResourceKey<? extends Registry<?>>, RegistryReadOps.ReadCache<?>> readCache;
   private final RegistryReadOps<JsonElement> jsonOps;

   public static <T> RegistryReadOps<T> createAndLoad(DynamicOps<T> var0, ResourceManager var1, RegistryAccess var2) {
      return createAndLoad(var0, RegistryResourceAccess.forResourceManager(var1), var2);
   }

   public static <T> RegistryReadOps<T> createAndLoad(DynamicOps<T> var0, RegistryResourceAccess var1, RegistryAccess var2) {
      RegistryReadOps var3 = new RegistryReadOps(var0, var1, var2, Maps.newIdentityHashMap());
      RegistryAccess.load(var2, var3);
      return var3;
   }

   public static <T> RegistryReadOps<T> create(DynamicOps<T> var0, ResourceManager var1, RegistryAccess var2) {
      return create(var0, RegistryResourceAccess.forResourceManager(var1), var2);
   }

   public static <T> RegistryReadOps<T> create(DynamicOps<T> var0, RegistryResourceAccess var1, RegistryAccess var2) {
      return new RegistryReadOps(var0, var1, var2, Maps.newIdentityHashMap());
   }

   private RegistryReadOps(DynamicOps<T> var1, RegistryResourceAccess var2, RegistryAccess var3, IdentityHashMap<ResourceKey<? extends Registry<?>>, RegistryReadOps.ReadCache<?>> var4) {
      super(var1);
      this.resources = var2;
      this.registryAccess = var3;
      this.readCache = var4;
      this.jsonOps = var1 == JsonOps.INSTANCE ? this : new RegistryReadOps(JsonOps.INSTANCE, var2, var3, var4);
   }

   protected <E> DataResult<Pair<Supplier<E>, T>> decodeElement(T var1, ResourceKey<? extends Registry<E>> var2, Codec<E> var3, boolean var4) {
      Optional var5 = this.registryAccess.ownedRegistry(var2);
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
            ResourceKey var9 = ResourceKey.create(var2, (ResourceLocation)var8.getFirst());
            return this.readAndRegisterElement(var2, var6, var3, var9).map((var1x) -> {
               return Pair.of(var1x, var8.getSecond());
            });
         }
      }
   }

   public <E> DataResult<MappedRegistry<E>> decodeElements(MappedRegistry<E> var1, ResourceKey<? extends Registry<E>> var2, Codec<E> var3) {
      Collection var4 = this.resources.listResources(var2);
      DataResult var5 = DataResult.success(var1, Lifecycle.stable());

      ResourceKey var7;
      for(Iterator var6 = var4.iterator(); var6.hasNext(); var5 = var5.flatMap((var4x) -> {
         return this.readAndRegisterElement(var2, var4x, var3, var7).map((var1) -> {
            return var4x;
         });
      })) {
         var7 = (ResourceKey)var6.next();
      }

      return var5.setPartial(var1);
   }

   private <E> DataResult<Supplier<E>> readAndRegisterElement(ResourceKey<? extends Registry<E>> var1, WritableRegistry<E> var2, Codec<E> var3, ResourceKey<E> var4) {
      RegistryReadOps.ReadCache var5 = this.readCache(var1);
      DataResult var6 = (DataResult)var5.values.get(var4);
      if (var6 != null) {
         return var6;
      } else {
         var5.values.put(var4, DataResult.success(createPlaceholderGetter(var2, var4)));
         Optional var7 = this.resources.parseElement(this.jsonOps, var1, var4, var3);
         DataResult var8;
         if (var7.isEmpty()) {
            if (var2.containsKey(var4)) {
               var8 = DataResult.success(createRegistryGetter(var2, var4), Lifecycle.stable());
            } else {
               var8 = DataResult.error("Missing referenced custom/removed registry entry for registry " + var1 + " named " + var4.location());
            }
         } else {
            DataResult var9 = (DataResult)var7.get();
            Optional var10 = var9.result();
            if (var10.isPresent()) {
               RegistryResourceAccess.ParsedEntry var11 = (RegistryResourceAccess.ParsedEntry)var10.get();
               var2.registerOrOverride(var11.fixedId(), var4, var11.value(), var9.lifecycle());
            }

            var8 = var9.map((var2x) -> {
               return createRegistryGetter(var2, var4);
            });
         }

         var5.values.put(var4, var8);
         return var8;
      }
   }

   private static <E> Supplier<E> createPlaceholderGetter(WritableRegistry<E> var0, ResourceKey<E> var1) {
      return Suppliers.memoize(() -> {
         Object var2 = var0.get(var1);
         if (var2 == null) {
            throw new RuntimeException("Error during recursive registry parsing, element resolved too early: " + var1);
         } else {
            return var2;
         }
      });
   }

   private static <E> Supplier<E> createRegistryGetter(final Registry<E> var0, final ResourceKey<E> var1) {
      return new Supplier<E>() {
         public E get() {
            return var0.get(var1);
         }

         public String toString() {
            return var1.toString();
         }
      };
   }

   private <E> RegistryReadOps.ReadCache<E> readCache(ResourceKey<? extends Registry<E>> var1) {
      return (RegistryReadOps.ReadCache)this.readCache.computeIfAbsent(var1, (var0) -> {
         return new RegistryReadOps.ReadCache();
      });
   }

   protected <E> DataResult<Registry<E>> registry(ResourceKey<? extends Registry<E>> var1) {
      return (DataResult)this.registryAccess.ownedRegistry(var1).map((var0) -> {
         return DataResult.success(var0, var0.elementsLifecycle());
      }).orElseGet(() -> {
         return DataResult.error("Unknown registry: " + var1);
      });
   }

   static final class ReadCache<E> {
      final Map<ResourceKey<E>, DataResult<Supplier<E>>> values = Maps.newIdentityHashMap();

      ReadCache() {
         super();
      }
   }
}
