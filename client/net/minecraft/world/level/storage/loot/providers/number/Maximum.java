package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.storage.loot.LootContext;

public record Maximum(HolderSet<NumberProvider> operands) implements Aggregate {
   public static final MapCodec<Maximum> MAP_CODEC = Aggregate.<Maximum>codec(Maximum::new);

   public Maximum {
      super();
   }

   @SafeVarargs
   public static Holder<NumberProvider> maximum(final Holder<NumberProvider>... operands) {
      return Holder.<NumberProvider>direct(new Maximum(HolderSet.direct(operands)));
   }

   public MapCodec<Maximum> codec() {
      return MAP_CODEC;
   }

   public float getFloat(final LootContext context) {
      float value = -3.4028235E38F;

      for(Holder<NumberProvider> operand : this.operands) {
         value = Math.max(value, ((NumberProvider)operand.value()).getFloat(context));
      }

      return value;
   }

   public int getInt(final LootContext context) {
      int value = -2147483647;

      for(Holder<NumberProvider> operand : this.operands) {
         value = Math.max(value, ((NumberProvider)operand.value()).getInt(context));
      }

      return value;
   }
}
