package net.minecraft.entity.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.PotionTypes;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityExpBottle extends EntityThrowable {
   public EntityExpBottle(World var1) {
      super(EntityType.field_200753_as, var1);
   }

   public EntityExpBottle(World var1, EntityLivingBase var2) {
      super(EntityType.field_200753_as, var2, var1);
   }

   public EntityExpBottle(World var1, double var2, double var4, double var6) {
      super(EntityType.field_200753_as, var2, var4, var6, var1);
   }

   protected float func_70185_h() {
      return 0.07F;
   }

   protected void func_70184_a(RayTraceResult var1) {
      if (!this.field_70170_p.field_72995_K) {
         this.field_70170_p.func_175718_b(2002, new BlockPos(this), PotionUtils.func_185183_a(PotionTypes.field_185230_b));
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
