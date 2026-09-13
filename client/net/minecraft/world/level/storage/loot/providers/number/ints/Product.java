package net.minecraft.world.level.storage.loot.providers.number.ints;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.AggregateProvider;

public record Product(HolderSet<ContextIntProvider> inputs) implements ContextIntProvider, AggregateProvider<ContextIntProvider> {
   public static final MapCodec<Product> MAP_CODEC;

   public Product {
      super();
   }

   public MapCodec<Product> codec() {
      return MAP_CODEC;
   }

   public int getIntUnsafe(final LootContext context) {
      long value = 1L;

      for(Holder<ContextIntProvider> input : this.inputs()) {
         value *= (long)((ContextIntProvider)input.value()).getIntUnsafe(context);
      }

      return ContextIntProvider.longToIntSafe(value);
   }

   static {
      MAP_CODEC = AggregateProvider.mapCodec(ContextIntProviders.LIST_CODEC, Product::new);
   }
}
