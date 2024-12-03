package net.minecraft.util.random;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;

public class SimpleWeightedRandomList<E> extends WeightedRandomList<WeightedEntry.Wrapper<E>> {
   public static <E> Codec<SimpleWeightedRandomList<E>> wrappedCodecAllowingEmpty(Codec<E> var0) {
      return WeightedEntry.Wrapper.codec(var0).listOf().xmap(SimpleWeightedRandomList::new, WeightedRandomList::unwrap);
   }

   public static <E> Codec<SimpleWeightedRandomList<E>> wrappedCodec(Codec<E> var0) {
      return ExtraCodecs.nonEmptyList(WeightedEntry.Wrapper.codec(var0).listOf()).xmap(SimpleWeightedRandomList::new, WeightedRandomList::unwrap);
   }

   SimpleWeightedRandomList(List<? extends WeightedEntry.Wrapper<E>> var1) {
      super(var1);
   }

   public static <E> Builder<E> builder() {
      return new Builder<E>();
   }

   public static <E> SimpleWeightedRandomList<E> empty() {
      return new SimpleWeightedRandomList<E>(List.of());
   }

   public static <E> SimpleWeightedRandomList<E> single(E var0) {
      return new SimpleWeightedRandomList<E>(List.of(WeightedEntry.wrap(var0, 1)));
   }

   public Optional<E> getRandomValue(RandomSource var1) {
      return this.getRandom(var1).map(WeightedEntry.Wrapper::data);
   }

   public static class Builder<E> {
      private final ImmutableList.Builder<WeightedEntry.Wrapper<E>> result = ImmutableList.builder();

      public Builder() {
         super();
      }

      public Builder<E> add(E var1) {
         return this.add(var1, 1);
      }

      public Builder<E> add(E var1, int var2) {
         this.result.add(WeightedEntry.wrap(var1, var2));
         return this;
      }

      public SimpleWeightedRandomList<E> build() {
         return new SimpleWeightedRandomList<E>(this.result.build());
      }
   }
}
