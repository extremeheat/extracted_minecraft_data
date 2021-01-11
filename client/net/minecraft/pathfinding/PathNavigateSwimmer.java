package net.minecraft.pathfinding;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.pathfinder.SwimNodeProcessor;

public class PathNavigateSwimmer extends PathNavigate {
   public PathNavigateSwimmer(EntityLiving var1, World var2) {
      super(var1, var2);
   }

   protected PathFinder func_179679_a() {
      return new PathFinder(new SwimNodeProcessor());
   }

   protected boolean func_75485_k() {
      return this.func_75506_l();
   }

   protected Vec3 func_75502_i() {
      return new Vec3(this.field_75515_a.field_70165_t, this.field_75515_a.field_70163_u + (double)this.field_75515_a.field_70131_O * 0.5D, this.field_75515_a.field_70161_v);
   }

   protected void func_75508_h() {
      Vec3 var1 = this.func_75502_i();
      float var2 = this.field_75515_a.field_70130_N * this.field_75515_a.field_70130_N;
      byte var3 = 6;
      if (var1.func_72436_e(this.field_75514_c.func_75881_a(this.field_75515_a, this.field_75514_c.func_75873_e())) < (double)var2) {
         this.field_75514_c.func_75875_a();
      }

      for(int var4 = Math.min(this.field_75514_c.func_75873_e() + var3, this.field_75514_c.func_75874_d() - 1); var4 > this.field_75514_c.func_75873_e(); --var4) {
         Vec3 var5 = this.field_75514_c.func_75881_a(this.field_75515_a, var4);
         if (var5.func_72436_e(var1) <= 36.0D && this.func_75493_a(var1, var5, 0, 0, 0)) {
            this.field_75514_c.func_75872_c(var4);
            break;
         }
      }

      this.func_179677_a(var1);
   }

   protected void func_75487_m() {
      super.func_75487_m();
   }

   protected boolean func_75493_a(Vec3 var1, Vec3 var2, int var3, int var4, int var5) {
      MovingObjectPosition var6 = this.field_75513_b.func_147447_a(var1, new Vec3(var2.field_72450_a, var2.field_72448_b + (double)this.field_75515_a.field_70131_O * 0.5D, var2.field_72449_c), false, true, false);
      return var6 == null || var6.field_72313_a == MovingObjectPosition.MovingObjectType.MISS;
   }
}
