package net.minecraft.core.component;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public final class DataComponentPredicate implements Predicate<DataComponentMap> {
   public static final Codec<DataComponentPredicate> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, DataComponentPredicate> STREAM_CODEC;
   public static final DataComponentPredicate EMPTY;
   private final List<TypedDataComponent<?>> expectedComponents;

   DataComponentPredicate(List<TypedDataComponent<?>> var1) {
      super();
      this.expectedComponents = var1;
   }

   public static Builder builder() {
      return new Builder();
   }

   public static DataComponentPredicate allOf(DataComponentMap var0) {
      return new DataComponentPredicate(ImmutableList.copyOf(var0));
   }

   public boolean equals(Object var1) {
      boolean var10000;
      if (var1 instanceof DataComponentPredicate var2) {
         if (this.expectedComponents.equals(var2.expectedComponents)) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   public int hashCode() {
      return this.expectedComponents.hashCode();
   }

   public String toString() {
      return this.expectedComponents.toString();
   }

   public boolean test(DataComponentMap var1) {
      Iterator var2 = this.expectedComponents.iterator();

      TypedDataComponent var3;
      Object var4;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         var3 = (TypedDataComponent)var2.next();
         var4 = var1.get(var3.type());
      } while(Objects.equals(var3.value(), var4));

      return false;
   }

   public boolean test(DataComponentHolder var1) {
      return this.test(var1.getComponents());
   }

   public boolean alwaysMatches() {
      return this.expectedComponents.isEmpty();
   }

   public DataComponentPatch asPatch() {
      DataComponentPatch.Builder var1 = DataComponentPatch.builder();
      Iterator var2 = this.expectedComponents.iterator();

      while(var2.hasNext()) {
         TypedDataComponent var3 = (TypedDataComponent)var2.next();
         var1.set(var3);
      }

      return var1.build();
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((DataComponentMap)var1);
   }

   static {
      CODEC = DataComponentType.VALUE_MAP_CODEC.xmap((var0) -> {
         return new DataComponentPredicate((List)var0.entrySet().stream().map(TypedDataComponent::fromEntryUnchecked).collect(Collectors.toList()));
      }, (var0) -> {
         return (Map)var0.expectedComponents.stream().filter((var0x) -> {
            return !var0x.type().isTransient();
         }).collect(Collectors.toMap(TypedDataComponent::type, TypedDataComponent::value));
      });
      STREAM_CODEC = TypedDataComponent.STREAM_CODEC.apply(ByteBufCodecs.list()).map(DataComponentPredicate::new, (var0) -> {
         return var0.expectedComponents;
      });
      EMPTY = new DataComponentPredicate(List.of());
   }

   public static class Builder {
      private final List<TypedDataComponent<?>> expectedComponents = new ArrayList();

      Builder() {
         super();
      }

      public <T> Builder expect(DataComponentType<? super T> var1, T var2) {
         Iterator var3 = this.expectedComponents.iterator();

         TypedDataComponent var4;
         do {
            if (!var3.hasNext()) {
               this.expectedComponents.add(new TypedDataComponent(var1, var2));
               return this;
            }

            var4 = (TypedDataComponent)var3.next();
         } while(var4.type() != var1);

         throw new IllegalArgumentException("Predicate already has component of type: '" + String.valueOf(var1) + "'");
      }

      public DataComponentPredicate build() {
         return new DataComponentPredicate(List.copyOf(this.expectedComponents));
      }
   }
}
