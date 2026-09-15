package net.minecraft.world.level.storage.loot.providers.number.floats;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.PowerProvider;

public record Power(Holder<ContextFloatProvider> base, Holder<ContextFloatProvider> exponent) implements ContextFloatProvider, PowerProvider<ContextFloatProvider> {
   public static final MapCodec<Power> MAP_CODEC;

   public Power {
      super();
   }

   public MapCodec<Power> codec() {
      return MAP_CODEC;
   }

   public float getFloatUnsafe(final LootContext context) {
      float base = ((ContextFloatProvider)this.base.value()).getFloatUnsafe(context);
      float exponent = ((ContextFloatProvider)this.exponent.value()).getFloatUnsafe(context);
      return base == 0.0F && exponent == 0.0F ? 0.0F / 0.0F : (float)Math.pow((double)base, (double)exponent);
   }

   static {
      MAP_CODEC = PowerProvider.mapCodec(ContextFloatProviders.CODEC, Power::new);
   }
}
