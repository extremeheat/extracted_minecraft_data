package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;

public interface PowerProvider<Value extends Validatable> extends Validatable {
   static <Value extends Validatable, Self extends PowerProvider<Value>> MapCodec<Self> mapCodec(final Codec<Holder<Value>> valueCodec, final Factory<Value, Self> factory) {
      return RecordCodecBuilder.mapCodec((i) -> {
         Products.P2 var10000 = i.group(valueCodec.fieldOf("base").forGetter(PowerProvider::base), valueCodec.fieldOf("exponent").forGetter(PowerProvider::exponent));
         Objects.requireNonNull(factory);
         return var10000.apply(i, factory::create);
      });
   }

   default void validate(final ValidationContext context) {
      Validatable.validateHolder(context, "base", this.base());
      Validatable.validateHolder(context, "exponent", this.exponent());
   }

   Holder<Value> base();

   Holder<Value> exponent();

   @FunctionalInterface
   public interface Factory<Value extends Validatable, Self extends PowerProvider<Value>> {
      Self create(Holder<Value> left, Holder<Value> right);
   }
}
