package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;

public interface UnaryProvider<Value extends Validatable> extends Validatable {
   static <Value extends Validatable, Self extends UnaryProvider<Value>> MapCodec<Self> codec(final Codec<Holder<Value>> valueCodec, final Factory<Value, Self> factory) {
      return RecordCodecBuilder.mapCodec((i) -> {
         Products.P1 var10000 = i.group(valueCodec.fieldOf("input").forGetter(UnaryProvider::input));
         Objects.requireNonNull(factory);
         return var10000.apply(i, factory::create);
      });
   }

   Holder<Value> input();

   default void validate(final ValidationContext context) {
      Validatable.validateHolder(context, "input", this.input());
   }

   @FunctionalInterface
   public interface Factory<Value extends Validatable, Self extends UnaryProvider<Value>> {
      Self create(Holder<Value> input);
   }
}
