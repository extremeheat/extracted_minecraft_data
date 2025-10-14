package net.minecraft.world.attribute;

import com.mojang.serialization.DataResult;
import net.minecraft.util.Mth;

public interface AttributeRange<Value> {
   AttributeRange<Float> UNIT_FLOAT = ofFloat(0.0F, 1.0F);
   AttributeRange<Float> NON_NEGATIVE_FLOAT = ofFloat(0.0F, 1.0F / 0.0F);

   static <Value> AttributeRange<Value> any() {
      return new AttributeRange<Value>() {
         public DataResult<Value> validate(Value var1) {
            return DataResult.success(var1);
         }

         public Value sanitize(Value var1) {
            return var1;
         }
      };
   }

   static AttributeRange<Float> ofFloat(final float var0, final float var1) {
      return new AttributeRange<Float>() {
         public DataResult<Float> validate(Float var1x) {
            return var1x >= var0 && var1x <= var1 ? DataResult.success(var1x) : DataResult.error(() -> var1x + " is not in range [" + var0 + "; " + var1 + "]");
         }

         public Float sanitize(Float var1x) {
            return var1x >= var0 && var1x <= var1 ? var1x : Mth.clamp(var1x, var0, var1);
         }
      };
   }

   DataResult<Value> validate(Value var1);

   Value sanitize(Value var1);
}
