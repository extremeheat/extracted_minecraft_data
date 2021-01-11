package net.minecraft.scoreboard;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class ScoreHealthCriteria extends ScoreDummyCriteria {
   public ScoreHealthCriteria(String var1) {
      super(var1);
   }

   public int func_96635_a(List<EntityPlayer> var1) {
      float var2 = 0.0F;

      EntityPlayer var4;
      for(Iterator var3 = var1.iterator(); var3.hasNext(); var2 += var4.func_110143_aJ() + var4.func_110139_bj()) {
         var4 = (EntityPlayer)var3.next();
      }

      if (var1.size() > 0) {
         var2 /= (float)var1.size();
      }

      return MathHelper.func_76123_f(var2);
   }

   public boolean func_96637_b() {
      return true;
   }

   public IScoreObjectiveCriteria.EnumRenderType func_178790_c() {
      return IScoreObjectiveCriteria.EnumRenderType.HEARTS;
   }
}
