package net.minecraft.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class PathNavigate {
   private EntityLiving field_75515_a;
   private World field_75513_b;
   private PathEntity field_75514_c;
   private double field_75511_d;
   private IAttributeInstance field_75512_e;
   private boolean field_75509_f;
   private int field_75510_g;
   private int field_75520_h;
   private Vec3 field_75521_i = Vec3.func_72443_a(0.0, 0.0, 0.0);
   private boolean field_75518_j = true;
   private boolean field_75519_k;
   private boolean field_75516_l;
   private boolean field_75517_m;

   public PathNavigate(EntityLiving var1, World var2) {
      super();
      this.field_75515_a = var1;
      this.field_75513_b = var2;
      this.field_75512_e = var1.func_110148_a(SharedMonsterAttributes.field_111265_b);
   }

   public void func_75491_a(boolean var1) {
      this.field_75516_l = var1;
   }

   public boolean func_75486_a() {
      return this.field_75516_l;
   }

   public void func_75498_b(boolean var1) {
      this.field_75519_k = var1;
   }

   public void func_75490_c(boolean var1) {
      this.field_75518_j = var1;
   }

   public boolean func_75507_c() {
      return this.field_75519_k;
   }

   public void func_75504_d(boolean var1) {
      this.field_75509_f = var1;
   }

   public void func_75489_a(double var1) {
      this.field_75511_d = var1;
   }

   public void func_75495_e(boolean var1) {
      this.field_75517_m = var1;
   }

   public float func_111269_d() {
      return (float)this.field_75512_e.func_111126_e();
   }

   public PathEntity func_75488_a(double var1, double var3, double var5) {
      return !this.func_75485_k()
         ? null
         : this.field_75513_b
            .func_72844_a(
               this.field_75515_a,
               MathHelper.func_76128_c(var1),
               (int)var3,
               MathHelper.func_76128_c(var5),
               this.func_111269_d(),
               this.field_75518_j,
               this.field_75519_k,
               this.field_75516_l,
               this.field_75517_m
            );
   }

   public boolean func_75492_a(double var1, double var3, double var5, double var7) {
      PathEntity var9 = this.func_75488_a((double)MathHelper.func_76128_c(var1), (double)((int)var3), (double)MathHelper.func_76128_c(var5));
      return this.func_75484_a(var9, var7);
   }

   public PathEntity func_75494_a(Entity var1) {
      return !this.func_75485_k()
         ? null
         : this.field_75513_b
            .func_72865_a(this.field_75515_a, var1, this.func_111269_d(), this.field_75518_j, this.field_75519_k, this.field_75516_l, this.field_75517_m);
   }

   public boolean func_75497_a(Entity var1, double var2) {
      PathEntity var4 = this.func_75494_a(var1);
      return var4 != null ? this.func_75484_a(var4, var2) : false;
   }

   public boolean func_75484_a(PathEntity var1, double var2) {
      if (var1 == null) {
         this.field_75514_c = null;
         return false;
      } else {
         if (!var1.func_75876_a(this.field_75514_c)) {
            this.field_75514_c = var1;
         }

         if (this.field_75509_f) {
            this.func_75487_m();
         }

         if (this.field_75514_c.func_75874_d() == 0) {
            return false;
         } else {
            this.field_75511_d = var2;
            Vec3 var4 = this.func_75502_i();
            this.field_75520_h = this.field_75510_g;
            this.field_75521_i.field_72450_a = var4.field_72450_a;
            this.field_75521_i.field_72448_b = var4.field_72448_b;
            this.field_75521_i.field_72449_c = var4.field_72449_c;
            return true;
         }
      }
   }

   public PathEntity func_75505_d() {
      return this.field_75514_c;
   }

   public void func_75501_e() {
      ++this.field_75510_g;
      if (!this.func_75500_f()) {
         if (this.func_75485_k()) {
            this.func_75508_h();
         }

         if (!this.func_75500_f()) {
            Vec3 var1 = this.field_75514_c.func_75878_a(this.field_75515_a);
            if (var1 != null) {
               this.field_75515_a.func_70605_aq().func_75642_a(var1.field_72450_a, var1.field_72448_b, var1.field_72449_c, this.field_75511_d);
            }
         }
      }
   }

   private void func_75508_h() {
      Vec3 var1 = this.func_75502_i();
      int var2 = this.field_75514_c.func_75874_d();

      for(int var3 = this.field_75514_c.func_75873_e(); var3 < this.field_75514_c.func_75874_d(); ++var3) {
         if (this.field_75514_c.func_75877_a(var3).field_75837_b != (int)var1.field_72448_b) {
            var2 = var3;
            break;
         }
      }

      float var8 = this.field_75515_a.field_70130_N * this.field_75515_a.field_70130_N;

      for(int var4 = this.field_75514_c.func_75873_e(); var4 < var2; ++var4) {
         if (var1.func_72436_e(this.field_75514_c.func_75881_a(this.field_75515_a, var4)) < (double)var8) {
            this.field_75514_c.func_75872_c(var4 + 1);
         }
      }

      int var9 = MathHelper.func_76123_f(this.field_75515_a.field_70130_N);
      int var5 = (int)this.field_75515_a.field_70131_O + 1;
      int var6 = var9;

      for(int var7 = var2 - 1; var7 >= this.field_75514_c.func_75873_e(); --var7) {
         if (this.func_75493_a(var1, this.field_75514_c.func_75881_a(this.field_75515_a, var7), var9, var5, var6)) {
            this.field_75514_c.func_75872_c(var7);
            break;
         }
      }

      if (this.field_75510_g - this.field_75520_h > 100) {
         if (var1.func_72436_e(this.field_75521_i) < 2.25) {
            this.func_75499_g();
         }

         this.field_75520_h = this.field_75510_g;
         this.field_75521_i.field_72450_a = var1.field_72450_a;
         this.field_75521_i.field_72448_b = var1.field_72448_b;
         this.field_75521_i.field_72449_c = var1.field_72449_c;
      }
   }

   public boolean func_75500_f() {
      return this.field_75514_c == null || this.field_75514_c.func_75879_b();
   }

   public void func_75499_g() {
      this.field_75514_c = null;
   }

   private Vec3 func_75502_i() {
      return Vec3.func_72443_a(this.field_75515_a.field_70165_t, (double)this.func_75503_j(), this.field_75515_a.field_70161_v);
   }

   private int func_75503_j() {
      if (this.field_75515_a.func_70090_H() && this.field_75517_m) {
         int var1 = (int)this.field_75515_a.field_70121_D.field_72338_b;
         Block var2 = this.field_75513_b
            .func_147439_a(MathHelper.func_76128_c(this.field_75515_a.field_70165_t), var1, MathHelper.func_76128_c(this.field_75515_a.field_70161_v));
         int var3 = 0;

         while(var2 == Blocks.field_150358_i || var2 == Blocks.field_150355_j) {
            var2 = this.field_75513_b
               .func_147439_a(MathHelper.func_76128_c(this.field_75515_a.field_70165_t), ++var1, MathHelper.func_76128_c(this.field_75515_a.field_70161_v));
            if (++var3 > 16) {
               return (int)this.field_75515_a.field_70121_D.field_72338_b;
            }
         }

         return var1;
      } else {
         return (int)(this.field_75515_a.field_70121_D.field_72338_b + 0.5);
      }
   }

   private boolean func_75485_k() {
      return this.field_75515_a.field_70122_E
         || this.field_75517_m && this.func_75506_l()
         || this.field_75515_a.func_70115_ae() && this.field_75515_a instanceof EntityZombie && this.field_75515_a.field_70154_o instanceof EntityChicken;
   }

   private boolean func_75506_l() {
      return this.field_75515_a.func_70090_H() || this.field_75515_a.func_70058_J();
   }

   private void func_75487_m() {
      if (!this.field_75513_b
         .func_72937_j(
            MathHelper.func_76128_c(this.field_75515_a.field_70165_t),
            (int)(this.field_75515_a.field_70121_D.field_72338_b + 0.5),
            MathHelper.func_76128_c(this.field_75515_a.field_70161_v)
         )) {
         for(int var1 = 0; var1 < this.field_75514_c.func_75874_d(); ++var1) {
            PathPoint var2 = this.field_75514_c.func_75877_a(var1);
            if (this.field_75513_b.func_72937_j(var2.field_75839_a, var2.field_75837_b, var2.field_75838_c)) {
               this.field_75514_c.func_75871_b(var1 - 1);
               return;
            }
         }
      }
   }

   private boolean func_75493_a(Vec3 var1, Vec3 var2, int var3, int var4, int var5) {
      int var6 = MathHelper.func_76128_c(var1.field_72450_a);
      int var7 = MathHelper.func_76128_c(var1.field_72449_c);
      double var8 = var2.field_72450_a - var1.field_72450_a;
      double var10 = var2.field_72449_c - var1.field_72449_c;
      double var12 = var8 * var8 + var10 * var10;
      if (var12 < 1.0E-8) {
         return false;
      } else {
         double var14 = 1.0 / Math.sqrt(var12);
         var8 *= var14;
         var10 *= var14;
         var3 += 2;
         var5 += 2;
         if (!this.func_75483_a(var6, (int)var1.field_72448_b, var7, var3, var4, var5, var1, var8, var10)) {
            return false;
         } else {
            var3 -= 2;
            var5 -= 2;
            double var16 = 1.0 / Math.abs(var8);
            double var18 = 1.0 / Math.abs(var10);
            double var20 = (double)(var6 * 1) - var1.field_72450_a;
            double var22 = (double)(var7 * 1) - var1.field_72449_c;
            if (var8 >= 0.0) {
               ++var20;
            }

            if (var10 >= 0.0) {
               ++var22;
            }

            var20 /= var8;
            var22 /= var10;
            int var24 = var8 < 0.0 ? -1 : 1;
            int var25 = var10 < 0.0 ? -1 : 1;
            int var26 = MathHelper.func_76128_c(var2.field_72450_a);
            int var27 = MathHelper.func_76128_c(var2.field_72449_c);
            int var28 = var26 - var6;
            int var29 = var27 - var7;

            while(var28 * var24 > 0 || var29 * var25 > 0) {
               if (var20 < var22) {
                  var20 += var16;
                  var6 += var24;
                  var28 = var26 - var6;
               } else {
                  var22 += var18;
                  var7 += var25;
                  var29 = var27 - var7;
               }

               if (!this.func_75483_a(var6, (int)var1.field_72448_b, var7, var3, var4, var5, var1, var8, var10)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private boolean func_75483_a(int var1, int var2, int var3, int var4, int var5, int var6, Vec3 var7, double var8, double var10) {
      int var12 = var1 - var4 / 2;
      int var13 = var3 - var6 / 2;
      if (!this.func_75496_b(var12, var2, var13, var4, var5, var6, var7, var8, var10)) {
         return false;
      } else {
         for(int var14 = var12; var14 < var12 + var4; ++var14) {
            for(int var15 = var13; var15 < var13 + var6; ++var15) {
               double var16 = (double)var14 + 0.5 - var7.field_72450_a;
               double var18 = (double)var15 + 0.5 - var7.field_72449_c;
               if (!(var16 * var8 + var18 * var10 < 0.0)) {
                  Block var20 = this.field_75513_b.func_147439_a(var14, var2 - 1, var15);
                  Material var21 = var20.func_149688_o();
                  if (var21 == Material.field_151579_a) {
                     return false;
                  }

                  if (var21 == Material.field_151586_h && !this.field_75515_a.func_70090_H()) {
                     return false;
                  }

                  if (var21 == Material.field_151587_i) {
                     return false;
                  }
               }
            }
         }

         return true;
      }
   }

   private boolean func_75496_b(int var1, int var2, int var3, int var4, int var5, int var6, Vec3 var7, double var8, double var10) {
      for(int var12 = var1; var12 < var1 + var4; ++var12) {
         for(int var13 = var2; var13 < var2 + var5; ++var13) {
            for(int var14 = var3; var14 < var3 + var6; ++var14) {
               double var15 = (double)var12 + 0.5 - var7.field_72450_a;
               double var17 = (double)var14 + 0.5 - var7.field_72449_c;
               if (!(var15 * var8 + var17 * var10 < 0.0)) {
                  Block var19 = this.field_75513_b.func_147439_a(var12, var13, var14);
                  if (!var19.func_149655_b(this.field_75513_b, var12, var13, var14)) {
                     return false;
                  }
               }
            }
         }
      }

      return true;
   }
}
