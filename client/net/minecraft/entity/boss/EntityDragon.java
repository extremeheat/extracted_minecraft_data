package net.minecraft.entity.boss;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
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
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
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
      this.field_70977_g = new EntityDragonPart[]{this.field_70986_h = new EntityDragonPart(this, "head", 6.0F, 6.0F), this.field_70987_i = new EntityDragonPart(this, "body", 8.0F, 8.0F), this.field_70985_j = new EntityDragonPart(this, "tail", 4.0F, 4.0F), this.field_70984_by = new EntityDragonPart(this, "tail", 4.0F, 4.0F), this.field_70982_bz = new EntityDragonPart(this, "tail", 4.0F, 4.0F), this.field_70983_bA = new EntityDragonPart(this, "wing", 4.0F, 4.0F), this.field_70990_bB = new EntityDragonPart(this, "wing", 4.0F, 4.0F)};
      this.func_70606_j(this.func_110138_aP());
      this.func_70105_a(16.0F, 8.0F);
      this.field_70145_X = true;
      this.field_70178_ae = true;
      this.field_70981_c = 100.0D;
      this.field_70158_ak = true;
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(200.0D);
   }

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

   public void func_70636_d() {
      float var1;
      float var2;
      if (this.field_70170_p.field_72995_K) {
         var1 = MathHelper.func_76134_b(this.field_70988_bD * 3.1415927F * 2.0F);
         var2 = MathHelper.func_76134_b(this.field_70991_bC * 3.1415927F * 2.0F);
         if (var2 <= -0.3F && var1 >= -0.3F && !this.func_174814_R()) {
            this.field_70170_p.func_72980_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, "mob.enderdragon.wings", 5.0F, 0.8F + this.field_70146_Z.nextFloat() * 0.3F, false);
         }
      }

      this.field_70991_bC = this.field_70988_bD;
      float var3;
      if (this.func_110143_aJ() <= 0.0F) {
         var1 = (this.field_70146_Z.nextFloat() - 0.5F) * 8.0F;
         var2 = (this.field_70146_Z.nextFloat() - 0.5F) * 4.0F;
         var3 = (this.field_70146_Z.nextFloat() - 0.5F) * 8.0F;
         this.field_70170_p.func_175688_a(EnumParticleTypes.EXPLOSION_LARGE, this.field_70165_t + (double)var1, this.field_70163_u + 2.0D + (double)var2, this.field_70161_v + (double)var3, 0.0D, 0.0D, 0.0D);
      } else {
         this.func_70969_j();
         var1 = 0.2F / (MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y) * 10.0F + 1.0F);
         var1 *= (float)Math.pow(2.0D, this.field_70181_x);
         if (this.field_70994_bF) {
            this.field_70988_bD += var1 * 0.5F;
         } else {
            this.field_70988_bD += var1;
         }

         this.field_70177_z = MathHelper.func_76142_g(this.field_70177_z);
         if (this.func_175446_cd()) {
            this.field_70988_bD = 0.5F;
         } else {
            if (this.field_70976_f < 0) {
               for(int var27 = 0; var27 < this.field_70979_e.length; ++var27) {
                  this.field_70979_e[var27][0] = (double)this.field_70177_z;
                  this.field_70979_e[var27][1] = this.field_70163_u;
               }
            }

            if (++this.field_70976_f == this.field_70979_e.length) {
               this.field_70976_f = 0;
            }

            this.field_70979_e[this.field_70976_f][0] = (double)this.field_70177_z;
            this.field_70979_e[this.field_70976_f][1] = this.field_70163_u;
            double var4;
            double var6;
            double var8;
            double var28;
            float var33;
            if (this.field_70170_p.field_72995_K) {
               if (this.field_70716_bi > 0) {
                  var28 = this.field_70165_t + (this.field_70709_bj - this.field_70165_t) / (double)this.field_70716_bi;
                  var4 = this.field_70163_u + (this.field_70710_bk - this.field_70163_u) / (double)this.field_70716_bi;
                  var6 = this.field_70161_v + (this.field_110152_bk - this.field_70161_v) / (double)this.field_70716_bi;
                  var8 = MathHelper.func_76138_g(this.field_70712_bm - (double)this.field_70177_z);
                  this.field_70177_z = (float)((double)this.field_70177_z + var8 / (double)this.field_70716_bi);
                  this.field_70125_A = (float)((double)this.field_70125_A + (this.field_70705_bn - (double)this.field_70125_A) / (double)this.field_70716_bi);
                  --this.field_70716_bi;
                  this.func_70107_b(var28, var4, var6);
                  this.func_70101_b(this.field_70177_z, this.field_70125_A);
               }
            } else {
               var28 = this.field_70980_b - this.field_70165_t;
               var4 = this.field_70981_c - this.field_70163_u;
               var6 = this.field_70978_d - this.field_70161_v;
               var8 = var28 * var28 + var4 * var4 + var6 * var6;
               double var16;
               if (this.field_70993_bI != null) {
                  this.field_70980_b = this.field_70993_bI.field_70165_t;
                  this.field_70978_d = this.field_70993_bI.field_70161_v;
                  double var10 = this.field_70980_b - this.field_70165_t;
                  double var12 = this.field_70978_d - this.field_70161_v;
                  double var14 = Math.sqrt(var10 * var10 + var12 * var12);
                  var16 = 0.4000000059604645D + var14 / 80.0D - 1.0D;
                  if (var16 > 10.0D) {
                     var16 = 10.0D;
                  }

                  this.field_70981_c = this.field_70993_bI.func_174813_aQ().field_72338_b + var16;
               } else {
                  this.field_70980_b += this.field_70146_Z.nextGaussian() * 2.0D;
                  this.field_70978_d += this.field_70146_Z.nextGaussian() * 2.0D;
               }

               if (this.field_70989_bE || var8 < 100.0D || var8 > 22500.0D || this.field_70123_F || this.field_70124_G) {
                  this.func_70967_k();
               }

               var4 /= (double)MathHelper.func_76133_a(var28 * var28 + var6 * var6);
               var33 = 0.6F;
               var4 = MathHelper.func_151237_a(var4, (double)(-var33), (double)var33);
               this.field_70181_x += var4 * 0.10000000149011612D;
               this.field_70177_z = MathHelper.func_76142_g(this.field_70177_z);
               double var11 = 180.0D - MathHelper.func_181159_b(var28, var6) * 180.0D / 3.1415927410125732D;
               double var13 = MathHelper.func_76138_g(var11 - (double)this.field_70177_z);
               if (var13 > 50.0D) {
                  var13 = 50.0D;
               }

               if (var13 < -50.0D) {
                  var13 = -50.0D;
               }

               Vec3 var15 = (new Vec3(this.field_70980_b - this.field_70165_t, this.field_70981_c - this.field_70163_u, this.field_70978_d - this.field_70161_v)).func_72432_b();
               var16 = (double)(-MathHelper.func_76134_b(this.field_70177_z * 3.1415927F / 180.0F));
               Vec3 var18 = (new Vec3((double)MathHelper.func_76126_a(this.field_70177_z * 3.1415927F / 180.0F), this.field_70181_x, var16)).func_72432_b();
               float var19 = ((float)var18.func_72430_b(var15) + 0.5F) / 1.5F;
               if (var19 < 0.0F) {
                  var19 = 0.0F;
               }

               this.field_70704_bt *= 0.8F;
               float var20 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y) * 1.0F + 1.0F;
               double var21 = Math.sqrt(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y) * 1.0D + 1.0D;
               if (var21 > 40.0D) {
                  var21 = 40.0D;
               }

               this.field_70704_bt = (float)((double)this.field_70704_bt + var13 * (0.699999988079071D / var21 / (double)var20));
               this.field_70177_z += this.field_70704_bt * 0.1F;
               float var23 = (float)(2.0D / (var21 + 1.0D));
               float var24 = 0.06F;
               this.func_70060_a(0.0F, -1.0F, var24 * (var19 * var23 + (1.0F - var23)));
               if (this.field_70994_bF) {
                  this.func_70091_d(this.field_70159_w * 0.800000011920929D, this.field_70181_x * 0.800000011920929D, this.field_70179_y * 0.800000011920929D);
               } else {
                  this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
               }

               Vec3 var25 = (new Vec3(this.field_70159_w, this.field_70181_x, this.field_70179_y)).func_72432_b();
               float var26 = ((float)var25.func_72430_b(var18) + 1.0F) / 2.0F;
               var26 = 0.8F + 0.15F * var26;
               this.field_70159_w *= (double)var26;
               this.field_70179_y *= (double)var26;
               this.field_70181_x *= 0.9100000262260437D;
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
            var2 = (float)(this.func_70974_a(5, 1.0F)[1] - this.func_70974_a(10, 1.0F)[1]) * 10.0F / 180.0F * 3.1415927F;
            var3 = MathHelper.func_76134_b(var2);
            float var29 = -MathHelper.func_76126_a(var2);
            float var5 = this.field_70177_z * 3.1415927F / 180.0F;
            float var30 = MathHelper.func_76126_a(var5);
            float var7 = MathHelper.func_76134_b(var5);
            this.field_70987_i.func_70071_h_();
            this.field_70987_i.func_70012_b(this.field_70165_t + (double)(var30 * 0.5F), this.field_70163_u, this.field_70161_v - (double)(var7 * 0.5F), 0.0F, 0.0F);
            this.field_70983_bA.func_70071_h_();
            this.field_70983_bA.func_70012_b(this.field_70165_t + (double)(var7 * 4.5F), this.field_70163_u + 2.0D, this.field_70161_v + (double)(var30 * 4.5F), 0.0F, 0.0F);
            this.field_70990_bB.func_70071_h_();
            this.field_70990_bB.func_70012_b(this.field_70165_t - (double)(var7 * 4.5F), this.field_70163_u + 2.0D, this.field_70161_v - (double)(var30 * 4.5F), 0.0F, 0.0F);
            if (!this.field_70170_p.field_72995_K && this.field_70737_aN == 0) {
               this.func_70970_a(this.field_70170_p.func_72839_b(this, this.field_70983_bA.func_174813_aQ().func_72314_b(4.0D, 2.0D, 4.0D).func_72317_d(0.0D, -2.0D, 0.0D)));
               this.func_70970_a(this.field_70170_p.func_72839_b(this, this.field_70990_bB.func_174813_aQ().func_72314_b(4.0D, 2.0D, 4.0D).func_72317_d(0.0D, -2.0D, 0.0D)));
               this.func_70971_b(this.field_70170_p.func_72839_b(this, this.field_70986_h.func_174813_aQ().func_72314_b(1.0D, 1.0D, 1.0D)));
            }

            double[] var31 = this.func_70974_a(5, 1.0F);
            double[] var9 = this.func_70974_a(0, 1.0F);
            var33 = MathHelper.func_76126_a(this.field_70177_z * 3.1415927F / 180.0F - this.field_70704_bt * 0.01F);
            float var35 = MathHelper.func_76134_b(this.field_70177_z * 3.1415927F / 180.0F - this.field_70704_bt * 0.01F);
            this.field_70986_h.func_70071_h_();
            this.field_70986_h.func_70012_b(this.field_70165_t + (double)(var33 * 5.5F * var3), this.field_70163_u + (var9[1] - var31[1]) * 1.0D + (double)(var29 * 5.5F), this.field_70161_v - (double)(var35 * 5.5F * var3), 0.0F, 0.0F);

            for(int var32 = 0; var32 < 3; ++var32) {
               EntityDragonPart var34 = null;
               if (var32 == 0) {
                  var34 = this.field_70985_j;
               }

               if (var32 == 1) {
                  var34 = this.field_70984_by;
               }

               if (var32 == 2) {
                  var34 = this.field_70982_bz;
               }

               double[] var36 = this.func_70974_a(12 + var32 * 2, 1.0F);
               float var37 = this.field_70177_z * 3.1415927F / 180.0F + this.func_70973_b(var36[0] - var31[0]) * 3.1415927F / 180.0F * 1.0F;
               float var39 = MathHelper.func_76126_a(var37);
               float var38 = MathHelper.func_76134_b(var37);
               float var40 = 1.5F;
               float var41 = (float)(var32 + 1) * 2.0F;
               var34.func_70071_h_();
               var34.func_70012_b(this.field_70165_t - (double)((var30 * var40 + var39 * var41) * var3), this.field_70163_u + (var36[1] - var31[1]) * 1.0D - (double)((var41 + var40) * var29) + 1.5D, this.field_70161_v + (double)((var7 * var40 + var38 * var41) * var3), 0.0F, 0.0F);
            }

            if (!this.field_70170_p.field_72995_K) {
               this.field_70994_bF = this.func_70972_a(this.field_70986_h.func_174813_aQ()) | this.func_70972_a(this.field_70987_i.func_174813_aQ());
            }

         }
      }
   }

   private void func_70969_j() {
      if (this.field_70992_bH != null) {
         if (this.field_70992_bH.field_70128_L) {
            if (!this.field_70170_p.field_72995_K) {
               this.func_70965_a(this.field_70986_h, DamageSource.func_94539_a((Explosion)null), 10.0F);
            }

            this.field_70992_bH = null;
         } else if (this.field_70173_aa % 10 == 0 && this.func_110143_aJ() < this.func_110138_aP()) {
            this.func_70606_j(this.func_110143_aJ() + 1.0F);
         }
      }

      if (this.field_70146_Z.nextInt(10) == 0) {
         float var1 = 32.0F;
         List var2 = this.field_70170_p.func_72872_a(EntityEnderCrystal.class, this.func_174813_aQ().func_72314_b((double)var1, (double)var1, (double)var1));
         EntityEnderCrystal var3 = null;
         double var4 = 1.7976931348623157E308D;
         Iterator var6 = var2.iterator();

         while(var6.hasNext()) {
            EntityEnderCrystal var7 = (EntityEnderCrystal)var6.next();
            double var8 = var7.func_70068_e(this);
            if (var8 < var4) {
               var4 = var8;
               var3 = var7;
            }
         }

         this.field_70992_bH = var3;
      }

   }

   private void func_70970_a(List<Entity> var1) {
      double var2 = (this.field_70987_i.func_174813_aQ().field_72340_a + this.field_70987_i.func_174813_aQ().field_72336_d) / 2.0D;
      double var4 = (this.field_70987_i.func_174813_aQ().field_72339_c + this.field_70987_i.func_174813_aQ().field_72334_f) / 2.0D;
      Iterator var6 = var1.iterator();

      while(var6.hasNext()) {
         Entity var7 = (Entity)var6.next();
         if (var7 instanceof EntityLivingBase) {
            double var8 = var7.field_70165_t - var2;
            double var10 = var7.field_70161_v - var4;
            double var12 = var8 * var8 + var10 * var10;
            var7.func_70024_g(var8 / var12 * 4.0D, 0.20000000298023224D, var10 / var12 * 4.0D);
         }
      }

   }

   private void func_70971_b(List<Entity> var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         Entity var3 = (Entity)var1.get(var2);
         if (var3 instanceof EntityLivingBase) {
            var3.func_70097_a(DamageSource.func_76358_a(this), 10.0F);
            this.func_174815_a(this, var3);
         }
      }

   }

   private void func_70967_k() {
      this.field_70989_bE = false;
      ArrayList var1 = Lists.newArrayList(this.field_70170_p.field_73010_i);
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         if (((EntityPlayer)var2.next()).func_175149_v()) {
            var2.remove();
         }
      }

      if (this.field_70146_Z.nextInt(2) == 0 && !var1.isEmpty()) {
         this.field_70993_bI = (Entity)var1.get(this.field_70146_Z.nextInt(var1.size()));
      } else {
         boolean var3;
         do {
            this.field_70980_b = 0.0D;
            this.field_70981_c = (double)(70.0F + this.field_70146_Z.nextFloat() * 50.0F);
            this.field_70978_d = 0.0D;
            this.field_70980_b += (double)(this.field_70146_Z.nextFloat() * 120.0F - 60.0F);
            this.field_70978_d += (double)(this.field_70146_Z.nextFloat() * 120.0F - 60.0F);
            double var4 = this.field_70165_t - this.field_70980_b;
            double var6 = this.field_70163_u - this.field_70981_c;
            double var8 = this.field_70161_v - this.field_70978_d;
            var3 = var4 * var4 + var6 * var6 + var8 * var8 > 100.0D;
         } while(!var3);

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
               BlockPos var13 = new BlockPos(var10, var11, var12);
               Block var14 = this.field_70170_p.func_180495_p(var13).func_177230_c();
               if (var14.func_149688_o() != Material.field_151579_a) {
                  if (var14 != Blocks.field_180401_cv && var14 != Blocks.field_150343_Z && var14 != Blocks.field_150377_bs && var14 != Blocks.field_150357_h && var14 != Blocks.field_150483_bI && this.field_70170_p.func_82736_K().func_82766_b("mobGriefing")) {
                     var9 = this.field_70170_p.func_175698_g(var13) || var9;
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
         double var18 = var1.field_72339_c + (var1.field_72334_f - var1.field_72339_c) * (double)this.field_70146_Z.nextFloat();
         this.field_70170_p.func_175688_a(EnumParticleTypes.EXPLOSION_LARGE, var16, var17, var18, 0.0D, 0.0D, 0.0D);
      }

      return var8;
   }

   public boolean func_70965_a(EntityDragonPart var1, DamageSource var2, float var3) {
      if (var1 != this.field_70986_h) {
         var3 = var3 / 4.0F + 1.0F;
      }

      float var4 = this.field_70177_z * 3.1415927F / 180.0F;
      float var5 = MathHelper.func_76126_a(var4);
      float var6 = MathHelper.func_76134_b(var4);
      this.field_70980_b = this.field_70165_t + (double)(var5 * 5.0F) + (double)((this.field_70146_Z.nextFloat() - 0.5F) * 2.0F);
      this.field_70981_c = this.field_70163_u + (double)(this.field_70146_Z.nextFloat() * 3.0F) + 1.0D;
      this.field_70978_d = this.field_70161_v - (double)(var6 * 5.0F) + (double)((this.field_70146_Z.nextFloat() - 0.5F) * 2.0F);
      this.field_70993_bI = null;
      if (var2.func_76346_g() instanceof EntityPlayer || var2.func_94541_c()) {
         this.func_82195_e(var2, var3);
      }

      return true;
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (var1 instanceof EntityDamageSource && ((EntityDamageSource)var1).func_180139_w()) {
         this.func_82195_e(var1, var2);
      }

      return false;
   }

   protected boolean func_82195_e(DamageSource var1, float var2) {
      return super.func_70097_a(var1, var2);
   }

   public void func_174812_G() {
      this.func_70106_y();
   }

   protected void func_70609_aI() {
      ++this.field_70995_bG;
      if (this.field_70995_bG >= 180 && this.field_70995_bG <= 200) {
         float var1 = (this.field_70146_Z.nextFloat() - 0.5F) * 8.0F;
         float var2 = (this.field_70146_Z.nextFloat() - 0.5F) * 4.0F;
         float var3 = (this.field_70146_Z.nextFloat() - 0.5F) * 8.0F;
         this.field_70170_p.func_175688_a(EnumParticleTypes.EXPLOSION_HUGE, this.field_70165_t + (double)var1, this.field_70163_u + 2.0D + (double)var2, this.field_70161_v + (double)var3, 0.0D, 0.0D, 0.0D);
      }

      boolean var4 = this.field_70170_p.func_82736_K().func_82766_b("doMobLoot");
      int var5;
      int var6;
      if (!this.field_70170_p.field_72995_K) {
         if (this.field_70995_bG > 150 && this.field_70995_bG % 5 == 0 && var4) {
            var5 = 1000;

            while(var5 > 0) {
               var6 = EntityXPOrb.func_70527_a(var5);
               var5 -= var6;
               this.field_70170_p.func_72838_d(new EntityXPOrb(this.field_70170_p, this.field_70165_t, this.field_70163_u, this.field_70161_v, var6));
            }
         }

         if (this.field_70995_bG == 1) {
            this.field_70170_p.func_175669_a(1018, new BlockPos(this), 0);
         }
      }

      this.func_70091_d(0.0D, 0.10000000149011612D, 0.0D);
      this.field_70761_aq = this.field_70177_z += 20.0F;
      if (this.field_70995_bG == 200 && !this.field_70170_p.field_72995_K) {
         if (var4) {
            var5 = 2000;

            while(var5 > 0) {
               var6 = EntityXPOrb.func_70527_a(var5);
               var5 -= var6;
               this.field_70170_p.func_72838_d(new EntityXPOrb(this.field_70170_p, this.field_70165_t, this.field_70163_u, this.field_70161_v, var6));
            }
         }

         this.func_175499_a(new BlockPos(this.field_70165_t, 64.0D, this.field_70161_v));
         this.func_70106_y();
      }

   }

   private void func_175499_a(BlockPos var1) {
      boolean var2 = true;
      double var3 = 12.25D;
      double var5 = 6.25D;

      for(int var7 = -1; var7 <= 32; ++var7) {
         for(int var8 = -4; var8 <= 4; ++var8) {
            for(int var9 = -4; var9 <= 4; ++var9) {
               double var10 = (double)(var8 * var8 + var9 * var9);
               if (var10 <= 12.25D) {
                  BlockPos var12 = var1.func_177982_a(var8, var7, var9);
                  if (var7 < 0) {
                     if (var10 <= 6.25D) {
                        this.field_70170_p.func_175656_a(var12, Blocks.field_150357_h.func_176223_P());
                     }
                  } else if (var7 > 0) {
                     this.field_70170_p.func_175656_a(var12, Blocks.field_150350_a.func_176223_P());
                  } else if (var10 > 6.25D) {
                     this.field_70170_p.func_175656_a(var12, Blocks.field_150357_h.func_176223_P());
                  } else {
                     this.field_70170_p.func_175656_a(var12, Blocks.field_150384_bq.func_176223_P());
                  }
               }
            }
         }
      }

      this.field_70170_p.func_175656_a(var1, Blocks.field_150357_h.func_176223_P());
      this.field_70170_p.func_175656_a(var1.func_177984_a(), Blocks.field_150357_h.func_176223_P());
      BlockPos var13 = var1.func_177981_b(2);
      this.field_70170_p.func_175656_a(var13, Blocks.field_150357_h.func_176223_P());
      this.field_70170_p.func_175656_a(var13.func_177976_e(), Blocks.field_150478_aa.func_176223_P().func_177226_a(BlockTorch.field_176596_a, EnumFacing.EAST));
      this.field_70170_p.func_175656_a(var13.func_177974_f(), Blocks.field_150478_aa.func_176223_P().func_177226_a(BlockTorch.field_176596_a, EnumFacing.WEST));
      this.field_70170_p.func_175656_a(var13.func_177978_c(), Blocks.field_150478_aa.func_176223_P().func_177226_a(BlockTorch.field_176596_a, EnumFacing.SOUTH));
      this.field_70170_p.func_175656_a(var13.func_177968_d(), Blocks.field_150478_aa.func_176223_P().func_177226_a(BlockTorch.field_176596_a, EnumFacing.NORTH));
      this.field_70170_p.func_175656_a(var1.func_177981_b(3), Blocks.field_150357_h.func_176223_P());
      this.field_70170_p.func_175656_a(var1.func_177981_b(4), Blocks.field_150380_bt.func_176223_P());
   }

   protected void func_70623_bb() {
   }

   public Entity[] func_70021_al() {
      return this.field_70977_g;
   }

   public boolean func_70067_L() {
      return false;
   }

   public World func_82194_d() {
      return this.field_70170_p;
   }

   protected String func_70639_aQ() {
      return "mob.enderdragon.growl";
   }

   protected String func_70621_aR() {
      return "mob.enderdragon.hit";
   }

   protected float func_70599_aP() {
      return 5.0F;
   }
}
