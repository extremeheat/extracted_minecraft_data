package net.minecraft.world.level.storage.loot.providers.number.ints;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;

public interface ContextIntProvider extends Validatable {
   int getIntUnsafe(LootContext context) throws ArithmeticException;

   default int getInt(final LootContext context) {
      try {
         return this.getIntUnsafe(context);
      } catch (ArithmeticException var3) {
         return 0;
      }
   }

   MapCodec<? extends ContextIntProvider> codec();

   static int floatToIntSafe(final float value) {
      if (!Float.isFinite(value)) {
         throw new ArithmeticException("Value " + value + " can't be safely converted to int");
      } else {
         return longToIntSafe((long)value);
      }
   }

   static int longToIntSafe(final long value) {
      int result = (int)value;
      if ((long)result != value) {
         throw new ArithmeticException("Value " + value + " can't be safely converted to int");
      } else {
         return result;
      }
   }
}
