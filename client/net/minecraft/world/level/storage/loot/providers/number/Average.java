package net.minecraft.world.level.storage.loot.providers.number;

import com.google.common.primitives.Ints;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.storage.loot.LootContext;

public record Average(HolderSet<NumberProvider> operands) implements Aggregate {
   public static final MapCodec<Average> MAP_CODEC = Aggregate.<Average>codec(Average::new);

   public Average {
      super();
   }

   @SafeVarargs
   public static Holder<NumberProvider> average(final Holder<NumberProvider>... operands) {
      return Holder.<NumberProvider>direct(new Average(HolderSet.direct(operands)));
   }

   public MapCodec<Average> codec() {
      return MAP_CODEC;
   }

   public float getFloat(final LootContext context) {
      float sum = 0.0F;
      int count = 0;

      for(Holder<NumberProvider> operand : this.operands) {
         sum += ((NumberProvider)operand.value()).getFloat(context);
         ++count;
      }

      return count == 0 ? 0.0F : sum / (float)count;
   }

   public int getInt(final LootContext context) {
      long sum = 0L;
      long count = 0L;

      for(Holder<NumberProvider> operand : this.operands) {
         sum += (long)((NumberProvider)operand.value()).getInt(context);
         ++count;
      }

      return count == 0L ? 0 : Ints.saturatedCast(sum / count);
   }
}
