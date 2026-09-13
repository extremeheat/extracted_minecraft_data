package net.minecraft.world.level.storage.loot.providers.number.ints;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.BinaryProvider;

public record Difference(Holder<ContextIntProvider> left, Holder<ContextIntProvider> right) implements ContextIntProvider, BinaryProvider<ContextIntProvider> {
   public static final MapCodec<Difference> MAP_CODEC;

   public Difference {
      super();
   }

   public MapCodec<Difference> codec() {
      return MAP_CODEC;
   }

   public int getIntUnsafe(final LootContext context) {
      return Math.subtractExact(((ContextIntProvider)this.left().value()).getIntUnsafe(context), ((ContextIntProvider)this.right().value()).getIntUnsafe(context));
   }

   static {
      MAP_CODEC = BinaryProvider.mapCodec(ContextIntProviders.CODEC, Difference::new);
   }
}
