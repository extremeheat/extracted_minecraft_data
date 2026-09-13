package net.minecraft.entity.projectile;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityArrow extends Entity implements IProjectile {
   private int field_145791_d = -1;
   private int field_145792_e = -1;
   private int field_145789_f = -1;
   private Block field_145790_g;
   private int field_70253_h;
   private boolean field_70254_i;
   public int field_70251_a;
   public int field_70249_b;
   public Entity field_70250_c;
   private int field_70252_j;
   private int field_70257_an;
   private double field_70255_ao = 2.0;
   private int field_70256_ap;

   public EntityArrow(World var1) {
      super(var1);
      this.field_70155_l = 10.0;
      this.func_70105_a(0.5F, 0.5F);
   }

   public EntityArrow(World var1, double var2, double var4, double var6) {
      super(var1);
      this.field_70155_l = 10.0;
      this.func_70105_a(0.5F, 0.5F);
      this.func_70107_b(var2, var4, var6);
      this.field_70129_M = 0.0F;
   }

   public EntityArrow(World var1, EntityLivingBase var2, EntityLivingBase var3, float var4, float var5) {
      super(var1);
      this.field_70155_l = 10.0;
      this.field_70250_c = var2;
      if (var2 instanceof EntityPlayer) {
         this.field_70251_a = 1;
      }

      this.field_70163_u = var2.field_70163_u + (double)var2.func_70047_e() - 0.10000000149011612;
      double var6 = var3.field_70165_t - var2.field_70165_t;
      double var8 = var3.field_70121_D.field_72338_b + (double)(var3.field_70131_O / 3.0F) - this.field_70163_u;
      double var10 = var3.field_70161_v - var2.field_70161_v;
      double var12 = (double)MathHelper.func_76133_a(var6 * var6 + var10 * var10);
      if (!(var12 < 1.0E-7)) {
         float var14 = (float)(Math.atan2(var10, var6) * 180.0 / 3.1415927410125732) - 90.0F;
         float var15 = (float)(-(Math.atan2(var8, var12) * 180.0 / 3.1415927410125732));
         double var16 = var6 / var12;
         double var18 = var10 / var12;
         this.func_70012_b(var2.field_70165_t + var16, this.field_70163_u, var2.field_70161_v + var18, var14, var15);
         this.field_70129_M = 0.0F;
         float var20 = (float)var12 * 0.2F;
         this.func_70186_c(var6, var8 + (double)var20, var10, var4, var5);
      }
   }

   public EntityArrow(World var1, EntityLivingBase var2, float var3) {
      super(var1);
      this.field_70155_l = 10.0;
      this.field_70250_c = var2;
      if (var2 instanceof EntityPlayer) {
         this.field_70251_a = 1;
      }

      this.func_70105_a(0.5F, 0.5F);
      this.func_70012_b(var2.field_70165_t, var2.field_70163_u + (double)var2.func_70047_e(), var2.field_70161_v, var2.field_70177_z, var2.field_70125_A);
      this.field_70165_t -= (double)(MathHelper.func_76134_b(this.field_70177_z / 180.0F * 3.1415927F) * 0.16F);
      this.field_70163_u -= 0.10000000149011612;
      this.field_70161_v -= (double)(MathHelper.func_76126_a(this.field_70177_z / 180.0F * 3.1415927F) * 0.16F);
      this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      this.field_70129_M = 0.0F;
      this.field_70159_w = (double)(
         -MathHelper.func_76126_a(this.field_70177_z / 180.0F * 3.1415927F) * MathHelper.func_76134_b(this.field_70125_A / 180.0F * 3.1415927F)
      );
      this.field_70179_y = (double)(
         MathHelper.func_76134_b(this.field_70177_z / 180.0F * 3.1415927F) * MathHelper.func_76134_b(this.field_70125_A / 180.0F * 3.1415927F)
      );
      this.field_70181_x = (double)(-MathHelper.func_76126_a(this.field_70125_A / 180.0F * 3.1415927F));
      this.func_70186_c(this.field_70159_w, this.field_70181_x, this.field_70179_y, var3 * 1.5F, 1.0F);
   }

   @Override
   protected void func_70088_a() {
      this.field_70180_af.func_75682_a(16, (byte)0);
   }

   @Override
   public void func_70186_c(double var1, double var3, double var5, float var7, float var8) {
      float var9 = MathHelper.func_76133_a(var1 * var1 + var3 * var3 + var5 * var5);
      var1 /= (double)var9;
      var3 /= (double)var9;
      var5 /= (double)var9;
      var1 += this.field_70146_Z.nextGaussian() * (double)(this.field_70146_Z.nextBoolean() ? -1 : 1) * 0.007499999832361937 * (double)var8;
      var3 += this.field_70146_Z.nextGaussian() * (double)(this.field_70146_Z.nextBoolean() ? -1 : 1) * 0.007499999832361937 * (double)var8;
      var5 += this.field_70146_Z.nextGaussian() * (double)(this.field_70146_Z.nextBoolean() ? -1 : 1) * 0.007499999832361937 * (double)var8;
      var1 *= (double)var7;
      var3 *= (double)var7;
      var5 *= (double)var7;
      this.field_70159_w = var1;
      this.field_70181_x = var3;
      this.field_70179_y = var5;
      float var10 = MathHelper.func_76133_a(var1 * var1 + var5 * var5);
      this.field_70126_B = this.field_70177_z = (float)(Math.atan2(var1, var5) * 180.0 / 3.1415927410125732);
      this.field_70127_C = this.field_70125_A = (float)(Math.atan2(var3, (double)var10) * 180.0 / 3.1415927410125732);
      this.field_70252_j = 0;
   }

   @Override
   public void func_70056_a(double var1, double var3, double var5, float var7, float var8, int var9) {
      this.func_70107_b(var1, var3, var5);
      this.func_70101_b(var7, var8);
   }

   @Override
   public void func_70016_h(double var1, double var3, double var5) {
      this.field_70159_w = var1;
      this.field_70181_x = var3;
      this.field_70179_y = var5;
      if (this.field_70127_C == 0.0F && this.field_70126_B == 0.0F) {
         float var7 = MathHelper.func_76133_a(var1 * var1 + var5 * var5);
         this.field_70126_B = this.field_70177_z = (float)(Math.atan2(var1, var5) * 180.0 / 3.1415927410125732);
         this.field_70127_C = this.field_70125_A = (float)(Math.atan2(var3, (double)var7) * 180.0 / 3.1415927410125732);
         this.field_70127_C = this.field_70125_A;
         this.field_70126_B = this.field_70177_z;
         this.func_70012_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
         this.field_70252_j = 0;
      }
   }

   @Override
   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.field_70127_C == 0.0F && this.field_70126_B == 0.0F) {
         float var1 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
         this.field_70126_B = this.field_70177_z = (float)(Math.atan2(this.field_70159_w, this.field_70179_y) * 180.0 / 3.1415927410125732);
         this.field_70127_C = this.field_70125_A = (float)(Math.atan2(this.field_70181_x, (double)var1) * 180.0 / 3.1415927410125732);
      }

      Block var16 = this.field_70170_p.func_147439_a(this.field_145791_d, this.field_145792_e, this.field_145789_f);
      if (var16.func_149688_o() != Material.field_151579_a) {
         var16.func_149719_a(this.field_70170_p, this.field_145791_d, this.field_145792_e, this.field_145789_f);
         AxisAlignedBB var2 = var16.func_149668_a(this.field_70170_p, this.field_145791_d, this.field_145792_e, this.field_145789_f);
         if (var2 != null && var2.func_72318_a(Vec3.func_72443_a(this.field_70165_t, this.field_70163_u, this.field_70161_v))) {
            this.field_70254_i = true;
         }
      }

      if (this.field_70249_b > 0) {
         --this.field_70249_b;
      }

      if (this.field_70254_i) {
         int var19 = this.field_70170_p.func_72805_g(this.field_145791_d, this.field_145792_e, this.field_145789_f);
         if (var16 == this.field_145790_g && var19 == this.field_70253_h) {
            ++this.field_70252_j;
            if (this.field_70252_j == 1200) {
               this.func_70106_y();
            }
         } else {
            this.field_70254_i = false;
            this.field_70159_w *= (double)(this.field_70146_Z.nextFloat() * 0.2F);
            this.field_70181_x *= (double)(this.field_70146_Z.nextFloat() * 0.2F);
            this.field_70179_y *= (double)(this.field_70146_Z.nextFloat() * 0.2F);
            this.field_70252_j = 0;
            this.field_70257_an = 0;
         }
      } else {
         ++this.field_70257_an;
         Vec3 var17 = Vec3.func_72443_a(this.field_70165_t, this.field_70163_u, this.field_70161_v);
         Vec3 var3 = Vec3.func_72443_a(
            this.field_70165_t + this.field_70159_w, this.field_70163_u + this.field_70181_x, this.field_70161_v + this.field_70179_y
         );
         MovingObjectPosition var4 = this.field_70170_p.func_147447_a(var17, var3, false, true, false);
         var17 = Vec3.func_72443_a(this.field_70165_t, this.field_70163_u, this.field_70161_v);
         var3 = Vec3.func_72443_a(this.field_70165_t + this.field_70159_w, this.field_70163_u + this.field_70181_x, this.field_70161_v + this.field_70179_y);
         if (var4 != null) {
            var3 = Vec3.func_72443_a(var4.field_72307_f.field_72450_a, var4.field_72307_f.field_72448_b, var4.field_72307_f.field_72449_c);
         }

         Entity var5 = null;
         List var6 = this.field_70170_p
            .func_72839_b(this, this.field_70121_D.func_72321_a(this.field_70159_w, this.field_70181_x, this.field_70179_y).func_72314_b(1.0, 1.0, 1.0));
         double var7 = 0.0;

         for(int var9 = 0; var9 < var6.size(); ++var9) {
            Entity var10 = (Entity)var6.get(var9);
            if (var10.func_70067_L() && (var10 != this.field_70250_c || this.field_70257_an >= 5)) {
               float var11 = 0.3F;
               AxisAlignedBB var12 = var10.field_70121_D.func_72314_b((double)var11, (double)var11, (double)var11);
               MovingObjectPosition var13 = var12.func_72327_a(var17, var3);
               if (var13 != null) {
                  double var14 = var17.func_72438_d(var13.field_72307_f);
                  if (var14 < var7 || var7 == 0.0) {
                     var5 = var10;
                     var7 = var14;
                  }
               }
            }
         }

         if (var5 != null) {
            var4 = new MovingObjectPosition(var5);
         }

         if (var4 != null && var4.field_72308_g != null && var4.field_72308_g instanceof EntityPlayer) {
            EntityPlayer var21 = (EntityPlayer)var4.field_72308_g;
            if (var21.field_71075_bZ.field_75102_a || this.field_70250_c instanceof EntityPlayer && !((EntityPlayer)this.field_70250_c).func_96122_a(var21)) {
               var4 = null;
            }
         }

         if (var4 != null) {
            if (var4.field_72308_g != null) {
               float var22 = MathHelper.func_76133_a(
                  this.field_70159_w * this.field_70159_w + this.field_70181_x * this.field_70181_x + this.field_70179_y * this.field_70179_y
               );
               int var26 = MathHelper.func_76143_f((double)var22 * this.field_70255_ao);
               if (this.func_70241_g()) {
                  var26 += this.field_70146_Z.nextInt(var26 / 2 + 2);
               }

               Object var28 = null;
               DamageSource var29;
               if (this.field_70250_c == null) {
                  var29 = DamageSource.func_76353_a(this, this);
               } else {
                  var29 = DamageSource.func_76353_a(this, this.field_70250_c);
               }

               if (this.func_70027_ad() && !(var4.field_72308_g instanceof EntityEnderman)) {
                  var4.field_72308_g.func_70015_d(5);
               }

               if (var4.field_72308_g.func_70097_a(var29, (float)var26)) {
                  if (var4.field_72308_g instanceof EntityLivingBase) {
                     EntityLivingBase var31 = (EntityLivingBase)var4.field_72308_g;
                     if (!this.field_70170_p.field_72995_K) {
                        var31.func_85034_r(var31.func_85035_bI() + 1);
                     }

                     if (this.field_70256_ap > 0) {
                        float var33 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
                        if (var33 > 0.0F) {
                           var4.field_72308_g
                              .func_70024_g(
                                 this.field_70159_w * (double)this.field_70256_ap * 0.6000000238418579 / (double)var33,
                                 0.1,
                                 this.field_70179_y * (double)this.field_70256_ap * 0.6000000238418579 / (double)var33
                              );
                        }
                     }

                     if (this.field_70250_c != null && this.field_70250_c instanceof EntityLivingBase) {
                        EnchantmentHelper.func_151384_a(var31, this.field_70250_c);
                        EnchantmentHelper.func_151385_b((EntityLivingBase)this.field_70250_c, var31);
                     }

                     if (this.field_70250_c != null
                        && var4.field_72308_g != this.field_70250_c
                        && var4.field_72308_g instanceof EntityPlayer
                        && this.field_70250_c instanceof EntityPlayerMP) {
                        ((EntityPlayerMP)this.field_70250_c).field_71135_a.func_147359_a(new S2BPacketChangeGameState(6, 0.0F));
                     }
                  }

                  this.func_85030_a("random.bowhit", 1.0F, 1.2F / (this.field_70146_Z.nextFloat() * 0.2F + 0.9F));
                  if (!(var4.field_72308_g instanceof EntityEnderman)) {
                     this.func_70106_y();
                  }
               } else {
                  this.field_70159_w *= -0.10000000149011612;
                  this.field_70181_x *= -0.10000000149011612;
                  this.field_70179_y *= -0.10000000149011612;
                  this.field_70177_z += 180.0F;
                  this.field_70126_B += 180.0F;
                  this.field_70257_an = 0;
               }
            } else {
               this.field_145791_d = var4.field_72311_b;
               this.field_145792_e = var4.field_72312_c;
               this.field_145789_f = var4.field_72309_d;
               this.field_145790_g = this.field_70170_p.func_147439_a(this.field_145791_d, this.field_145792_e, this.field_145789_f);
               this.field_70253_h = this.field_70170_p.func_72805_g(this.field_145791_d, this.field_145792_e, this.field_145789_f);
               this.field_70159_w = (double)((float)(var4.field_72307_f.field_72450_a - this.field_70165_t));
               this.field_70181_x = (double)((float)(var4.field_72307_f.field_72448_b - this.field_70163_u));
               this.field_70179_y = (double)((float)(var4.field_72307_f.field_72449_c - this.field_70161_v));
               float var23 = MathHelper.func_76133_a(
                  this.field_70159_w * this.field_70159_w + this.field_70181_x * this.field_70181_x + this.field_70179_y * this.field_70179_y
               );
               this.field_70165_t -= this.field_70159_w / (double)var23 * 0.05000000074505806;
               this.field_70163_u -= this.field_70181_x / (double)var23 * 0.05000000074505806;
               this.field_70161_v -= this.field_70179_y / (double)var23 * 0.05000000074505806;
               this.func_85030_a("random.bowhit", 1.0F, 1.2F / (this.field_70146_Z.nextFloat() * 0.2F + 0.9F));
               this.field_70254_i = true;
               this.field_70249_b = 7;
               this.func_70243_d(false);
               if (this.field_145790_g.func_149688_o() != Material.field_151579_a) {
                  this.field_145790_g.func_149670_a(this.field_70170_p, this.field_145791_d, this.field_145792_e, this.field_145789_f, this);
               }
            }
         }

         if (this.func_70241_g()) {
            for(int var24 = 0; var24 < 4; ++var24) {
               this.field_70170_p
                  .func_72869_a(
                     "crit",
                     this.field_70165_t + this.field_70159_w * (double)var24 / 4.0,
                     this.field_70163_u + this.field_70181_x * (double)var24 / 4.0,
                     this.field_70161_v + this.field_70179_y * (double)var24 / 4.0,
                     -this.field_70159_w,
                     -this.field_70181_x + 0.2,
                     -this.field_70179_y
                  );
            }
         }

         this.field_70165_t += this.field_70159_w;
         this.field_70163_u += this.field_70181_x;
         this.field_70161_v += this.field_70179_y;
         float var25 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
         this.field_70177_z = (float)(Math.atan2(this.field_70159_w, this.field_70179_y) * 180.0 / 3.1415927410125732);
         this.field_70125_A = (float)(Math.atan2(this.field_70181_x, (double)var25) * 180.0 / 3.1415927410125732);

         while(this.field_70125_A - this.field_70127_C < -180.0F) {
            this.field_70127_C -= 360.0F;
         }

         while(this.field_70125_A - this.field_70127_C >= 180.0F) {
            this.field_70127_C += 360.0F;
         }

         while(this.field_70177_z - this.field_70126_B < -180.0F) {
            this.field_70126_B -= 360.0F;
         }

         while(this.field_70177_z - this.field_70126_B >= 180.0F) {
            this.field_70126_B += 360.0F;
         }

         this.field_70125_A = this.field_70127_C + (this.field_70125_A - this.field_70127_C) * 0.2F;
         this.field_70177_z = this.field_70126_B + (this.field_70177_z - this.field_70126_B) * 0.2F;
         float var27 = 0.99F;
         float var30 = 0.05F;
         if (this.func_70090_H()) {
            for(int var32 = 0; var32 < 4; ++var32) {
               float var34 = 0.25F;
               this.field_70170_p
                  .func_72869_a(
                     "bubble",
                     this.field_70165_t - this.field_70159_w * (double)var34,
                     this.field_70163_u - this.field_70181_x * (double)var34,
                     this.field_70161_v - this.field_70179_y * (double)var34,
                     this.field_70159_w,
                     this.field_70181_x,
                     this.field_70179_y
                  );
            }

            var27 = 0.8F;
         }

         if (this.func_70026_G()) {
            this.func_70066_B();
         }

         this.field_70159_w *= (double)var27;
         this.field_70181_x *= (double)var27;
         this.field_70179_y *= (double)var27;
         this.field_70181_x -= (double)var30;
         this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
         this.func_145775_I();
      }
   }

   @Override
   public void func_70014_b(NBTTagCompound var1) {
      var1.func_74777_a("xTile", (short)this.field_145791_d);
      var1.func_74777_a("yTile", (short)this.field_145792_e);
      var1.func_74777_a("zTile", (short)this.field_145789_f);
      var1.func_74777_a("life", (short)this.field_70252_j);
      var1.func_74774_a("inTile", (byte)Block.func_149682_b(this.field_145790_g));
      var1.func_74774_a("inData", (byte)this.field_70253_h);
      var1.func_74774_a("shake", (byte)this.field_70249_b);
      var1.func_74774_a("inGround", (byte)(this.field_70254_i ? 1 : 0));
      var1.func_74774_a("pickup", (byte)this.field_70251_a);
      var1.func_74780_a("damage", this.field_70255_ao);
   }

   @Override
   public void func_70037_a(NBTTagCompound var1) {
      this.field_145791_d = var1.func_74765_d("xTile");
      this.field_145792_e = var1.func_74765_d("yTile");
      this.field_145789_f = var1.func_74765_d("zTile");
      this.field_70252_j = var1.func_74765_d("life");
      this.field_145790_g = Block.func_149729_e(var1.func_74771_c("inTile") & 255);
      this.field_70253_h = var1.func_74771_c("inData") & 255;
      this.field_70249_b = var1.func_74771_c("shake") & 255;
      this.field_70254_i = var1.func_74771_c("inGround") == 1;
      if (var1.func_150297_b("damage", 99)) {
         this.field_70255_ao = var1.func_74769_h("damage");
      }

      if (var1.func_150297_b("pickup", 99)) {
         this.field_70251_a = var1.func_74771_c("pickup");
      } else if (var1.func_150297_b("player", 99)) {
         this.field_70251_a = var1.func_74767_n("player") ? 1 : 0;
      }
   }

   @Override
   public void func_70100_b_(EntityPlayer var1) {
      if (!this.field_70170_p.field_72995_K && this.field_70254_i && this.field_70249_b <= 0) {
         boolean var2 = this.field_70251_a == 1 || this.field_70251_a == 2 && var1.field_71075_bZ.field_75098_d;
         if (this.field_70251_a == 1 && !var1.field_71071_by.func_70441_a(new ItemStack(Items.field_151032_g, 1))) {
            var2 = false;
         }

         if (var2) {
            this.func_85030_a("random.pop", 0.2F, ((this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            var1.func_71001_a(this, 1);
            this.func_70106_y();
         }
      }
   }

   @Override
   protected boolean func_70041_e_() {
      return false;
   }

   @Override
   public float func_70053_R() {
      return 0.0F;
   }

   public void func_70239_b(double var1) {
      this.field_70255_ao = var1;
   }

   public double func_70242_d() {
      return this.field_70255_ao;
   }

   public void func_70240_a(int var1) {
      this.field_70256_ap = var1;
   }

   @Override
   public boolean func_70075_an() {
      return false;
   }

   public void func_70243_d(boolean var1) {
      byte var2 = this.field_70180_af.func_75683_a(16);
      if (var1) {
         this.field_70180_af.func_75692_b(16, (byte)(var2 | 1));
      } else {
         this.field_70180_af.func_75692_b(16, (byte)(var2 & -2));
      }
   }

   public boolean func_70241_g() {
      byte var1 = this.field_70180_af.func_75683_a(16);
      return (var1 & 1) != 0;
   }
}
