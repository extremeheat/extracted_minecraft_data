package net.minecraft.pathfinding;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PathNavigateFlying extends PathNavigate {
   public PathNavigateFlying(EntityLiving var1, World var2) {
      super(var1, var2);
   }

   protected PathFinder func_179679_a() {
      this.field_179695_a = new FlyingNodeProcessor();
      this.field_179695_a.func_186317_a(true);
      return new PathFinder(this.field_179695_a);
   }

   protected boolean func_75485_k() {
      return this.func_212238_t() && this.func_75506_l() || !this.field_75515_a.func_184218_aH();
   }

   protected Vec3d func_75502_i() {
      return new Vec3d(this.field_75515_a.field_70165_t, this.field_75515_a.field_70163_u, this.field_75515_a.field_70161_v);
   }

   public Path func_75494_a(Entity var1) {
      return this.func_179680_a(new BlockPos(var1));
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

   protected boolean func_75493_a(Vec3d var1, Vec3d var2, int var3, int var4, int var5) {
      int var6 = MathHelper.func_76128_c(var1.field_72450_a);
      int var7 = MathHelper.func_76128_c(var1.field_72448_b);
      int var8 = MathHelper.func_76128_c(var1.field_72449_c);
      double var9 = var2.field_72450_a - var1.field_72450_a;
      double var11 = var2.field_72448_b - var1.field_72448_b;
      double var13 = var2.field_72449_c - var1.field_72449_c;
      double var15 = var9 * var9 + var11 * var11 + var13 * var13;
      if (var15 < 1.0E-8D) {
         return false;
      } else {
         double var17 = 1.0D / Math.sqrt(var15);
         var9 *= var17;
         var11 *= var17;
         var13 *= var17;
         double var19 = 1.0D / Math.abs(var9);
         double var21 = 1.0D / Math.abs(var11);
         double var23 = 1.0D / Math.abs(var13);
         double var25 = (double)var6 - var1.field_72450_a;
         double var27 = (double)var7 - var1.field_72448_b;
         double var29 = (double)var8 - var1.field_72449_c;
         if (var9 >= 0.0D) {
            ++var25;
         }

         if (var11 >= 0.0D) {
            ++var27;
         }

         if (var13 >= 0.0D) {
            ++var29;
         }

         var25 /= var9;
         var27 /= var11;
         var29 /= var13;
         int var31 = var9 < 0.0D ? -1 : 1;
         int var32 = var11 < 0.0D ? -1 : 1;
         int var33 = var13 < 0.0D ? -1 : 1;
         int var34 = MathHelper.func_76128_c(var2.field_72450_a);
         int var35 = MathHelper.func_76128_c(var2.field_72448_b);
         int var36 = MathHelper.func_76128_c(var2.field_72449_c);
         int var37 = var34 - var6;
         int var38 = var35 - var7;
         int var39 = var36 - var8;

         while(true) {
            while(var37 * var31 > 0 || var38 * var32 > 0 || var39 * var33 > 0) {
               if (var25 < var29 && var25 <= var27) {
                  var25 += var19;
                  var6 += var31;
                  var37 = var34 - var6;
               } else if (var27 < var25 && var27 <= var29) {
                  var27 += var21;
                  var7 += var32;
                  var38 = var35 - var7;
               } else {
                  var29 += var23;
                  var8 += var33;
                  var39 = var36 - var8;
               }
            }

            return true;
         }
      }
   }

   public void func_192879_a(boolean var1) {
      this.field_179695_a.func_186321_b(var1);
   }

   public void func_192878_b(boolean var1) {
      this.field_179695_a.func_186317_a(var1);
   }

   public boolean func_188555_b(BlockPos var1) {
      return this.field_75513_b.func_180495_p(var1).func_185896_q();
   }
}
