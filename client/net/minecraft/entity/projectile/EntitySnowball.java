package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntitySnowball extends EntityThrowable {
   public EntitySnowball(World var1) {
      super(var1);
   }

   public EntitySnowball(World var1, EntityLivingBase var2) {
      super(var1, var2);
   }

   public EntitySnowball(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   protected void func_70184_a(MovingObjectPosition var1) {
      if (var1.field_72308_g != null) {
         byte var2 = 0;
         if (var1.field_72308_g instanceof EntityBlaze) {
            var2 = 3;
         }

         var1.field_72308_g.func_70097_a(DamageSource.func_76356_a(this, this.func_85052_h()), (float)var2);
      }

      for(int var3 = 0; var3 < 8; ++var3) {
         this.field_70170_p.func_175688_a(EnumParticleTypes.SNOWBALL, this.field_70165_t, this.field_70163_u, this.field_70161_v, 0.0D, 0.0D, 0.0D);
      }

      if (!this.field_70170_p.field_72995_K) {
         this.func_70106_y();
      }

   }
}
