package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.DamageSource;

public abstract class PhaseSittingBase extends PhaseBase {
   public PhaseSittingBase(EntityDragon var1) {
      super(var1);
   }

   public boolean func_188654_a() {
      return true;
   }

   public float func_188656_a(MultiPartEntityPart var1, DamageSource var2, float var3) {
      if (var2.func_76364_f() instanceof EntityArrow) {
         var2.func_76364_f().func_70015_d(1);
         return 0.0F;
      } else {
         return super.func_188656_a(var1, var2, var3);
      }
   }
}
