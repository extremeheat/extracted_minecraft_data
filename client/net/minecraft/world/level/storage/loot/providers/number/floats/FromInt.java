package net.minecraft.world.level.storage.loot.providers.number.floats;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.UnaryProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

public record FromInt(Holder<ContextIntProvider> input) implements ContextFloatProvider, UnaryProvider<ContextIntProvider> {
   public static final MapCodec<FromInt> MAP_CODEC;

   public FromInt {
      super();
   }

   public MapCodec<FromInt> codec() {
      return MAP_CODEC;
   }

   public float getFloatUnsafe(final LootContext context) {
      return ContextFloatProvider.intToFloatSafe(((ContextIntProvider)this.input().value()).getIntUnsafe(context));
   }

   static {
      MAP_CODEC = UnaryProvider.codec(ContextIntProviders.CODEC, FromInt::new);
   }
}
