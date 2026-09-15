package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.core.Holder;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;

public interface DistributionProvider<Value extends Validatable> extends Validatable {
   WeightedList<Holder<Value>> distribution();

   static <Value extends Validatable, Self extends DistributionProvider<Value>> MapCodec<Self> mapCodec(final Codec<Holder<Value>> valueCodec, final Factory<Value, Self> factory) {
      return RecordCodecBuilder.mapCodec((i) -> {
         Products.P1 var10000 = i.group(WeightedList.nonEmptyCodec(valueCodec).fieldOf("distribution").forGetter(DistributionProvider::distribution));
         Objects.requireNonNull(factory);
         return var10000.apply(i, factory::create);
      });
   }

   default void validate(final ValidationContext context) {
      Validatable.validateHolder(context, "distribution", this.distribution().unwrap().stream().map(Weighted::value).toList());
   }

   @FunctionalInterface
   public interface Factory<Value extends Validatable, Self extends DistributionProvider<Value>> {
      Self create(WeightedList<Holder<Value>> distribution);
   }
}
