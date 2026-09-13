package net.minecraft.entity.boss;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityDragon extends EntityLiving implements IBossDisplayData, IEntityMultiPart, IMob {
   public double field_70980_b;
   public double field_70981_c;
   public double field_70978_d;
   public double[][] field_70979_e = new double[64][3];
   public int field_70976_f = -1;
   public EntityDragonPart[] field_70977_g;
   public EntityDragonPart field_70986_h;
   public EntityDragonPart field_70987_i;
   public EntityDragonPart field_70985_j;
   public EntityDragonPart field_70984_by;
   public EntityDragonPart field_70982_bz;
   public EntityDragonPart field_70983_bA;
   public EntityDragonPart field_70990_bB;
   public float field_70991_bC;
   public float field_70988_bD;
   public boolean field_70989_bE;
   public boolean field_70994_bF;
   private Entity field_70993_bI;
   public int field_70995_bG;
   public EntityEnderCrystal field_70992_bH;

   public EntityDragon(World var1) {
      super(var1);
      this.field_70977_g = new EntityDragonPart[]{
         this.field_70986_h = new EntityDragonPart(this, "head", 6.0F, 6.0F),
         this.field_70987_i = new EntityDragonPart(this, "body", 8.0F, 8.0F),
         this.field_70985_j = new EntityDragonPart(this, "tail", 4.0F, 4.0F),
         this.field_70984_by = new EntityDragonPart(this, "tail", 4.0F, 4.0F),
         this.field_70982_bz = new EntityDragonPart(this, "tail", 4.0F, 4.0F),
         this.field_70983_bA = new EntityDragonPart(this, "wing", 4.0F, 4.0F),
         this.field_70990_bB = new EntityDragonPart(this, "wing", 4.0F, 4.0F)
      };
      this.func_70606_j(this.func_110138_aP());
      this.func_70105_a(16.0F, 8.0F);
      this.field_70145_X = true;
      this.field_70178_ae = true;
      this.field_70981_c = 100.0;
      this.field_70158_ak = true;
   }

   @Override
   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(200.0);
   }

   @Override
   protected void func_70088_a() {
      super.func_70088_a();
   }

   public double[] func_70974_a(int var1, float var2) {
      if (this.func_110143_aJ() <= 0.0F) {
         var2 = 0.0F;
      }

      var2 = 1.0F - var2;
      int var3 = this.field_70976_f - var1 * 1 & 63;
      int var4 = this.field_70976_f - var1 * 1 - 1 & 63;
      double[] var5 = new double[3];
      double var6 = this.field_70979_e[var3][0];
      double var8 = MathHelper.func_76138_g(this.field_70979_e[var4][0] - var6);
      var5[0] = var6 + var8 * (double)var2;
      var6 = this.field_70979_e[var3][1];
      var8 = this.field_70979_e[var4][1] - var6;
      var5[1] = var6 + var8 * (double)var2;
      var5[2] = this.field_70979_e[var3][2] + (this.field_70979_e[var4][2] - this.field_70979_e[var3][2]) * (double)var2;
      return var5;
   }

   @Override
   public void func_70636_d() {
      if (this.field_70170_p.field_72995_K) {
         float var1 = MathHelper.func_76134_b(this.field_70988_bD * 3.1415927F * 2.0F);
         float var2 = MathHelper.func_76134_b(this.field_70991_bC * 3.1415927F * 2.0F);
         if (var2 <= -0.3F && var1 >= -0.3F) {
            this.field_70170_p
               .func_72980_b(
                  this.field_70165_t,
                  this.field_70163_u,
                  this.field_70161_v,
                  "mob.enderdragon.wings",
                  5.0F,
                  0.8F + this.field_70146_Z.nextFloat() * 0.3F,
                  false
               );
         }
      }

      this.field_70991_bC = this.field_70988_bD;
      if (this.func_110143_aJ() <= 0.0F) {
         float var27 = (this.field_70146_Z.nextFloat() - 0.5F) * 8.0F;
         float var32 = (this.field_70146_Z.nextFloat() - 0.5F) * 4.0F;
         float var33 = (this.field_70146_Z.nextFloat() - 0.5F) * 8.0F;
         this.field_70170_p
            .func_72869_a(
               "largeexplode", this.field_70165_t + (double)var27, this.field_70163_u + 2.0 + (double)var32, this.field_70161_v + (double)var33, 0.0, 0.0, 0.0
            );
      } else {
         this.func_70969_j();
         float var25 = 0.2F / (MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y) * 10.0F + 1.0F);
         var25 *= (float)Math.pow(2.0, this.field_70181_x);
         if (this.field_70994_bF) {
            this.field_70988_bD += var25 * 0.5F;
         } else {
            this.field_70988_bD += var25;
         }

         this.field_70177_z = MathHelper.func_76142_g(this.field_70177_z);
         if (this.field_70976_f < 0) {
            for(int var28 = 0; var28 < this.field_70979_e.length; ++var28) {
               this.field_70979_e[var28][0] = (double)this.field_70177_z;
               this.field_70979_e[var28][1] = this.field_70163_u;
            }
         }

         if (++this.field_70976_f == this.field_70979_e.length) {
            this.field_70976_f = 0;
         }

         this.field_70979_e[this.field_70976_f][0] = (double)this.field_70177_z;
         this.field_70979_e[this.field_70976_f][1] = this.field_70163_u;
         if (this.field_70170_p.field_72995_K) {
            if (this.field_70716_bi > 0) {
               double var29 = this.field_70165_t + (this.field_70709_bj - this.field_70165_t) / (double)this.field_70716_bi;
               double var4 = this.field_70163_u + (this.field_70710_bk - this.field_70163_u) / (double)this.field_70716_bi;
               double var6 = this.field_70161_v + (this.field_110152_bk - this.field_70161_v) / (double)this.field_70716_bi;
               double var8 = MathHelper.func_76138_g(this.field_70712_bm - (double)this.field_70177_z);
               this.field_70177_z = (float)((double)this.field_70177_z + var8 / (double)this.field_70716_bi);
               this.field_70125_A = (float)((double)this.field_70125_A + (this.field_70705_bn - (double)this.field_70125_A) / (double)this.field_70716_bi);
               --this.field_70716_bi;
               this.func_70107_b(var29, var4, var6);
               this.func_70101_b(this.field_70177_z, this.field_70125_A);
            }
         } else {
            double var30 = this.field_70980_b - this.field_70165_t;
            double var34 = this.field_70981_c - this.field_70163_u;
            double var37 = this.field_70978_d - this.field_70161_v;
            double var39 = var30 * var30 + var34 * var34 + var37 * var37;
            if (this.field_70993_bI != null) {
               this.field_70980_b = this.field_70993_bI.field_70165_t;
               this.field_70978_d = this.field_70993_bI.field_70161_v;
               double var10 = this.field_70980_b - this.field_70165_t;
               double var12 = this.field_70978_d - this.field_70161_v;
               double var14 = Math.sqrt(var10 * var10 + var12 * var12);
               double var16 = 0.4000000059604645 + var14 / 80.0 - 1.0;
               if (var16 > 10.0) {
                  var16 = 10.0;
               }

               this.field_70981_c = this.field_70993_bI.field_70121_D.field_72338_b + var16;
            } else {
               this.field_70980_b += this.field_70146_Z.nextGaussian() * 2.0;
               this.field_70978_d += this.field_70146_Z.nextGaussian() * 2.0;
            }

            if (this.field_70989_bE || var39 < 100.0 || var39 > 22500.0 || this.field_70123_F || this.field_70124_G) {
               this.func_70967_k();
            }

            var34 /= (double)MathHelper.func_76133_a(var30 * var30 + var37 * var37);
            float var42 = 0.6F;
            if (var34 < (double)(-var42)) {
               var34 = (double)(-var42);
            }

            if (var34 > (double)var42) {
               var34 = (double)var42;
            }

            this.field_70181_x += var34 * 0.10000000149011612;
            this.field_70177_z = MathHelper.func_76142_g(this.field_70177_z);
            double var11 = 180.0 - Math.atan2(var30, var37) * 180.0 / 3.1415927410125732;
            double var13 = MathHelper.func_76138_g(var11 - (double)this.field_70177_z);
            if (var13 > 50.0) {
               var13 = 50.0;
            }

            if (var13 < -50.0) {
               var13 = -50.0;
            }

            Vec3 var15 = Vec3.func_72443_a(
                  this.field_70980_b - this.field_70165_t, this.field_70981_c - this.field_70163_u, this.field_70978_d - this.field_70161_v
               )
               .func_72432_b();
            Vec3 var51 = Vec3.func_72443_a(
                  (double)MathHelper.func_76126_a(this.field_70177_z * 3.1415927F / 180.0F),
                  this.field_70181_x,
                  (double)(-MathHelper.func_76134_b(this.field_70177_z * 3.1415927F / 180.0F))
               )
               .func_72432_b();
            float var17 = (float)(var51.func_72430_b(var15) + 0.5) / 1.5F;
            if (var17 < 0.0F) {
               var17 = 0.0F;
            }

            this.field_70704_bt *= 0.8F;
            float var18 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y) * 1.0F + 1.0F;
            double var19 = Math.sqrt(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y) * 1.0 + 1.0;
            if (var19 > 40.0) {
               var19 = 40.0;
            }

            this.field_70704_bt = (float)((double)this.field_70704_bt + var13 * (0.699999988079071 / var19 / (double)var18));
            this.field_70177_z += this.field_70704_bt * 0.1F;
            float var21 = (float)(2.0 / (var19 + 1.0));
            float var22 = 0.06F;
            this.func_70060_a(0.0F, -1.0F, var22 * (var17 * var21 + (1.0F - var21)));
            if (this.field_70994_bF) {
               this.func_70091_d(this.field_70159_w * 0.800000011920929, this.field_70181_x * 0.800000011920929, this.field_70179_y * 0.800000011920929);
            } else {
               this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
            }

            Vec3 var23 = Vec3.func_72443_a(this.field_70159_w, this.field_70181_x, this.field_70179_y).func_72432_b();
            float var24 = (float)(var23.func_72430_b(var51) + 1.0) / 2.0F;
            var24 = 0.8F + 0.15F * var24;
            this.field_70159_w *= (double)var24;
            this.field_70179_y *= (double)var24;
            this.field_70181_x *= 0.9100000262260437;
         }

         this.field_70761_aq = this.field_70177_z;
         this.field_70986_h.field_70130_N = this.field_70986_h.field_70131_O = 3.0F;
         this.field_70985_j.field_70130_N = this.field_70985_j.field_70131_O = 2.0F;
         this.field_70984_by.field_70130_N = this.field_70984_by.field_70131_O = 2.0F;
         this.field_70982_bz.field_70130_N = this.field_70982_bz.field_70131_O = 2.0F;
         this.field_70987_i.field_70131_O = 3.0F;
         this.field_70987_i.field_70130_N = 5.0F;
         this.field_70983_bA.field_70131_O = 2.0F;
         this.field_70983_bA.field_70130_N = 4.0F;
         this.field_70990_bB.field_70131_O = 3.0F;
         this.field_70990_bB.field_70130_N = 4.0F;
         float var31 = (float)(this.func_70974_a(5, 1.0F)[1] - this.func_70974_a(10, 1.0F)[1]) * 10.0F / 180.0F * 3.1415927F;
         float var3 = MathHelper.func_76134_b(var31);
         float var36 = -MathHelper.func_76126_a(var31);
         float var5 = this.field_70177_z * 3.1415927F / 180.0F;
         float var38 = MathHelper.func_76126_a(var5);
         float var7 = MathHelper.func_76134_b(var5);
         this.field_70987_i.func_70071_h_();
         this.field_70987_i
            .func_70012_b(this.field_70165_t + (double)(var38 * 0.5F), this.field_70163_u, this.field_70161_v - (double)(var7 * 0.5F), 0.0F, 0.0F);
         this.field_70983_bA.func_70071_h_();
         this.field_70983_bA
            .func_70012_b(this.field_70165_t + (double)(var7 * 4.5F), this.field_70163_u + 2.0, this.field_70161_v + (double)(var38 * 4.5F), 0.0F, 0.0F);
         this.field_70990_bB.func_70071_h_();
         this.field_70990_bB
            .func_70012_b(this.field_70165_t - (double)(var7 * 4.5F), this.field_70163_u + 2.0, this.field_70161_v - (double)(var38 * 4.5F), 0.0F, 0.0F);
         if (!this.field_70170_p.field_72995_K && this.field_70737_aN == 0) {
            this.func_70970_a(
               this.field_70170_p.func_72839_b(this, this.field_70983_bA.field_70121_D.func_72314_b(4.0, 2.0, 4.0).func_72317_d(0.0, -2.0, 0.0))
            );
            this.func_70970_a(
               this.field_70170_p.func_72839_b(this, this.field_70990_bB.field_70121_D.func_72314_b(4.0, 2.0, 4.0).func_72317_d(0.0, -2.0, 0.0))
            );
            this.func_70971_b(this.field_70170_p.func_72839_b(this, this.field_70986_h.field_70121_D.func_72314_b(1.0, 1.0, 1.0)));
         }

         double[] var40 = this.func_70974_a(5, 1.0F);
         double[] var9 = this.func_70974_a(0, 1.0F);
         float var43 = MathHelper.func_76126_a(this.field_70177_z * 3.1415927F / 180.0F - this.field_70704_bt * 0.01F);
         float var45 = MathHelper.func_76134_b(this.field_70177_z * 3.1415927F / 180.0F - this.field_70704_bt * 0.01F);
         this.field_70986_h.func_70071_h_();
         this.field_70986_h
            .func_70012_b(
               this.field_70165_t + (double)(var43 * 5.5F * var3),
               this.field_70163_u + (var9[1] - var40[1]) * 1.0 + (double)(var36 * 5.5F),
               this.field_70161_v - (double)(var45 * 5.5F * var3),
               0.0F,
               0.0F
            );

         for(int var41 = 0; var41 < 3; ++var41) {
            EntityDragonPart var44 = null;
            if (var41 == 0) {
               var44 = this.field_70985_j;
            }

            if (var41 == 1) {
               var44 = this.field_70984_by;
            }

            if (var41 == 2) {
               var44 = this.field_70982_bz;
            }

            double[] var46 = this.func_70974_a(12 + var41 * 2, 1.0F);
            float var47 = this.field_70177_z * 3.1415927F / 180.0F + this.func_70973_b(var46[0] - var40[0]) * 3.1415927F / 180.0F * 1.0F;
            float var48 = MathHelper.func_76126_a(var47);
            float var49 = MathHelper.func_76134_b(var47);
            float var50 = 1.5F;
            float var52 = (float)(var41 + 1) * 2.0F;
            var44.func_70071_h_();
            var44.func_70012_b(
               this.field_70165_t - (double)((var38 * var50 + var48 * var52) * var3),
               this.field_70163_u + (var46[1] - var40[1]) * 1.0 - (double)((var52 + var50) * var36) + 1.5,
               this.field_70161_v + (double)((var7 * var50 + var49 * var52) * var3),
               0.0F,
               0.0F
            );
         }

         if (!this.field_70170_p.field_72995_K) {
            this.field_70994_bF = this.func_70972_a(this.field_70986_h.field_70121_D) | this.func_70972_a(this.field_70987_i.field_70121_D);
         }
      }
   }

   private void func_70969_j() {
      if (this.field_70992_bH != null) {
         if (this.field_70992_bH.field_70128_L) {
            if (!this.field_70170_p.field_72995_K) {
               this.func_70965_a(this.field_70986_h, DamageSource.func_94539_a(null), 10.0F);
            }

            this.field_70992_bH = null;
         } else if (this.field_70173_aa % 10 == 0 && this.func_110143_aJ() < this.func_110138_aP()) {
            this.func_70606_j(this.func_110143_aJ() + 1.0F);
         }
      }

      if (this.field_70146_Z.nextInt(10) == 0) {
         float var1 = 32.0F;
         List var2 = this.field_70170_p.func_72872_a(EntityEnderCrystal.class, this.field_70121_D.func_72314_b((double)var1, (double)var1, (double)var1));
         EntityEnderCrystal var3 = null;
         double var4 = 1.7976931348623157E308;

         for(EntityEnderCrystal var7 : var2) {
            double var8 = var7.func_70068_e(this);
            if (var8 < var4) {
               var4 = var8;
               var3 = var7;
            }
         }

         this.field_70992_bH = var3;
      }
   }

   private void func_70970_a(List var1) {
      double var2 = (this.field_70987_i.field_70121_D.field_72340_a + this.field_70987_i.field_70121_D.field_72336_d) / 2.0;
      double var4 = (this.field_70987_i.field_70121_D.field_72339_c + this.field_70987_i.field_70121_D.field_72334_f) / 2.0;

      for(Entity var7 : var1) {
         if (var7 instanceof EntityLivingBase) {
            double var8 = var7.field_70165_t - var2;
            double var10 = var7.field_70161_v - var4;
            double var12 = var8 * var8 + var10 * var10;
            var7.func_70024_g(var8 / var12 * 4.0, 0.20000000298023224, var10 / var12 * 4.0);
         }
      }
   }

   private void func_70971_b(List var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         Entity var3 = (Entity)var1.get(var2);
         if (var3 instanceof EntityLivingBase) {
            var3.func_70097_a(DamageSource.func_76358_a(this), 10.0F);
         }
      }
   }

   private void func_70967_k() {
      this.field_70989_bE = false;
      if (this.field_70146_Z.nextInt(2) == 0 && !this.field_70170_p.field_73010_i.isEmpty()) {
         this.field_70993_bI = (Entity)this.field_70170_p.field_73010_i.get(this.field_70146_Z.nextInt(this.field_70170_p.field_73010_i.size()));
      } else {
         boolean var1 = false;

         do {
            this.field_70980_b = 0.0;
            this.field_70981_c = (double)(70.0F + this.field_70146_Z.nextFloat() * 50.0F);
            this.field_70978_d = 0.0;
            this.field_70980_b += (double)(this.field_70146_Z.nextFloat() * 120.0F - 60.0F);
            this.field_70978_d += (double)(this.field_70146_Z.nextFloat() * 120.0F - 60.0F);
            double var2 = this.field_70165_t - this.field_70980_b;
            double var4 = this.field_70163_u - this.field_70981_c;
            double var6 = this.field_70161_v - this.field_70978_d;
            var1 = var2 * var2 + var4 * var4 + var6 * var6 > 100.0;
         } while(!var1);

         this.field_70993_bI = null;
      }
   }

   private float func_70973_b(double var1) {
      return (float)MathHelper.func_76138_g(var1);
   }

   private boolean func_70972_a(AxisAlignedBB var1) {
      int var2 = MathHelper.func_76128_c(var1.field_72340_a);
      int var3 = MathHelper.func_76128_c(var1.field_72338_b);
      int var4 = MathHelper.func_76128_c(var1.field_72339_c);
      int var5 = MathHelper.func_76128_c(var1.field_72336_d);
      int var6 = MathHelper.func_76128_c(var1.field_72337_e);
      int var7 = MathHelper.func_76128_c(var1.field_72334_f);
      boolean var8 = false;
      boolean var9 = false;

      for(int var10 = var2; var10 <= var5; ++var10) {
         for(int var11 = var3; var11 <= var6; ++var11) {
            for(int var12 = var4; var12 <= var7; ++var12) {
               Block var13 = this.field_70170_p.func_147439_a(var10, var11, var12);
               if (var13.func_149688_o() != Material.field_151579_a) {
                  if (var13 != Blocks.field_150343_Z
                     && var13 != Blocks.field_150377_bs
                     && var13 != Blocks.field_150357_h
                     && this.field_70170_p.func_82736_K().func_82766_b("mobGriefing")) {
                     var9 = this.field_70170_p.func_147468_f(var10, var11, var12) || var9;
                  } else {
                     var8 = true;
                  }
               }
            }
         }
      }

      if (var9) {
         double var16 = var1.field_72340_a + (var1.field_72336_d - var1.field_72340_a) * (double)this.field_70146_Z.nextFloat();
         double var17 = var1.field_72338_b + (var1.field_72337_e - var1.field_72338_b) * (double)this.field_70146_Z.nextFloat();
         double var14 = var1.field_72339_c + (var1.field_72334_f - var1.field_72339_c) * (double)this.field_70146_Z.nextFloat();
         this.field_70170_p.func_72869_a("largeexplode", var16, var17, var14, 0.0, 0.0, 0.0);
      }

      return var8;
   }

   @Override
   public boolean func_70965_a(EntityDragonPart var1, DamageSource var2, float var3) {
      if (var1 != this.field_70986_h) {
         var3 = var3 / 4.0F + 1.0F;
      }

      float var4 = this.field_70177_z * 3.1415927F / 180.0F;
      float var5 = MathHelper.func_76126_a(var4);
      float var6 = MathHelper.func_76134_b(var4);
      this.field_70980_b = this.field_70165_t + (double)(var5 * 5.0F) + (double)((this.field_70146_Z.nextFloat() - 0.5F) * 2.0F);
      this.field_70981_c = this.field_70163_u + (double)(this.field_70146_Z.nextFloat() * 3.0F) + 1.0;
      this.field_70978_d = this.field_70161_v - (double)(var6 * 5.0F) + (double)((this.field_70146_Z.nextFloat() - 0.5F) * 2.0F);
      this.field_70993_bI = null;
      if (var2.func_76346_g() instanceof EntityPlayer || var2.func_94541_c()) {
         this.func_82195_e(var2, var3);
      }

      return true;
   }

   @Override
   public boolean func_70097_a(DamageSource var1, float var2) {
      return false;
   }

   protected boolean func_82195_e(DamageSource var1, float var2) {
      return super.func_70097_a(var1, var2);
   }

   @Override
   protected void func_70609_aI() {
      ++this.field_70995_bG;
      if (this.field_70995_bG >= 180 && this.field_70995_bG <= 200) {
         float var1 = (this.field_70146_Z.nextFloat() - 0.5F) * 8.0F;
         float var2 = (this.field_70146_Z.nextFloat() - 0.5F) * 4.0F;
         float var3 = (this.field_70146_Z.nextFloat() - 0.5F) * 8.0F;
         this.field_70170_p
            .func_72869_a(
               "hugeexplosion", this.field_70165_t + (double)var1, this.field_70163_u + 2.0 + (double)var2, this.field_70161_v + (double)var3, 0.0, 0.0, 0.0
            );
      }

      if (!this.field_70170_p.field_72995_K) {
         if (this.field_70995_bG > 150 && this.field_70995_bG % 5 == 0) {
            int var4 = 1000;

            while(var4 > 0) {
               int var6 = EntityXPOrb.func_70527_a(var4);
               var4 -= var6;
               this.field_70170_p.func_72838_d(new EntityXPOrb(this.field_70170_p, this.field_70165_t, this.field_70163_u, this.field_70161_v, var6));
            }
         }

         if (this.field_70995_bG == 1) {
            this.field_70170_p.func_82739_e(1018, (int)this.field_70165_t, (int)this.field_70163_u, (int)this.field_70161_v, 0);
         }
      }

      this.func_70091_d(0.0, 0.10000000149011612, 0.0);
      this.field_70761_aq = this.field_70177_z += 20.0F;
      if (this.field_70995_bG == 200 && !this.field_70170_p.field_72995_K) {
         int var5 = 2000;

         while(var5 > 0) {
            int var7 = EntityXPOrb.func_70527_a(var5);
            var5 -= var7;
            this.field_70170_p.func_72838_d(new EntityXPOrb(this.field_70170_p, this.field_70165_t, this.field_70163_u, this.field_70161_v, var7));
         }

         this.func_70975_a(MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70161_v));
         this.func_70106_y();
      }
   }

   private void func_70975_a(int var1, int var2) {
      byte var3 = 64;
      BlockEndPortal.field_149948_a = true;
      byte var4 = 4;

      for(int var5 = var3 - 1; var5 <= var3 + 32; ++var5) {
         for(int var6 = var1 - var4; var6 <= var1 + var4; ++var6) {
            for(int var7 = var2 - var4; var7 <= var2 + var4; ++var7) {
               double var8 = (double)(var6 - var1);
               double var10 = (double)(var7 - var2);
               double var12 = var8 * var8 + var10 * var10;
               if (var12 <= ((double)var4 - 0.5) * ((double)var4 - 0.5)) {
                  if (var5 < var3) {
                     if (var12 <= ((double)(var4 - 1) - 0.5) * ((double)(var4 - 1) - 0.5)) {
                        this.field_70170_p.func_147449_b(var6, var5, var7, Blocks.field_150357_h);
                     }
                  } else if (var5 > var3) {
                     this.field_70170_p.func_147449_b(var6, var5, var7, Blocks.field_150350_a);
                  } else if (var12 > ((double)(var4 - 1) - 0.5) * ((double)(var4 - 1) - 0.5)) {
                     this.field_70170_p.func_147449_b(var6, var5, var7, Blocks.field_150357_h);
                  } else {
                     this.field_70170_p.func_147449_b(var6, var5, var7, Blocks.field_150384_bq);
                  }
               }
            }
         }
      }

      this.field_70170_p.func_147449_b(var1, var3 + 0, var2, Blocks.field_150357_h);
      this.field_70170_p.func_147449_b(var1, var3 + 1, var2, Blocks.field_150357_h);
      this.field_70170_p.func_147449_b(var1, var3 + 2, var2, Blocks.field_150357_h);
      this.field_70170_p.func_147449_b(var1 - 1, var3 + 2, var2, Blocks.field_150478_aa);
      this.field_70170_p.func_147449_b(var1 + 1, var3 + 2, var2, Blocks.field_150478_aa);
      this.field_70170_p.func_147449_b(var1, var3 + 2, var2 - 1, Blocks.field_150478_aa);
      this.field_70170_p.func_147449_b(var1, var3 + 2, var2 + 1, Blocks.field_150478_aa);
      this.field_70170_p.func_147449_b(var1, var3 + 3, var2, Blocks.field_150357_h);
      this.field_70170_p.func_147449_b(var1, var3 + 4, var2, Blocks.field_150380_bt);
      BlockEndPortal.field_149948_a = false;
   }

   @Override
   protected void func_70623_bb() {
   }

   @Override
   public Entity[] func_70021_al() {
      return this.field_70977_g;
   }

   @Override
   public boolean func_70067_L() {
      return false;
   }

   @Override
   public World func_82194_d() {
      return this.field_70170_p;
   }

   @Override
   protected String func_70639_aQ() {
      return "mob.enderdragon.growl";
   }

   @Override
   protected String func_70621_aR() {
      return "mob.enderdragon.hit";
   }

   @Override
   protected float func_70599_aP() {
      return 5.0F;
   }
}
