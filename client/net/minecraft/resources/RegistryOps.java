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
   private final RegistryInfoLookup lookupProvider;

   public static <T> RegistryOps<T> create(DynamicOps<T> var0, HolderLookup.Provider var1) {
      return create(var0, (RegistryInfoLookup)(new HolderLookupAdapter(var1)));
   }

   public static <T> RegistryOps<T> create(DynamicOps<T> var0, RegistryInfoLookup var1) {
      return new RegistryOps<T>(var0, var1);
   }

   public static <T> Dynamic<T> injectRegistryContext(Dynamic<T> var0, HolderLookup.Provider var1) {
      return new Dynamic(var1.createSerializationContext(var0.getOps()), var0.getValue());
   }

   private RegistryOps(DynamicOps<T> var1, RegistryInfoLookup var2) {
      super(var1);
      this.lookupProvider = var2;
   }

   public <U> RegistryOps<U> withParent(DynamicOps<U> var1) {
      return var1 == this.delegate ? this : new RegistryOps(var1, this.lookupProvider);
   }

   public <E> Optional<HolderOwner<E>> owner(ResourceKey<? extends Registry<? extends E>> var1) {
      return this.lookupProvider.lookup(var1).map(RegistryInfo::owner);
   }

   public <E> Optional<HolderGetter<E>> getter(ResourceKey<? extends Registry<? extends E>> var1) {
      return this.lookupProvider.lookup(var1).map(RegistryInfo::getter);
   }

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

   public int hashCode() {
      return this.delegate.hashCode() * 31 + this.lookupProvider.hashCode();
   }

   public static <E, O> RecordCodecBuilder<O, HolderGetter<E>> retrieveGetter(ResourceKey<? extends Registry<? extends E>> var0) {
      return ExtraCodecs.retrieveContext((var1) -> {
         if (var1 instanceof RegistryOps var2) {
            return (DataResult)var2.lookupProvider.lookup(var0).map((var0x) -> DataResult.success(var0x.getter(), var0x.elementsLifecycle())).orElseGet(() -> DataResult.error(() -> "Unknown registry: " + String.valueOf(var0)));
         } else {
            return DataResult.error(() -> "Not a registry ops");
         }
      }).forGetter((var0x) -> null);
   }

   public static <E, O> RecordCodecBuilder<O, Holder.Reference<E>> retrieveElement(ResourceKey<E> var0) {
      ResourceKey var1 = ResourceKey.createRegistryKey(var0.registry());
      return ExtraCodecs.retrieveContext((var2) -> {
         if (var2 instanceof RegistryOps var3) {
            return (DataResult)var3.lookupProvider.lookup(var1).flatMap((var1x) -> var1x.getter().get(var0)).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Can't find value: " + String.valueOf(var0)));
         } else {
            return DataResult.error(() -> "Not a registry ops");
         }
      }).forGetter((var0x) -> null);
   }

   public static record RegistryInfo<T>(HolderOwner<T> owner, HolderGetter<T> getter, Lifecycle elementsLifecycle) {
      public RegistryInfo(HolderOwner<T> var1, HolderGetter<T> var2, Lifecycle var3) {
         super();
         this.owner = var1;
         this.getter = var2;
         this.elementsLifecycle = var3;
      }

      public static <T> RegistryInfo<T> fromRegistryLookup(HolderLookup.RegistryLookup<T> var0) {
         return new RegistryInfo<T>(var0, var0, var0.registryLifecycle());
      }
   }

   static final class HolderLookupAdapter implements RegistryInfoLookup {
      private final HolderLookup.Provider lookupProvider;
      private final Map<ResourceKey<? extends Registry<?>>, Optional<? extends RegistryInfo<?>>> lookups = new ConcurrentHashMap();

      public HolderLookupAdapter(HolderLookup.Provider var1) {
         super();
         this.lookupProvider = var1;
      }

      public <E> Optional<RegistryInfo<E>> lookup(ResourceKey<? extends Registry<? extends E>> var1) {
         return (Optional)this.lookups.computeIfAbsent(var1, this::createLookup);
      }

      private Optional<RegistryInfo<Object>> createLookup(ResourceKey<? extends Registry<?>> var1) {
         return this.lookupProvider.lookup(var1).map(RegistryInfo::fromRegistryLookup);
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            boolean var10000;
            if (var1 instanceof HolderLookupAdapter) {
               HolderLookupAdapter var2 = (HolderLookupAdapter)var1;
               if (this.lookupProvider.equals(var2.lookupProvider)) {
                  var10000 = true;
                  return var10000;
               }
            }

            var10000 = false;
            return var10000;
         }
      }

      public int hashCode() {
         return this.lookupProvider.hashCode();
      }
   }

   public interface RegistryInfoLookup {
      <T> Optional<RegistryInfo<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1);
   }
}
