package net.minecraft.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;

public class PotionHealthBoost extends Potion {
   public PotionHealthBoost(boolean var1, int var2) {
      super(var1, var2);
   }

   public void func_111187_a(EntityLivingBase var1, AbstractAttributeMap var2, int var3) {
      super.func_111187_a(var1, var2, var3);
      if (var1.func_110143_aJ() > var1.func_110138_aP()) {
         var1.func_70606_j(var1.func_110138_aP());
      }

   }
}
