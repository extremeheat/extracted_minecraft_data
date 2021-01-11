package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntitySmallFireball extends EntityFireball {
   public EntitySmallFireball(World var1) {
      super(var1);
      this.func_70105_a(0.3125F, 0.3125F);
   }

   public EntitySmallFireball(World var1, EntityLivingBase var2, double var3, double var5, double var7) {
      super(var1, var2, var3, var5, var7);
      this.func_70105_a(0.3125F, 0.3125F);
   }

   public EntitySmallFireball(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.func_70105_a(0.3125F, 0.3125F);
   }

   protected void func_70227_a(MovingObjectPosition var1) {
      if (!this.field_70170_p.field_72995_K) {
         boolean var2;
         if (var1.field_72308_g != null) {
            var2 = var1.field_72308_g.func_70097_a(DamageSource.func_76362_a(this, this.field_70235_a), 5.0F);
            if (var2) {
               this.func_174815_a(this.field_70235_a, var1.field_72308_g);
               if (!var1.field_72308_g.func_70045_F()) {
                  var1.field_72308_g.func_70015_d(5);
               }
            }
         } else {
            var2 = true;
            if (this.field_70235_a != null && this.field_70235_a instanceof EntityLiving) {
               var2 = this.field_70170_p.func_82736_K().func_82766_b("mobGriefing");
            }

            if (var2) {
               BlockPos var3 = var1.func_178782_a().func_177972_a(var1.field_178784_b);
               if (this.field_70170_p.func_175623_d(var3)) {
                  this.field_70170_p.func_175656_a(var3, Blocks.field_150480_ab.func_176223_P());
               }
            }
         }

         this.func_70106_y();
      }

   }

   public boolean func_70067_L() {
      return false;
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      return false;
   }
}
