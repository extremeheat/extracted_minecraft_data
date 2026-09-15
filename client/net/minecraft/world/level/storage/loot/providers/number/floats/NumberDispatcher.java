package net.minecraft.world.level.storage.loot.providers.number.floats;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.DispatcherProvider;

public record NumberDispatcher(List<DispatcherProvider.Case<ContextFloatProvider>> cases, Holder<ContextFloatProvider> defaultValue) implements ContextFloatProvider, DispatcherProvider<ContextFloatProvider> {
   public static final MapCodec<NumberDispatcher> MAP_CODEC;

   public NumberDispatcher {
      super();
   }

   public float getFloatUnsafe(final LootContext context) {
      return ((ContextFloatProvider)this.selectValue(context).value()).getFloatUnsafe(context);
   }

   public MapCodec<NumberDispatcher> codec() {
      return MAP_CODEC;
   }

   static {
      MAP_CODEC = DispatcherProvider.mapCodec(ContextFloatProviders.CODEC, ContextFloatProviders.exactly(0.0F), NumberDispatcher::new);
   }
}
