package net.minecraft.world.entity.ai.attributes;

import javax.annotation.Nullable;
import net.minecraft.util.Mth;

public class RangedAttribute extends BaseAttribute {
   private final double minValue;
   private final double maxValue;
   private String importLegacyName;

   public RangedAttribute(@Nullable Attribute var1, String var2, double var3, double var5, double var7) {
      super(var1, var2, var3);
      this.minValue = var5;
      this.maxValue = var7;
      if (var5 > var7) {
         throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
      } else if (var3 < var5) {
         throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
      } else if (var3 > var7) {
         throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
      }
   }

   public RangedAttribute importLegacyName(String var1) {
      this.importLegacyName = var1;
      return this;
   }

   public String getImportLegacyName() {
      return this.importLegacyName;
   }

   public double sanitizeValue(double var1) {
      var1 = Mth.clamp(var1, this.minValue, this.maxValue);
      return var1;
   }
}
