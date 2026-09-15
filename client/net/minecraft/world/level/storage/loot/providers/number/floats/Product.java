package net.minecraft.world.level.storage.loot.providers.number.floats;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.AggregateProvider;

public record Product(HolderSet<ContextFloatProvider> inputs) implements ContextFloatProvider, AggregateProvider<ContextFloatProvider> {
   public static final MapCodec<Product> MAP_CODEC;

   public Product {
      super();
   }

   public MapCodec<Product> codec() {
      return MAP_CODEC;
   }

   public float getFloatUnsafe(final LootContext context) {
      float value = 1.0F;

      for(Holder<ContextFloatProvider> input : this.inputs()) {
         value *= ((ContextFloatProvider)input.value()).getFloatUnsafe(context);
      }

      return value;
   }

   static {
      MAP_CODEC = AggregateProvider.mapCodec(ContextFloatProviders.LIST_CODEC, Product::new);
   }
}
