package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
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

   @Override
   protected void func_70227_a(MovingObjectPosition var1) {
      if (!this.field_70170_p.field_72995_K) {
         if (var1.field_72308_g != null) {
            if (!var1.field_72308_g.func_70045_F() && var1.field_72308_g.func_70097_a(DamageSource.func_76362_a(this, this.field_70235_a), 5.0F)) {
               var1.field_72308_g.func_70015_d(5);
            }
         } else {
            int var2 = var1.field_72311_b;
            int var3 = var1.field_72312_c;
            int var4 = var1.field_72309_d;
            switch(var1.field_72310_e) {
               case 0:
                  --var3;
                  break;
               case 1:
                  ++var3;
                  break;
               case 2:
                  --var4;
                  break;
               case 3:
                  ++var4;
                  break;
               case 4:
                  --var2;
                  break;
               case 5:
                  ++var2;
            }

            if (this.field_70170_p.func_147437_c(var2, var3, var4)) {
               this.field_70170_p.func_147449_b(var2, var3, var4, Blocks.field_150480_ab);
            }
         }

         this.func_70106_y();
      }
   }

   @Override
   public boolean func_70067_L() {
      return false;
   }

   @Override
   public boolean func_70097_a(DamageSource var1, float var2) {
      return false;
   }
}
