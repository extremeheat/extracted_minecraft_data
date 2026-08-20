package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;

public interface Aggregate extends NumberProvider {
   static <A extends Aggregate> MapCodec<A> codec(final Function<HolderSet<NumberProvider>, A> factory) {
      return RecordCodecBuilder.mapCodec((i) -> i.group(NumberProviders.LIST_CODEC.fieldOf("operands").forGetter(Aggregate::operands)).apply(i, factory));
   }

   int getInt(LootContext context);

   HolderSet<NumberProvider> operands();

   default void validate(final ValidationContext context) {
      NumberProvider.super.validate(context);
      Validatable.validateHolderSet(context, "operands", this.operands(), 1);
   }

   MapCodec<? extends Aggregate> codec();
}
