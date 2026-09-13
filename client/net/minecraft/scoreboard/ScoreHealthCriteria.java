package net.minecraft.scoreboard;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class ScoreHealthCriteria extends ScoreDummyCriteria {
   public ScoreHealthCriteria(String var1) {
      super(var1);
   }

   @Override
   public int func_96635_a(List var1) {
      float var2 = 0.0F;

      for(EntityPlayer var4 : var1) {
         var2 += var4.func_110143_aJ() + var4.func_110139_bj();
      }

      if (var1.size() > 0) {
         var2 /= (float)var1.size();
      }

      return MathHelper.func_76123_f(var2);
   }

   @Override
   public boolean func_96637_b() {
      return true;
   }
}
