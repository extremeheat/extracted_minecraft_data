package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;

public interface BinaryProvider<Value extends Validatable> extends Validatable {
   static <Value extends Validatable, Self extends BinaryProvider<Value>> MapCodec<Self> mapCodec(final Codec<Holder<Value>> valueCodec, final Factory<Value, Self> factory) {
      return RecordCodecBuilder.mapCodec((i) -> {
         Products.P2 var10000 = i.group(valueCodec.fieldOf("left").forGetter(BinaryProvider::left), valueCodec.fieldOf("right").forGetter(BinaryProvider::right));
         Objects.requireNonNull(factory);
         return var10000.apply(i, factory::create);
      });
   }

   default void validate(final ValidationContext context) {
      Validatable.validateHolder(context, "left", this.left());
      Validatable.validateHolder(context, "right", this.right());
   }

   Holder<Value> left();

   Holder<Value> right();

   @FunctionalInterface
   public interface Factory<Value extends Validatable, Self extends BinaryProvider<Value>> {
      Self create(Holder<Value> left, Holder<Value> right);
   }
}
