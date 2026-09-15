package net.minecraft.world.level.storage.loot.providers.number.floats;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.UnaryProvider;

public record Truncate(Holder<ContextFloatProvider> input) implements ContextFloatProvider, UnaryProvider<ContextFloatProvider> {
   public static final MapCodec<Truncate> MAP_CODEC;

   public Truncate {
      super();
   }

   public MapCodec<Truncate> codec() {
      return MAP_CODEC;
   }

   public float getFloatUnsafe(final LootContext context) {
      float value = ((ContextFloatProvider)this.input().value()).getFloatUnsafe(context);
      return value > 0.0F ? (float)Math.floor((double)value) : (float)Math.ceil((double)value);
   }

   static {
      MAP_CODEC = UnaryProvider.codec(ContextFloatProviders.CODEC, Truncate::new);
   }
}
