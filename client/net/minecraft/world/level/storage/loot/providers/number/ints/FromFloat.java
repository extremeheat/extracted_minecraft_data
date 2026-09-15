package net.minecraft.world.level.storage.loot.providers.number.ints;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.UnaryProvider;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProvider;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;

public record FromFloat(Holder<ContextFloatProvider> input) implements ContextIntProvider, UnaryProvider<ContextFloatProvider> {
   public static final MapCodec<FromFloat> MAP_CODEC;

   public FromFloat {
      super();
   }

   public MapCodec<FromFloat> codec() {
      return MAP_CODEC;
   }

   public int getIntUnsafe(final LootContext context) {
      return ContextIntProvider.floatToIntSafe(((ContextFloatProvider)this.input().value()).getFloatUnsafe(context));
   }

   static {
      MAP_CODEC = UnaryProvider.codec(ContextFloatProviders.CODEC, FromFloat::new);
   }
}
