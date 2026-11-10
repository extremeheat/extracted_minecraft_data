package net.minecraft.resources;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.util.ExtraCodecs;

public class RegistryOps<T> extends DelegatingOps<T> {
   private final HolderGetter.Provider getterProvider;

   public static <T> RegistryOps<T> create(final DynamicOps<T> parent, final HolderGetter.Provider getterProvider) {
      return new RegistryOps<T>(parent, getterProvider);
   }

   public static <T> Dynamic<T> injectRegistryContext(final Dynamic<T> dynamic, final HolderGetter.Provider lookupProvider) {
      return new Dynamic(lookupProvider.createSerializationContext(dynamic.getOps()), dynamic.getValue());
   }

   private RegistryOps(final DynamicOps<T> parent, final HolderGetter.Provider getterProvider) {
      super(parent);
      this.getterProvider = getterProvider;
   }

   public <U> RegistryOps<U> withParent(final DynamicOps<U> parent) {
      return parent == this.delegate ? this : new RegistryOps(parent, this.getterProvider);
   }

   public <E> Optional<? extends HolderGetter<E>> getter(final ResourceKey<? extends Registry<? extends E>> registryKey) {
      return this.getterProvider.lookup(registryKey);
   }

   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         RegistryOps<?> ops = (RegistryOps)obj;
         return this.delegate.equals(ops.delegate) && this.getterProvider.equals(ops.getterProvider);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.delegate.hashCode() * 31 + this.getterProvider.hashCode();
   }

   public static <E, O> RecordCodecBuilder<O, E> retrieveRegistryContext(final Function<RegistryOps<?>, DataResult<E>> getter) {
      return ExtraCodecs.retrieveContext((ops) -> {
         if (ops instanceof RegistryOps<?> registryOps) {
            return (DataResult)getter.apply(registryOps);
         } else {
            return DataResult.error(() -> "Not a registry ops");
         }
      }).forGetter((var0) -> null);
   }

   public static <O> RecordCodecBuilder<O, HolderGetter.Provider> retrieveGetterProvider() {
      return retrieveRegistryContext((registryOps) -> DataResult.success(registryOps.getterProvider, Lifecycle.stable()));
   }

   public static <E, O> RecordCodecBuilder<O, HolderGetter<E>> retrieveGetter(final ResourceKey<? extends Registry<? extends E>> registryKey) {
      return retrieveRegistryContext((registryOps) -> (DataResult)registryOps.getterProvider.lookup(registryKey).map((r) -> DataResult.success(r, Lifecycle.stable())).orElseGet(() -> DataResult.error(() -> "Unknown registry: " + String.valueOf(registryKey))));
   }

   public static <E, O> RecordCodecBuilder<O, Holder.Reference<E>> retrieveElement(final ResourceKey<E> key) {
      ResourceKey<? extends Registry<E>> registryKey = ResourceKey.createRegistryKey(key.registry());
      return retrieveRegistryContext((registryOps) -> (DataResult)registryOps.getterProvider.lookup(registryKey).flatMap((r) -> r.get(key)).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Can't find value: " + String.valueOf(key))));
   }
}
