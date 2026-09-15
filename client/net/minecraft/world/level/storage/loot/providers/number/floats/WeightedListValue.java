package net.minecraft.world.level.storage.loot.providers.number.floats;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.DistributionProvider;

public record WeightedListValue(WeightedList<Holder<ContextFloatProvider>> distribution) implements ContextFloatProvider, DistributionProvider<ContextFloatProvider> {
   public static final MapCodec<WeightedListValue> MAP_CODEC;

   public WeightedListValue {
      super();
   }

   public float getFloatUnsafe(final LootContext context) {
      return ((ContextFloatProvider)((Holder)this.distribution().getRandomOrThrow(context.getRandom())).value()).getFloatUnsafe(context);
   }

   public MapCodec<WeightedListValue> codec() {
      return MAP_CODEC;
   }

   static {
      MAP_CODEC = DistributionProvider.mapCodec(ContextFloatProviders.CODEC, WeightedListValue::new);
   }
}
