package net.minecraft.world.level.storage.loot.providers.number.ints;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.DistributionProvider;

public record WeightedListValue(WeightedList<Holder<ContextIntProvider>> distribution) implements ContextIntProvider, DistributionProvider<ContextIntProvider> {
   public static final MapCodec<WeightedListValue> MAP_CODEC;

   public WeightedListValue {
      super();
   }

   public int getIntUnsafe(final LootContext context) {
      return ((ContextIntProvider)((Holder)this.distribution().getRandomOrThrow(context.getRandom())).value()).getIntUnsafe(context);
   }

   public MapCodec<WeightedListValue> codec() {
      return MAP_CODEC;
   }

   static {
      MAP_CODEC = DistributionProvider.mapCodec(ContextIntProviders.CODEC, WeightedListValue::new);
   }
}
