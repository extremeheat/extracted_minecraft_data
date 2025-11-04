package net.minecraft.advancements.criterion;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DataComponentMatchers(DataComponentExactPredicate exact, Map<DataComponentPredicate.Type<?>, DataComponentPredicate> partial) implements Predicate<DataComponentGetter> {
   public static final DataComponentMatchers ANY;
   public static final MapCodec<DataComponentMatchers> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, DataComponentMatchers> STREAM_CODEC;

   public DataComponentMatchers(DataComponentExactPredicate var1, Map<DataComponentPredicate.Type<?>, DataComponentPredicate> var2) {
      super();
      this.exact = var1;
      this.partial = var2;
   }

   public boolean test(DataComponentGetter var1) {
      if (!this.exact.test(var1)) {
         return false;
      } else {
         for(DataComponentPredicate var3 : this.partial.values()) {
            if (!var3.matches(var1)) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean isEmpty() {
      return this.exact.isEmpty() && this.partial.isEmpty();
   }

   // $FF: synthetic method
   public boolean test(final Object var1) {
      return this.test((DataComponentGetter)var1);
   }

   static {
      ANY = new DataComponentMatchers(DataComponentExactPredicate.EMPTY, Map.of());
      CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(DataComponentExactPredicate.CODEC.optionalFieldOf("components", DataComponentExactPredicate.EMPTY).forGetter(DataComponentMatchers::exact), DataComponentPredicate.CODEC.optionalFieldOf("predicates", Map.of()).forGetter(DataComponentMatchers::partial)).apply(var0, DataComponentMatchers::new));
      STREAM_CODEC = StreamCodec.composite(DataComponentExactPredicate.STREAM_CODEC, DataComponentMatchers::exact, DataComponentPredicate.STREAM_CODEC, DataComponentMatchers::partial, DataComponentMatchers::new);
   }

   public static class Builder {
      private DataComponentExactPredicate exact;
      private final ImmutableMap.Builder<DataComponentPredicate.Type<?>, DataComponentPredicate> partial;

      private Builder() {
         super();
         this.exact = DataComponentExactPredicate.EMPTY;
         this.partial = ImmutableMap.builder();
      }

      public static Builder components() {
         return new Builder();
      }

      public <T extends DataComponentType<?>> Builder any(DataComponentType<?> var1) {
         DataComponentPredicate.AnyValueType var2 = DataComponentPredicate.AnyValueType.create(var1);
         this.partial.put(var2, var2.predicate());
         return this;
      }

      public <T extends DataComponentPredicate> Builder partial(DataComponentPredicate.Type<T> var1, T var2) {
         this.partial.put(var1, var2);
         return this;
      }

      public Builder exact(DataComponentExactPredicate var1) {
         this.exact = var1;
         return this;
      }

      public DataComponentMatchers build() {
         return new DataComponentMatchers(this.exact, this.partial.buildOrThrow());
      }
   }
}
