package net.minecraft.world.level.storage.loot.providers.number.ints;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.AggregateProvider;

public record Maximum(HolderSet<ContextIntProvider> inputs) implements ContextIntProvider, AggregateProvider<ContextIntProvider> {
   public static final MapCodec<Maximum> MAP_CODEC;

   public Maximum {
      super();
   }

   public MapCodec<Maximum> codec() {
      return MAP_CODEC;
   }

   public int getIntUnsafe(final LootContext context) {
      int value = -2147483648;

      for(Holder<ContextIntProvider> operand : this.inputs()) {
         value = Math.max(value, ((ContextIntProvider)operand.value()).getIntUnsafe(context));
      }

      return value;
   }

   static {
      MAP_CODEC = AggregateProvider.mapCodec(ContextIntProviders.LIST_CODEC, Maximum::new);
   }
}
