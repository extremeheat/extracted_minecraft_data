package net.minecraft.world.level.storage.loot.providers.number.floats;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.UnaryProvider;

public record Negate(Holder<ContextFloatProvider> input) implements ContextFloatProvider, UnaryProvider<ContextFloatProvider> {
   public static final MapCodec<Negate> MAP_CODEC;

   public Negate {
      super();
   }

   public MapCodec<Negate> codec() {
      return MAP_CODEC;
   }

   public float getFloatUnsafe(final LootContext context) {
      return -((ContextFloatProvider)this.input().value()).getFloatUnsafe(context);
   }

   static {
      MAP_CODEC = UnaryProvider.codec(ContextFloatProviders.CODEC, Negate::new);
   }
}
