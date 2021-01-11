package net.minecraft.entity.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityExpBottle extends EntityThrowable {
   public EntityExpBottle(World var1) {
      super(var1);
   }

   public EntityExpBottle(World var1, EntityLivingBase var2) {
      super(var1, var2);
   }

   public EntityExpBottle(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   protected float func_70185_h() {
      return 0.07F;
   }

   protected float func_70182_d() {
      return 0.7F;
   }

   protected float func_70183_g() {
      return -20.0F;
   }

   protected void func_70184_a(MovingObjectPosition var1) {
      if (!this.field_70170_p.field_72995_K) {
         this.field_70170_p.func_175718_b(2002, new BlockPos(this), 0);
         int var2 = 3 + this.field_70170_p.field_73012_v.nextInt(5) + this.field_70170_p.field_73012_v.nextInt(5);

         while(var2 > 0) {
            int var3 = EntityXPOrb.func_70527_a(var2);
            var2 -= var3;
            this.field_70170_p.func_72838_d(new EntityXPOrb(this.field_70170_p, this.field_70165_t, this.field_70163_u, this.field_70161_v, var3));
         }

         this.func_70106_y();
      }

   }
}
