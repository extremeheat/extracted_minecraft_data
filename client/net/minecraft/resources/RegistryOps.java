package net.minecraft.resources;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.util.ExtraCodecs;

public class RegistryOps<T> extends DelegatingOps<T> {
   private final RegistryOps.RegistryInfoLookup lookupProvider;

   public static <T> RegistryOps<T> create(DynamicOps<T> var0, HolderLookup.Provider var1) {
      return create(var0, new RegistryOps.HolderLookupAdapter(var1));
   }

   public static <T> RegistryOps<T> create(DynamicOps<T> var0, RegistryOps.RegistryInfoLookup var1) {
      return new RegistryOps<>(var0, var1);
   }

   public static <T> Dynamic<T> injectRegistryContext(Dynamic<T> var0, HolderLookup.Provider var1) {
      return new Dynamic(var1.createSerializationContext(var0.getOps()), var0.getValue());
   }

   private RegistryOps(DynamicOps<T> var1, RegistryOps.RegistryInfoLookup var2) {
      super(var1);
      this.lookupProvider = var2;
   }

   public <U> RegistryOps<U> withParent(DynamicOps<U> var1) {
      return (RegistryOps<U>)(var1 == this.delegate ? this : new RegistryOps(var1, this.lookupProvider));
   }

   public <E> Optional<HolderOwner<E>> owner(ResourceKey<? extends Registry<? extends E>> var1) {
      return this.lookupProvider.lookup(var1).map(RegistryOps.RegistryInfo::owner);
   }

   public <E> Optional<HolderGetter<E>> getter(ResourceKey<? extends Registry<? extends E>> var1) {
      return this.lookupProvider.lookup(var1).map(RegistryOps.RegistryInfo::getter);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         RegistryOps var2 = (RegistryOps)var1;
         return this.delegate.equals(var2.delegate) && this.lookupProvider.equals(var2.lookupProvider);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.delegate.hashCode() * 31 + this.lookupProvider.hashCode();
   }

   public static <E, O> RecordCodecBuilder<O, HolderGetter<E>> retrieveGetter(ResourceKey<? extends Registry<? extends E>> var0) {
      return ExtraCodecs.retrieveContext(
            var1 -> var1 instanceof RegistryOps var2
                  ? var2.lookupProvider
                     .lookup(var0)
                     .map(var0xx -> DataResult.success(var0xx.getter(), var0xx.elementsLifecycle()))
                     .orElseGet(() -> DataResult.error(() -> "Unknown registry: " + var0))
                  : DataResult.error(() -> "Not a registry ops")
         )
         .forGetter(var0x -> null);
   }

   public static <E, O> RecordCodecBuilder<O, Holder.Reference<E>> retrieveElement(ResourceKey<E> var0) {
      ResourceKey var1 = ResourceKey.createRegistryKey(var0.registry());
      return ExtraCodecs.retrieveContext(
            var2 -> var2 instanceof RegistryOps var3
                  ? var3.lookupProvider
                     .lookup(var1)
                     .flatMap(var1xx -> var1xx.getter().get(var0))
                     .<DataResult<E>>map(DataResult::success)
                     .orElseGet(() -> DataResult.error(() -> "Can't find value: " + var0))
                  : DataResult.error(() -> "Not a registry ops")
         )
         .forGetter(var0x -> null);
   }

   static final class HolderLookupAdapter implements RegistryOps.RegistryInfoLookup {
      private final HolderLookup.Provider lookupProvider;
      private final Map<ResourceKey<? extends Registry<?>>, Optional<? extends RegistryOps.RegistryInfo<?>>> lookups = new ConcurrentHashMap<>();

      public HolderLookupAdapter(HolderLookup.Provider var1) {
         super();
         this.lookupProvider = var1;
      }

      @Override
      public <E> Optional<RegistryOps.RegistryInfo<E>> lookup(ResourceKey<? extends Registry<? extends E>> var1) {
         return (Optional<RegistryOps.RegistryInfo<E>>)this.lookups.computeIfAbsent(var1, this::createLookup);
      }

      private Optional<RegistryOps.RegistryInfo<Object>> createLookup(ResourceKey<? extends Registry<?>> var1) {
         return this.lookupProvider.lookup((ResourceKey<? extends Registry<?>>)var1).map(RegistryOps.RegistryInfo::fromRegistryLookup);
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            if (var1 instanceof RegistryOps.HolderLookupAdapter var2 && this.lookupProvider.equals(var2.lookupProvider)) {
               return true;
            }

            return false;
         }
      }

      @Override
      public int hashCode() {
         return this.lookupProvider.hashCode();
      }
   }

   public static record RegistryInfo<T>(HolderOwner<T> owner, HolderGetter<T> getter, Lifecycle elementsLifecycle) {
      public RegistryInfo(HolderOwner<T> owner, HolderGetter<T> getter, Lifecycle elementsLifecycle) {
         super();
         this.owner = owner;
         this.getter = getter;
         this.elementsLifecycle = elementsLifecycle;
      }

      public static <T> RegistryOps.RegistryInfo<T> fromRegistryLookup(HolderLookup.RegistryLookup<T> var0) {
         return new RegistryOps.RegistryInfo<>(var0, var0, var0.registryLifecycle());
      }
   }

   public interface RegistryInfoLookup {
      <T> Optional<RegistryOps.RegistryInfo<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1);
   }
}
