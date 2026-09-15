package net.minecraft.world.level.storage.loot.providers.number.floats;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.BinaryProvider;

public record Quotient(Holder<ContextFloatProvider> left, Holder<ContextFloatProvider> right) implements ContextFloatProvider, BinaryProvider<ContextFloatProvider> {
   public static final MapCodec<Quotient> MAP_CODEC;

   public Quotient {
      super();
   }

   public MapCodec<Quotient> codec() {
      return MAP_CODEC;
   }

   public float getFloatUnsafe(final LootContext context) {
      return ((ContextFloatProvider)this.left().value()).getFloatUnsafe(context) / ((ContextFloatProvider)this.right().value()).getFloatUnsafe(context);
   }

   static {
      MAP_CODEC = BinaryProvider.mapCodec(ContextFloatProviders.CODEC, Quotient::new);
   }
}
