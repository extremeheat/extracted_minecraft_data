package net.minecraft.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;

public class PotionAbsoption extends Potion {
   protected PotionAbsoption(int var1, boolean var2, int var3) {
      super(var1, var2, var3);
   }

   @Override
   public void func_111187_a(EntityLivingBase var1, BaseAttributeMap var2, int var3) {
      var1.func_110149_m(var1.func_110139_bj() - (float)(4 * (var3 + 1)));
      super.func_111187_a(var1, var2, var3);
   }

   @Override
   public void func_111185_a(EntityLivingBase var1, BaseAttributeMap var2, int var3) {
      var1.func_110149_m(var1.func_110139_bj() + (float)(4 * (var3 + 1)));
      super.func_111185_a(var1, var2, var3);
   }
}
