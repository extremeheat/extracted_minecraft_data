package net.minecraft.world.level.storage.loot.providers.number.floats;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;

public interface ContextFloatProvider extends Validatable {
   float getFloatUnsafe(LootContext context) throws ArithmeticException;

   default float getFloat(final LootContext context) {
      try {
         float result = this.getFloatUnsafe(context);
         if (Float.isFinite(result)) {
            return result;
         }
      } catch (ArithmeticException var3) {
      }

      return 0.0F;
   }

   default float getFloatOrThrow(final LootContext context) throws ArithmeticException {
      float value = this.getFloatUnsafe(context);
      if (!Float.isFinite(value)) {
         throw new ArithmeticException("Invalid value: " + value);
      } else {
         return value;
      }
   }

   MapCodec<? extends ContextFloatProvider> codec();

   static float intToFloatSafe(final int value) {
      return (float)value;
   }
}
