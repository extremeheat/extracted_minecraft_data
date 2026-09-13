package net.minecraft.world.level.storage.loot.providers.number.floats;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.UnaryProvider;

public record Absolute(Holder<ContextFloatProvider> input) implements ContextFloatProvider, UnaryProvider<ContextFloatProvider> {
   public static final MapCodec<Absolute> MAP_CODEC;

   public Absolute {
      super();
   }

   public MapCodec<Absolute> codec() {
      return MAP_CODEC;
   }

   public float getFloatUnsafe(final LootContext context) {
      return Mth.abs(((ContextFloatProvider)this.input().value()).getFloatUnsafe(context));
   }

   static {
      MAP_CODEC = UnaryProvider.codec(ContextFloatProviders.CODEC, Absolute::new);
   }
}
