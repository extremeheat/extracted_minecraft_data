package net.minecraft.world.level.storage.loot.providers.number.floats;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.BinaryProvider;

public record Modulus(Holder<ContextFloatProvider> left, Holder<ContextFloatProvider> right) implements ContextFloatProvider, BinaryProvider<ContextFloatProvider> {
   public static final MapCodec<Modulus> MAP_CODEC;

   public Modulus {
      super();
   }

   public MapCodec<Modulus> codec() {
      return MAP_CODEC;
   }

   public float getFloatUnsafe(final LootContext context) {
      float rightValue = ((ContextFloatProvider)this.right().value()).getFloatUnsafe(context);
      return rightValue == 0.0F ? 0.0F / 0.0F : ((ContextFloatProvider)this.left().value()).getFloatUnsafe(context) % rightValue;
   }

   static {
      MAP_CODEC = BinaryProvider.mapCodec(ContextFloatProviders.CODEC, Modulus::new);
   }
}
