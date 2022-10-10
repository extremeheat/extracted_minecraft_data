package net.minecraft.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;

public class PotionAbsorption extends Potion {
   protected PotionAbsorption(boolean var1, int var2) {
      super(var1, var2);
   }

   public void func_111187_a(EntityLivingBase var1, AbstractAttributeMap var2, int var3) {
      var1.func_110149_m(var1.func_110139_bj() - (float)(4 * (var3 + 1)));
      super.func_111187_a(var1, var2, var3);
   }

   public void func_111185_a(EntityLivingBase var1, AbstractAttributeMap var2, int var3) {
      var1.func_110149_m(var1.func_110139_bj() + (float)(4 * (var3 + 1)));
      super.func_111185_a(var1, var2, var3);
   }
}
