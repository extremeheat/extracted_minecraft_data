package net.minecraft.pathfinding;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityDolphin;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PathNavigateSwimmer extends PathNavigate {
   private boolean field_205155_i;

   public PathNavigateSwimmer(EntityLiving var1, World var2) {
      super(var1, var2);
   }

   protected PathFinder func_179679_a() {
      this.field_205155_i = this.field_75515_a instanceof EntityDolphin;
      this.field_179695_a = new SwimNodeProcessor(this.field_205155_i);
      return new PathFinder(this.field_179695_a);
   }

   protected boolean func_75485_k() {
      return this.field_205155_i || this.func_75506_l();
   }

   protected Vec3d func_75502_i() {
      return new Vec3d(this.field_75515_a.field_70165_t, this.field_75515_a.field_70163_u + (double)this.field_75515_a.field_70131_O * 0.5D, this.field_75515_a.field_70161_v);
   }

   public void func_75501_e() {
      ++this.field_75510_g;
      if (this.field_188562_p) {
         this.func_188554_j();
      }

      if (!this.func_75500_f()) {
         Vec3d var1;
         if (this.func_75485_k()) {
            this.func_75508_h();
         } else if (this.field_75514_c != null && this.field_75514_c.func_75873_e() < this.field_75514_c.func_75874_d()) {
            var1 = this.field_75514_c.func_75881_a(this.field_75515_a, this.field_75514_c.func_75873_e());
            if (MathHelper.func_76128_c(this.field_75515_a.field_70165_t) == MathHelper.func_76128_c(var1.field_72450_a) && MathHelper.func_76128_c(this.field_75515_a.field_70163_u) == MathHelper.func_76128_c(var1.field_72448_b) && MathHelper.func_76128_c(this.field_75515_a.field_70161_v) == MathHelper.func_76128_c(var1.field_72449_c)) {
               this.field_75514_c.func_75872_c(this.field_75514_c.func_75873_e() + 1);
            }
         }

         this.func_192876_m();
         if (!this.func_75500_f()) {
            var1 = this.field_75514_c.func_75878_a(this.field_75515_a);
            this.field_75515_a.func_70605_aq().func_75642_a(var1.field_72450_a, var1.field_72448_b, var1.field_72449_c, this.field_75511_d);
         }
      }
   }

   protected void func_75508_h() {
      if (this.field_75514_c != null) {
         Vec3d var1 = this.func_75502_i();
         float var2 = this.field_75515_a.field_70130_N > 0.75F ? this.field_75515_a.field_70130_N / 2.0F : 0.75F - this.field_75515_a.field_70130_N / 2.0F;
         if ((double)MathHelper.func_76135_e((float)this.field_75515_a.field_70159_w) > 0.2D || (double)MathHelper.func_76135_e((float)this.field_75515_a.field_70179_y) > 0.2D) {
            var2 *= MathHelper.func_76133_a(this.field_75515_a.field_70159_w * this.field_75515_a.field_70159_w + this.field_75515_a.field_70181_x * this.field_75515_a.field_70181_x + this.field_75515_a.field_70179_y * this.field_75515_a.field_70179_y) * 6.0F;
         }

         boolean var3 = true;
         Vec3d var4 = this.field_75514_c.func_186310_f();
         if (MathHelper.func_76135_e((float)(this.field_75515_a.field_70165_t - (var4.field_72450_a + 0.5D))) < var2 && MathHelper.func_76135_e((float)(this.field_75515_a.field_70161_v - (var4.field_72449_c + 0.5D))) < var2 && Math.abs(this.field_75515_a.field_70163_u - var4.field_72448_b) < (double)(var2 * 2.0F)) {
            this.field_75514_c.func_75875_a();
         }

         for(int var5 = Math.min(this.field_75514_c.func_75873_e() + 6, this.field_75514_c.func_75874_d() - 1); var5 > this.field_75514_c.func_75873_e(); --var5) {
            var4 = this.field_75514_c.func_75881_a(this.field_75515_a, var5);
            if (var4.func_72436_e(var1) <= 36.0D && this.func_75493_a(var1, var4, 0, 0, 0)) {
               this.field_75514_c.func_75872_c(var5);
               break;
            }
         }

         this.func_179677_a(var1);
      }
   }

   protected void func_179677_a(Vec3d var1) {
      if (this.field_75510_g - this.field_75520_h > 100) {
         if (var1.func_72436_e(this.field_75521_i) < 2.25D) {
            this.func_75499_g();
         }

         this.field_75520_h = this.field_75510_g;
         this.field_75521_i = var1;
      }

      if (this.field_75514_c != null && !this.field_75514_c.func_75879_b()) {
         Vec3d var2 = this.field_75514_c.func_186310_f();
         if (var2.equals(this.field_188557_k)) {
            this.field_188558_l += Util.func_211177_b() - this.field_188559_m;
         } else {
            this.field_188557_k = var2;
            double var3 = var1.func_72438_d(this.field_188557_k);
            this.field_188560_n = this.field_75515_a.func_70689_ay() > 0.0F ? var3 / (double)this.field_75515_a.func_70689_ay() * 100.0D : 0.0D;
         }

         if (this.field_188560_n > 0.0D && (double)this.field_188558_l > this.field_188560_n * 2.0D) {
            this.field_188557_k = Vec3d.field_186680_a;
            this.field_188558_l = 0L;
            this.field_188560_n = 0.0D;
            this.func_75499_g();
         }

         this.field_188559_m = Util.func_211177_b();
      }

   }

   protected boolean func_75493_a(Vec3d var1, Vec3d var2, int var3, int var4, int var5) {
      RayTraceResult var6 = this.field_75513_b.func_200259_a(var1, new Vec3d(var2.field_72450_a, var2.field_72448_b + (double)this.field_75515_a.field_70131_O * 0.5D, var2.field_72449_c), RayTraceFluidMode.NEVER, true, false);
      return var6 == null || var6.field_72313_a == RayTraceResult.Type.MISS;
   }

   public boolean func_188555_b(BlockPos var1) {
      return !this.field_75513_b.func_180495_p(var1).func_200015_d(this.field_75513_b, var1);
   }

   public void func_212239_d(boolean var1) {
   }
}
