package net.minecraft.core.component;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public final class DataComponentExactPredicate implements Predicate<DataComponentGetter> {
   public static final Codec<DataComponentExactPredicate> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, DataComponentExactPredicate> STREAM_CODEC;
   public static final DataComponentExactPredicate EMPTY;
   private final List<TypedDataComponent<?>> expectedComponents;

   private DataComponentExactPredicate(final List<TypedDataComponent<?>> expectedComponents) {
      super();
      this.expectedComponents = expectedComponents;
   }

   public static Builder builder() {
      return new Builder();
   }

   public static <T> DataComponentExactPredicate expect(final DataComponentType<T> type, final T value) {
      return new DataComponentExactPredicate(List.of(new TypedDataComponent(type, value)));
   }

   public static DataComponentExactPredicate allOf(final DataComponentMap components) {
      return new DataComponentExactPredicate(ImmutableList.copyOf(components));
   }

   public static DataComponentExactPredicate someOf(final DataComponentMap components, final DataComponentType<?>... types) {
      Builder result = new Builder();

      for(DataComponentType<?> type : types) {
         TypedDataComponent<?> value = components.getTyped(type);
         if (value != null) {
            result.expect(value);
         }
      }

      return result.build();
   }

   public boolean isEmpty() {
      return this.expectedComponents.isEmpty();
   }

   public boolean equals(final Object obj) {
      boolean var10000;
      if (obj instanceof DataComponentExactPredicate predicate) {
         if (this.expectedComponents.equals(predicate.expectedComponents)) {
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

   public boolean test(final DataComponentGetter actualComponents) {
      for(TypedDataComponent<?> expected : this.expectedComponents) {
         Object actual = actualComponents.get(expected.type());
         if (!Objects.equals(expected.value(), actual)) {
            return false;
         }
      }

      return true;
   }

   public boolean alwaysMatches() {
      return this.expectedComponents.isEmpty();
   }

   public DataComponentPatch asPatch() {
      return DataComponentPatch.builder().set(this.expectedComponents).build();
   }

   static {
      CODEC = DataComponentType.VALUE_MAP_CODEC.xmap((map) -> new DataComponentExactPredicate((List)map.entrySet().stream().map(TypedDataComponent::fromEntryUnchecked).collect(Collectors.toList())), (predicate) -> (Map)predicate.expectedComponents.stream().filter((e) -> !e.type().isTransient()).collect(Collectors.toMap(TypedDataComponent::type, TypedDataComponent::value)));
      STREAM_CODEC = TypedDataComponent.STREAM_CODEC.apply(ByteBufCodecs.list()).map(DataComponentExactPredicate::new, (predicate) -> predicate.expectedComponents);
      EMPTY = new DataComponentExactPredicate(List.of());
   }

   public static class Builder {
      private final List<TypedDataComponent<?>> expectedComponents = new ArrayList();

      private Builder() {
         super();
      }

      public <T> Builder expect(final TypedDataComponent<T> value) {
         return this.expect(value.type(), value.value());
      }

      public <T> Builder expect(final DataComponentType<? super T> type, final T value) {
         for(TypedDataComponent<?> component : this.expectedComponents) {
            if (component.type() == type) {
               throw new IllegalArgumentException("Predicate already has component of type: '" + String.valueOf(type) + "'");
            }
         }

         this.expectedComponents.add(new TypedDataComponent(type, value));
         return this;
      }

      public DataComponentExactPredicate build() {
         return new DataComponentExactPredicate(List.copyOf(this.expectedComponents));
      }
   }
}
