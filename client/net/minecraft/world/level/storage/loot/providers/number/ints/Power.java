package net.minecraft.world.level.storage.loot.providers.number.ints;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.PowerProvider;

public record Power(Holder<ContextIntProvider> base, Holder<ContextIntProvider> exponent) implements ContextIntProvider, PowerProvider<ContextIntProvider> {
   public static final MapCodec<Power> MAP_CODEC;

   public Power {
      super();
   }

   public MapCodec<Power> codec() {
      return MAP_CODEC;
   }

   public int getIntUnsafe(final LootContext context) throws ArithmeticException {
      int base = ((ContextIntProvider)this.base.value()).getIntUnsafe(context);
      int exponent = ((ContextIntProvider)this.exponent.value()).getIntUnsafe(context);
      if (base == 0 && exponent == 0) {
         throw new ArithmeticException("Result of 0 to the power of 0 is undefined");
      } else {
         return Math.powExact(base, exponent);
      }
   }

   static {
      MAP_CODEC = PowerProvider.mapCodec(ContextIntProviders.CODEC, Power::new);
   }
}
