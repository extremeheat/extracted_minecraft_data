package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityBoat extends Entity {
   private boolean field_70279_a = true;
   private double field_70276_b = 0.07;
   private int field_70277_c;
   private double field_70274_d;
   private double field_70275_e;
   private double field_70272_f;
   private double field_70273_g;
   private double field_70281_h;
   private double field_70282_i;
   private double field_70280_j;
   private double field_70278_an;

   public EntityBoat(World var1) {
      super(var1);
      this.field_70156_m = true;
      this.func_70105_a(1.5F, 0.6F);
      this.field_70129_M = this.field_70131_O / 2.0F;
   }

   @Override
   protected boolean func_70041_e_() {
      return false;
   }

   @Override
   protected void func_70088_a() {
      this.field_70180_af.func_75682_a(17, new Integer(0));
      this.field_70180_af.func_75682_a(18, new Integer(1));
      this.field_70180_af.func_75682_a(19, new Float(0.0F));
   }

   @Override
   public AxisAlignedBB func_70114_g(Entity var1) {
      return var1.field_70121_D;
   }

   @Override
   public AxisAlignedBB func_70046_E() {
      return this.field_70121_D;
   }

   @Override
   public boolean func_70104_M() {
      return true;
   }

   public EntityBoat(World var1, double var2, double var4, double var6) {
      this(var1);
      this.func_70107_b(var2, var4 + (double)this.field_70129_M, var6);
      this.field_70159_w = 0.0;
      this.field_70181_x = 0.0;
      this.field_70179_y = 0.0;
      this.field_70169_q = var2;
      this.field_70167_r = var4;
      this.field_70166_s = var6;
   }

   @Override
   public double func_70042_X() {
      return (double)this.field_70131_O * 0.0 - 0.30000001192092896;
   }

   @Override
   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_85032_ar()) {
         return false;
      } else if (!this.field_70170_p.field_72995_K && !this.field_70128_L) {
         this.func_70269_c(-this.func_70267_i());
         this.func_70265_b(10);
         this.func_70266_a(this.func_70271_g() + var2 * 10.0F);
         this.func_70018_K();
         boolean var3 = var1.func_76346_g() instanceof EntityPlayer && ((EntityPlayer)var1.func_76346_g()).field_71075_bZ.field_75098_d;
         if (var3 || this.func_70271_g() > 40.0F) {
            if (this.field_70153_n != null) {
               this.field_70153_n.func_70078_a(this);
            }

            if (!var3) {
               this.func_145778_a(Items.field_151124_az, 1, 0.0F);
            }

            this.func_70106_y();
         }

         return true;
      } else {
         return true;
      }
   }

   @Override
   public void func_70057_ab() {
      this.func_70269_c(-this.func_70267_i());
      this.func_70265_b(10);
      this.func_70266_a(this.func_70271_g() * 11.0F);
   }

   @Override
   public boolean func_70067_L() {
      return !this.field_70128_L;
   }

   @Override
   public void func_70056_a(double var1, double var3, double var5, float var7, float var8, int var9) {
      if (this.field_70279_a) {
         this.field_70277_c = var9 + 5;
      } else {
         double var10 = var1 - this.field_70165_t;
         double var12 = var3 - this.field_70163_u;
         double var14 = var5 - this.field_70161_v;
         double var16 = var10 * var10 + var12 * var12 + var14 * var14;
         if (!(var16 > 1.0)) {
            return;
         }

         this.field_70277_c = 3;
      }

      this.field_70274_d = var1;
      this.field_70275_e = var3;
      this.field_70272_f = var5;
      this.field_70273_g = (double)var7;
      this.field_70281_h = (double)var8;
      this.field_70159_w = this.field_70282_i;
      this.field_70181_x = this.field_70280_j;
      this.field_70179_y = this.field_70278_an;
   }

   @Override
   public void func_70016_h(double var1, double var3, double var5) {
      this.field_70282_i = this.field_70159_w = var1;
      this.field_70280_j = this.field_70181_x = var3;
      this.field_70278_an = this.field_70179_y = var5;
   }

   @Override
   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.func_70268_h() > 0) {
         this.func_70265_b(this.func_70268_h() - 1);
      }

      if (this.func_70271_g() > 0.0F) {
         this.func_70266_a(this.func_70271_g() - 1.0F);
      }

      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      byte var1 = 5;
      double var2 = 0.0;

      for(int var4 = 0; var4 < var1; ++var4) {
         double var5 = this.field_70121_D.field_72338_b
            + (this.field_70121_D.field_72337_e - this.field_70121_D.field_72338_b) * (double)(var4 + 0) / (double)var1
            - 0.125;
         double var7 = this.field_70121_D.field_72338_b
            + (this.field_70121_D.field_72337_e - this.field_70121_D.field_72338_b) * (double)(var4 + 1) / (double)var1
            - 0.125;
         AxisAlignedBB var9 = AxisAlignedBB.func_72330_a(
            this.field_70121_D.field_72340_a, var5, this.field_70121_D.field_72339_c, this.field_70121_D.field_72336_d, var7, this.field_70121_D.field_72334_f
         );
         if (this.field_70170_p.func_72830_b(var9, Material.field_151586_h)) {
            var2 += 1.0 / (double)var1;
         }
      }

      double var19 = Math.sqrt(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
      if (var19 > 0.26249999999999996) {
         double var6 = Math.cos((double)this.field_70177_z * 3.141592653589793 / 180.0);
         double var8 = Math.sin((double)this.field_70177_z * 3.141592653589793 / 180.0);

         for(int var10 = 0; (double)var10 < 1.0 + var19 * 60.0; ++var10) {
            double var11 = (double)(this.field_70146_Z.nextFloat() * 2.0F - 1.0F);
            double var13 = (double)(this.field_70146_Z.nextInt(2) * 2 - 1) * 0.7;
            if (this.field_70146_Z.nextBoolean()) {
               double var15 = this.field_70165_t - var6 * var11 * 0.8 + var8 * var13;
               double var17 = this.field_70161_v - var8 * var11 * 0.8 - var6 * var13;
               this.field_70170_p.func_72869_a("splash", var15, this.field_70163_u - 0.125, var17, this.field_70159_w, this.field_70181_x, this.field_70179_y);
            } else {
               double var42 = this.field_70165_t + var6 + var8 * var11 * 0.7;
               double var43 = this.field_70161_v + var8 - var6 * var11 * 0.7;
               this.field_70170_p.func_72869_a("splash", var42, this.field_70163_u - 0.125, var43, this.field_70159_w, this.field_70181_x, this.field_70179_y);
            }
         }
      }

      if (this.field_70170_p.field_72995_K && this.field_70279_a) {
         if (this.field_70277_c > 0) {
            double var23 = this.field_70165_t + (this.field_70274_d - this.field_70165_t) / (double)this.field_70277_c;
            double var31 = this.field_70163_u + (this.field_70275_e - this.field_70163_u) / (double)this.field_70277_c;
            double var36 = this.field_70161_v + (this.field_70272_f - this.field_70161_v) / (double)this.field_70277_c;
            double var40 = MathHelper.func_76138_g(this.field_70273_g - (double)this.field_70177_z);
            this.field_70177_z = (float)((double)this.field_70177_z + var40 / (double)this.field_70277_c);
            this.field_70125_A = (float)((double)this.field_70125_A + (this.field_70281_h - (double)this.field_70125_A) / (double)this.field_70277_c);
            --this.field_70277_c;
            this.func_70107_b(var23, var31, var36);
            this.func_70101_b(this.field_70177_z, this.field_70125_A);
         } else {
            double var24 = this.field_70165_t + this.field_70159_w;
            double var32 = this.field_70163_u + this.field_70181_x;
            double var37 = this.field_70161_v + this.field_70179_y;
            this.func_70107_b(var24, var32, var37);
            if (this.field_70122_E) {
               this.field_70159_w *= 0.5;
               this.field_70181_x *= 0.5;
               this.field_70179_y *= 0.5;
            }

            this.field_70159_w *= 0.9900000095367432;
            this.field_70181_x *= 0.949999988079071;
            this.field_70179_y *= 0.9900000095367432;
         }
      } else {
         if (var2 < 1.0) {
            double var20 = var2 * 2.0 - 1.0;
            this.field_70181_x += 0.03999999910593033 * var20;
         } else {
            if (this.field_70181_x < 0.0) {
               this.field_70181_x /= 2.0;
            }

            this.field_70181_x += 0.007000000216066837;
         }

         if (this.field_70153_n != null && this.field_70153_n instanceof EntityLivingBase) {
            EntityLivingBase var21 = (EntityLivingBase)this.field_70153_n;
            float var25 = this.field_70153_n.field_70177_z + -var21.field_70702_br * 90.0F;
            this.field_70159_w += -Math.sin((double)(var25 * 3.1415927F / 180.0F)) * this.field_70276_b * (double)var21.field_70701_bs * 0.05000000074505806;
            this.field_70179_y += Math.cos((double)(var25 * 3.1415927F / 180.0F)) * this.field_70276_b * (double)var21.field_70701_bs * 0.05000000074505806;
         }

         double var22 = Math.sqrt(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
         if (var22 > 0.35) {
            double var26 = 0.35 / var22;
            this.field_70159_w *= var26;
            this.field_70179_y *= var26;
            var22 = 0.35;
         }

         if (var22 > var19 && this.field_70276_b < 0.35) {
            this.field_70276_b += (0.35 - this.field_70276_b) / 35.0;
            if (this.field_70276_b > 0.35) {
               this.field_70276_b = 0.35;
            }
         } else {
            this.field_70276_b -= (this.field_70276_b - 0.07) / 35.0;
            if (this.field_70276_b < 0.07) {
               this.field_70276_b = 0.07;
            }
         }

         for(int var27 = 0; var27 < 4; ++var27) {
            int var33 = MathHelper.func_76128_c(this.field_70165_t + ((double)(var27 % 2) - 0.5) * 0.8);
            int var34 = MathHelper.func_76128_c(this.field_70161_v + ((double)(var27 / 2) - 0.5) * 0.8);

            for(int var38 = 0; var38 < 2; ++var38) {
               int var12 = MathHelper.func_76128_c(this.field_70163_u) + var38;
               Block var41 = this.field_70170_p.func_147439_a(var33, var12, var34);
               if (var41 == Blocks.field_150431_aC) {
                  this.field_70170_p.func_147468_f(var33, var12, var34);
                  this.field_70123_F = false;
               } else if (var41 == Blocks.field_150392_bi) {
                  this.field_70170_p.func_147480_a(var33, var12, var34, true);
                  this.field_70123_F = false;
               }
            }
         }

         if (this.field_70122_E) {
            this.field_70159_w *= 0.5;
            this.field_70181_x *= 0.5;
            this.field_70179_y *= 0.5;
         }

         this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
         if (!this.field_70123_F || !(var19 > 0.2)) {
            this.field_70159_w *= 0.9900000095367432;
            this.field_70181_x *= 0.949999988079071;
            this.field_70179_y *= 0.9900000095367432;
         } else if (!this.field_70170_p.field_72995_K && !this.field_70128_L) {
            this.func_70106_y();

            for(int var28 = 0; var28 < 3; ++var28) {
               this.func_145778_a(Item.func_150898_a(Blocks.field_150344_f), 1, 0.0F);
            }

            for(int var29 = 0; var29 < 2; ++var29) {
               this.func_145778_a(Items.field_151055_y, 1, 0.0F);
            }
         }

         this.field_70125_A = 0.0F;
         double var30 = (double)this.field_70177_z;
         double var35 = this.field_70169_q - this.field_70165_t;
         double var39 = this.field_70166_s - this.field_70161_v;
         if (var35 * var35 + var39 * var39 > 0.001) {
            var30 = (double)((float)(Math.atan2(var39, var35) * 180.0 / 3.141592653589793));
         }

         double var14 = MathHelper.func_76138_g(var30 - (double)this.field_70177_z);
         if (var14 > 20.0) {
            var14 = 20.0;
         }

         if (var14 < -20.0) {
            var14 = -20.0;
         }

         this.field_70177_z = (float)((double)this.field_70177_z + var14);
         this.func_70101_b(this.field_70177_z, this.field_70125_A);
         if (!this.field_70170_p.field_72995_K) {
            List var16 = this.field_70170_p.func_72839_b(this, this.field_70121_D.func_72314_b(0.20000000298023224, 0.0, 0.20000000298023224));
            if (var16 != null && !var16.isEmpty()) {
               for(int var44 = 0; var44 < var16.size(); ++var44) {
                  Entity var18 = (Entity)var16.get(var44);
                  if (var18 != this.field_70153_n && var18.func_70104_M() && var18 instanceof EntityBoat) {
                     var18.func_70108_f(this);
                  }
               }
            }

            if (this.field_70153_n != null && this.field_70153_n.field_70128_L) {
               this.field_70153_n = null;
            }
         }
      }
   }

   @Override
   public void func_70043_V() {
      if (this.field_70153_n != null) {
         double var1 = Math.cos((double)this.field_70177_z * 3.141592653589793 / 180.0) * 0.4;
         double var3 = Math.sin((double)this.field_70177_z * 3.141592653589793 / 180.0) * 0.4;
         this.field_70153_n
            .func_70107_b(this.field_70165_t + var1, this.field_70163_u + this.func_70042_X() + this.field_70153_n.func_70033_W(), this.field_70161_v + var3);
      }
   }

   @Override
   protected void func_70014_b(NBTTagCompound var1) {
   }

   @Override
   protected void func_70037_a(NBTTagCompound var1) {
   }

   @Override
   public float func_70053_R() {
      return 0.0F;
   }

   @Override
   public boolean func_130002_c(EntityPlayer var1) {
      if (this.field_70153_n != null && this.field_70153_n instanceof EntityPlayer && this.field_70153_n != var1) {
         return true;
      } else {
         if (!this.field_70170_p.field_72995_K) {
            var1.func_70078_a(this);
         }

         return true;
      }
   }

   @Override
   protected void func_70064_a(double var1, boolean var3) {
      int var4 = MathHelper.func_76128_c(this.field_70165_t);
      int var5 = MathHelper.func_76128_c(this.field_70163_u);
      int var6 = MathHelper.func_76128_c(this.field_70161_v);
      if (var3) {
         if (this.field_70143_R > 3.0F) {
            this.func_70069_a(this.field_70143_R);
            if (!this.field_70170_p.field_72995_K && !this.field_70128_L) {
               this.func_70106_y();

               for(int var7 = 0; var7 < 3; ++var7) {
                  this.func_145778_a(Item.func_150898_a(Blocks.field_150344_f), 1, 0.0F);
               }

               for(int var8 = 0; var8 < 2; ++var8) {
                  this.func_145778_a(Items.field_151055_y, 1, 0.0F);
               }
            }

            this.field_70143_R = 0.0F;
         }
      } else if (this.field_70170_p.func_147439_a(var4, var5 - 1, var6).func_149688_o() != Material.field_151586_h && var1 < 0.0) {
         this.field_70143_R = (float)((double)this.field_70143_R - var1);
      }
   }

   public void func_70266_a(float var1) {
      this.field_70180_af.func_75692_b(19, var1);
   }

   public float func_70271_g() {
      return this.field_70180_af.func_111145_d(19);
   }

   public void func_70265_b(int var1) {
      this.field_70180_af.func_75692_b(17, var1);
   }

   public int func_70268_h() {
      return this.field_70180_af.func_75679_c(17);
   }

   public void func_70269_c(int var1) {
      this.field_70180_af.func_75692_b(18, var1);
   }

   public int func_70267_i() {
      return this.field_70180_af.func_75679_c(18);
   }

   public void func_70270_d(boolean var1) {
      this.field_70279_a = var1;
   }
}
