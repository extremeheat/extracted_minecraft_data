package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.storage.loot.LootContext;

public record Minimum(HolderSet<NumberProvider> operands) implements Aggregate {
   public static final MapCodec<Minimum> MAP_CODEC = Aggregate.<Minimum>codec(Minimum::new);

   public Minimum {
      super();
   }

   @SafeVarargs
   public static Holder<NumberProvider> minimum(final Holder<NumberProvider>... operands) {
      return Holder.<NumberProvider>direct(new Minimum(HolderSet.direct(operands)));
   }

   public MapCodec<Minimum> codec() {
      return MAP_CODEC;
   }

   public float getFloat(final LootContext context) {
      float value = 3.4028235E38F;

      for(Holder<NumberProvider> operand : this.operands) {
         value = Math.min(value, ((NumberProvider)operand.value()).getFloat(context));
      }

      return value;
   }

   public int getInt(final LootContext context) {
      int value = 2147483647;

      for(Holder<NumberProvider> operand : this.operands) {
         value = Math.min(value, ((NumberProvider)operand.value()).getInt(context));
      }

      return value;
   }
}
