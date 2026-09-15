package net.minecraft.world.level.storage.loot.providers.number.ints;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.DispatcherProvider;

public record NumberDispatcher(List<DispatcherProvider.Case<ContextIntProvider>> cases, Holder<ContextIntProvider> defaultValue) implements ContextIntProvider, DispatcherProvider<ContextIntProvider> {
   public static final MapCodec<NumberDispatcher> MAP_CODEC;

   public NumberDispatcher {
      super();
   }

   public int getIntUnsafe(final LootContext context) {
      return ((ContextIntProvider)this.selectValue(context).value()).getIntUnsafe(context);
   }

   public MapCodec<NumberDispatcher> codec() {
      return MAP_CODEC;
   }

   static {
      MAP_CODEC = DispatcherProvider.mapCodec(ContextIntProviders.CODEC, ContextIntProviders.exactly(0), NumberDispatcher::new);
   }
}
