package net.minecraft.world.level.storage.loot.providers.number;

import com.google.common.primitives.Ints;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.storage.loot.LootContext;

public record Product(HolderSet<NumberProvider> operands) implements Aggregate {
   public static final MapCodec<Product> MAP_CODEC = Aggregate.<Product>codec(Product::new);

   public Product {
      super();
   }

   @SafeVarargs
   public static Holder<NumberProvider> product(final Holder<NumberProvider>... operands) {
      return Holder.<NumberProvider>direct(new Product(HolderSet.direct(operands)));
   }

   public MapCodec<Product> codec() {
      return MAP_CODEC;
   }

   public float getFloat(final LootContext context) {
      float value = 1.0F;

      for(Holder<NumberProvider> operand : this.operands) {
         value *= ((NumberProvider)operand.value()).getFloat(context);
      }

      return value;
   }

   public int getInt(final LootContext context) {
      long value = 1L;

      for(Holder<NumberProvider> operand : this.operands) {
         value *= (long)((NumberProvider)operand.value()).getInt(context);
      }

      return Ints.saturatedCast(value);
   }
}
