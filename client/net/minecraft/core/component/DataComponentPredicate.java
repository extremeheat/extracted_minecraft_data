package net.minecraft.core.component;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public final class DataComponentPredicate implements Predicate<DataComponentMap> {
   public static final Codec<DataComponentPredicate> CODEC = DataComponentType.VALUE_MAP_CODEC
      .xmap(
         var0 -> new DataComponentPredicate(var0.entrySet().stream().map(TypedDataComponent::fromEntryUnchecked).collect(Collectors.toList())),
         var0 -> var0.expectedComponents
               .stream()
               .filter(var0x -> !var0x.type().isTransient())
               .collect(Collectors.toMap(TypedDataComponent::type, TypedDataComponent::value))
      );
   public static final StreamCodec<RegistryFriendlyByteBuf, DataComponentPredicate> STREAM_CODEC = TypedDataComponent.STREAM_CODEC
      .apply(ByteBufCodecs.list())
      .map(DataComponentPredicate::new, var0 -> var0.expectedComponents);
   public static final DataComponentPredicate EMPTY = new DataComponentPredicate(List.of());
   private final List<TypedDataComponent<?>> expectedComponents;

   DataComponentPredicate(List<TypedDataComponent<?>> var1) {
      super();
      this.expectedComponents = (List<TypedDataComponent<?>>)var1;
   }

   public static DataComponentPredicate.Builder builder() {
      return new DataComponentPredicate.Builder();
   }

   public static DataComponentPredicate allOf(DataComponentMap var0) {
      return new DataComponentPredicate(ImmutableList.copyOf(var0));
   }

   public static DataComponentPredicate someOf(DataComponentMap var0, DataComponentType<?>... var1) {
      DataComponentPredicate.Builder var2 = new DataComponentPredicate.Builder();

      for (DataComponentType var6 : var1) {
         TypedDataComponent var7 = var0.getTyped(var6);
         if (var7 != null) {
            var2.expect(var7);
         }
      }

      return var2.build();
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 instanceof DataComponentPredicate var2 && this.expectedComponents.equals(var2.expectedComponents)) {
         return true;
      }

      return false;
   }

   @Override
   public int hashCode() {
      return this.expectedComponents.hashCode();
   }

   @Override
   public String toString() {
      return this.expectedComponents.toString();
   }

   public boolean test(DataComponentMap var1) {
      for (TypedDataComponent var3 : this.expectedComponents) {
         Object var4 = var1.get(var3.type());
         if (!Objects.equals(var3.value(), var4)) {
            return false;
         }
      }

      return true;
   }

   public boolean test(DataComponentHolder var1) {
      return this.test(var1.getComponents());
   }

   public boolean alwaysMatches() {
      return this.expectedComponents.isEmpty();
   }

   public DataComponentPatch asPatch() {
      DataComponentPatch.Builder var1 = DataComponentPatch.builder();

      for (TypedDataComponent var3 : this.expectedComponents) {
         var1.set(var3);
      }

      return var1.build();
   }

   public static class Builder {
      private final List<TypedDataComponent<?>> expectedComponents = new ArrayList<>();

      Builder() {
         super();
      }

      public <T> DataComponentPredicate.Builder expect(TypedDataComponent<T> var1) {
         return this.expect(var1.type(), var1.value());
      }

      public <T> DataComponentPredicate.Builder expect(DataComponentType<? super T> var1, T var2) {
         for (TypedDataComponent var4 : this.expectedComponents) {
            if (var4.type() == var1) {
               throw new IllegalArgumentException("Predicate already has component of type: '" + var1 + "'");
            }
         }

         this.expectedComponents.add(new TypedDataComponent<>(var1, var2));
         return this;
      }

      public DataComponentPredicate build() {
         return new DataComponentPredicate(List.copyOf(this.expectedComponents));
      }
   }
}
