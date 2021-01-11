package net.minecraft.pathfinding;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class PathNavigateClimber extends PathNavigateGround {
   private BlockPos field_179696_f;

   public PathNavigateClimber(EntityLiving var1, World var2) {
      super(var1, var2);
   }

   public PathEntity func_179680_a(BlockPos var1) {
      this.field_179696_f = var1;
      return super.func_179680_a(var1);
   }

   public PathEntity func_75494_a(Entity var1) {
      this.field_179696_f = new BlockPos(var1);
      return super.func_75494_a(var1);
   }

   public boolean func_75497_a(Entity var1, double var2) {
      PathEntity var4 = this.func_75494_a(var1);
      if (var4 != null) {
         return this.func_75484_a(var4, var2);
      } else {
         this.field_179696_f = new BlockPos(var1);
         this.field_75511_d = var2;
         return true;
      }
   }

   public void func_75501_e() {
      if (!this.func_75500_f()) {
         super.func_75501_e();
      } else {
         if (this.field_179696_f != null) {
            double var1 = (double)(this.field_75515_a.field_70130_N * this.field_75515_a.field_70130_N);
            if (this.field_75515_a.func_174831_c(this.field_179696_f) >= var1 && (this.field_75515_a.field_70163_u <= (double)this.field_179696_f.func_177956_o() || this.field_75515_a.func_174831_c(new BlockPos(this.field_179696_f.func_177958_n(), MathHelper.func_76128_c(this.field_75515_a.field_70163_u), this.field_179696_f.func_177952_p())) >= var1)) {
               this.field_75515_a.func_70605_aq().func_75642_a((double)this.field_179696_f.func_177958_n(), (double)this.field_179696_f.func_177956_o(), (double)this.field_179696_f.func_177952_p(), this.field_75511_d);
            } else {
               this.field_179696_f = null;
            }
         }

      }
   }
}
