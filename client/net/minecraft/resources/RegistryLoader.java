package net.minecraft.resources;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;

public class RegistryLoader {
   private final RegistryResourceAccess resources;
   private final Map<ResourceKey<? extends Registry<?>>, RegistryLoader.ReadCache<?>> readCache = new IdentityHashMap<>();

   RegistryLoader(RegistryResourceAccess var1) {
      super();
      this.resources = var1;
   }

   public <E> DataResult<? extends Registry<E>> overrideRegistryFromResources(
      WritableRegistry<E> var1, ResourceKey<? extends Registry<E>> var2, Codec<E> var3, DynamicOps<JsonElement> var4
   ) {
      Map var5 = this.resources.listResources(var2);
      DataResult var6 = DataResult.success(var1, Lifecycle.stable());

      for(Entry var8 : var5.entrySet()) {
         var6 = var6.flatMap(
            var5x -> this.overrideElementFromResources(
                     var5x, var2, var3, (ResourceKey<E>)var8.getKey(), Optional.of((RegistryResourceAccess.EntryThunk<E>)var8.getValue()), var4
                  )
                  .map(var1xx -> var5x)
         );
      }

      return var6.setPartial(var1);
   }

   <E> DataResult<Holder<E>> overrideElementFromResources(
      WritableRegistry<E> var1, ResourceKey<? extends Registry<E>> var2, Codec<E> var3, ResourceKey<E> var4, DynamicOps<JsonElement> var5
   ) {
      Optional var6 = this.resources.getResource(var4);
      return this.overrideElementFromResources(var1, var2, var3, var4, var6, var5);
   }

   private <E> DataResult<Holder<E>> overrideElementFromResources(
      WritableRegistry<E> var1,
      ResourceKey<? extends Registry<E>> var2,
      Codec<E> var3,
      ResourceKey<E> var4,
      Optional<RegistryResourceAccess.EntryThunk<E>> var5,
      DynamicOps<JsonElement> var6
   ) {
      RegistryLoader.ReadCache var7 = this.readCache(var2);
      DataResult var8 = (DataResult)var7.values.get(var4);
      if (var8 != null) {
         return var8;
      } else {
         Holder var9 = var1.getOrCreateHolderOrThrow(var4);
         var7.values.put(var4, DataResult.success(var9));
         DataResult var10;
         if (var5.isEmpty()) {
            if (var1.containsKey(var4)) {
               var10 = DataResult.success(var9, Lifecycle.stable());
            } else {
               var10 = DataResult.error("Missing referenced custom/removed registry entry for registry " + var2 + " named " + var4.location());
            }
         } else {
            DataResult var11 = ((RegistryResourceAccess.EntryThunk)var5.get()).parseElement(var6, var3);
            Optional var12 = var11.result();
            if (var12.isPresent()) {
               RegistryResourceAccess.ParsedEntry var13 = (RegistryResourceAccess.ParsedEntry)var12.get();
               var1.registerOrOverride(var13.fixedId(), var4, var13.value(), var11.lifecycle());
            }

            var10 = var11.map(var1x -> var9);
         }

         var7.values.put(var4, var10);
         return var10;
      }
   }

   private <E> RegistryLoader.ReadCache<E> readCache(ResourceKey<? extends Registry<E>> var1) {
      return (RegistryLoader.ReadCache<E>)this.readCache.computeIfAbsent(var1, var0 -> new RegistryLoader.ReadCache());
   }

   public RegistryLoader.Bound bind(RegistryAccess.Writable var1) {
      return new RegistryLoader.Bound(var1, this);
   }

   public static record Bound(RegistryAccess.Writable a, RegistryLoader b) {
      private final RegistryAccess.Writable access;
      private final RegistryLoader loader;

      public Bound(RegistryAccess.Writable var1, RegistryLoader var2) {
         super();
         this.access = var1;
         this.loader = var2;
      }

      public <E> DataResult<? extends Registry<E>> overrideRegistryFromResources(
         ResourceKey<? extends Registry<E>> var1, Codec<E> var2, DynamicOps<JsonElement> var3
      ) {
         WritableRegistry var4 = this.access.ownedWritableRegistryOrThrow(var1);
         return this.loader.overrideRegistryFromResources(var4, var1, var2, var3);
      }

      public <E> DataResult<Holder<E>> overrideElementFromResources(
         ResourceKey<? extends Registry<E>> var1, Codec<E> var2, ResourceKey<E> var3, DynamicOps<JsonElement> var4
      ) {
         WritableRegistry var5 = this.access.ownedWritableRegistryOrThrow(var1);
         return this.loader.overrideElementFromResources(var5, var1, var2, var3, var4);
      }
   }

   static final class ReadCache<E> {
      final Map<ResourceKey<E>, DataResult<Holder<E>>> values = Maps.newIdentityHashMap();

      ReadCache() {
         super();
      }
   }
}
