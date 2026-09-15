package net.minecraft.world.level.storage.loot.providers.number.floats;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.UnaryProvider;

public record SquareRoot(Holder<ContextFloatProvider> input) implements ContextFloatProvider, UnaryProvider<ContextFloatProvider> {
   public static final MapCodec<SquareRoot> MAP_CODEC;

   public SquareRoot {
      super();
   }

   public MapCodec<SquareRoot> codec() {
      return MAP_CODEC;
   }

   public float getFloatUnsafe(final LootContext context) {
      return Mth.sqrt(((ContextFloatProvider)this.input().value()).getFloatUnsafe(context));
   }

   static {
      MAP_CODEC = UnaryProvider.codec(ContextFloatProviders.CODEC, SquareRoot::new);
   }
}
