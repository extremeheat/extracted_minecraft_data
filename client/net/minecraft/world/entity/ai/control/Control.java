package net.minecraft.world.entity.ai.control;

import net.minecraft.util.Mth;

public interface Control {
   default float rotateTowards(float var1, float var2, float var3) {
      float var4 = Mth.degreesDifference(var1, var2);
      float var5 = Mth.clamp(var4, -var3, var3);
      return var1 + var5;
   }
}
