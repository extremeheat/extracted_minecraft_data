package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;

public interface AggregateProvider<Value extends Validatable> extends Validatable {
   static <Value extends Validatable, Self extends AggregateProvider<Value>> MapCodec<Self> mapCodec(final Codec<HolderSet<Value>> valueCodec, final Factory<Value, Self> factory) {
      return RecordCodecBuilder.mapCodec((i) -> {
         Products.P1 var10000 = i.group(valueCodec.fieldOf("inputs").forGetter(AggregateProvider::inputs));
         Objects.requireNonNull(factory);
         return var10000.apply(i, factory::create);
      });
   }

   HolderSet<Value> inputs();

   default void validate(final ValidationContext context) {
      Validatable.validateHolderSet(context, "inputs", this.inputs(), 1);
   }

   @FunctionalInterface
   public interface Factory<Value extends Validatable, Self extends AggregateProvider<Value>> {
      Self create(HolderSet<Value> value);
   }
}
