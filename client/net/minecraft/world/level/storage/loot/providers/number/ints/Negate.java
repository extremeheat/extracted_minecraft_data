package net.minecraft.world.level.storage.loot.providers.number.ints;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.UnaryProvider;

public record Negate(Holder<ContextIntProvider> input) implements ContextIntProvider, UnaryProvider<ContextIntProvider> {
   public static final MapCodec<Negate> MAP_CODEC;

   public Negate {
      super();
   }

   public MapCodec<Negate> codec() {
      return MAP_CODEC;
   }

   public int getIntUnsafe(final LootContext context) {
      return Math.negateExact(((ContextIntProvider)this.input().value()).getIntUnsafe(context));
   }

   static {
      MAP_CODEC = UnaryProvider.codec(ContextIntProviders.CODEC, Negate::new);
   }
}
