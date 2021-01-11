package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemDye;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFirework {
   public static class Factory implements IParticleFactory {
      public Factory() {
         super();
      }

      public EntityFX func_178902_a(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         EntityFirework.SparkFX var16 = new EntityFirework.SparkFX(var2, var3, var5, var7, var9, var11, var13, Minecraft.func_71410_x().field_71452_i);
         var16.func_82338_g(0.99F);
         return var16;
      }
   }

   public static class OverlayFX extends EntityFX {
      protected OverlayFX(World var1, double var2, double var4, double var6) {
         super(var1, var2, var4, var6);
         this.field_70547_e = 4;
      }

      public void func_180434_a(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
         float var9 = 0.25F;
         float var10 = 0.5F;
         float var11 = 0.125F;
         float var12 = 0.375F;
         float var13 = 7.1F * MathHelper.func_76126_a(((float)this.field_70546_d + var3 - 1.0F) * 0.25F * 3.1415927F);
         this.field_82339_as = 0.6F - ((float)this.field_70546_d + var3 - 1.0F) * 0.25F * 0.5F;
         float var14 = (float)(this.field_70169_q + (this.field_70165_t - this.field_70169_q) * (double)var3 - field_70556_an);
         float var15 = (float)(this.field_70167_r + (this.field_70163_u - this.field_70167_r) * (double)var3 - field_70554_ao);
         float var16 = (float)(this.field_70166_s + (this.field_70161_v - this.field_70166_s) * (double)var3 - field_70555_ap);
         int var17 = this.func_70070_b(var3);
         int var18 = var17 >> 16 & '\uffff';
         int var19 = var17 & '\uffff';
         var1.func_181662_b((double)(var14 - var4 * var13 - var7 * var13), (double)(var15 - var5 * var13), (double)(var16 - var6 * var13 - var8 * var13)).func_181673_a(0.5D, 0.375D).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_181671_a(var18, var19).func_181675_d();
         var1.func_181662_b((double)(var14 - var4 * var13 + var7 * var13), (double)(var15 + var5 * var13), (double)(var16 - var6 * var13 + var8 * var13)).func_181673_a(0.5D, 0.125D).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_181671_a(var18, var19).func_181675_d();
         var1.func_181662_b((double)(var14 + var4 * var13 + var7 * var13), (double)(var15 + var5 * var13), (double)(var16 + var6 * var13 + var8 * var13)).func_181673_a(0.25D, 0.125D).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_181671_a(var18, var19).func_181675_d();
         var1.func_181662_b((double)(var14 + var4 * var13 - var7 * var13), (double)(var15 - var5 * var13), (double)(var16 + var6 * var13 - var8 * var13)).func_181673_a(0.25D, 0.375D).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_181671_a(var18, var19).func_181675_d();
      }
   }

   public static class SparkFX extends EntityFX {
      private int field_92049_a = 160;
      private boolean field_92054_ax;
      private boolean field_92048_ay;
      private final EffectRenderer field_92047_az;
      private float field_92050_aA;
      private float field_92051_aB;
      private float field_92052_aC;
      private boolean field_92053_aD;

      public SparkFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12, EffectRenderer var14) {
         super(var1, var2, var4, var6);
         this.field_70159_w = var8;
         this.field_70181_x = var10;
         this.field_70179_y = var12;
         this.field_92047_az = var14;
         this.field_70544_f *= 0.75F;
         this.field_70547_e = 48 + this.field_70146_Z.nextInt(12);
         this.field_70145_X = false;
      }

      public void func_92045_e(boolean var1) {
         this.field_92054_ax = var1;
      }

      public void func_92043_f(boolean var1) {
         this.field_92048_ay = var1;
      }

      public void func_92044_a(int var1) {
         float var2 = (float)((var1 & 16711680) >> 16) / 255.0F;
         float var3 = (float)((var1 & '\uff00') >> 8) / 255.0F;
         float var4 = (float)((var1 & 255) >> 0) / 255.0F;
         float var5 = 1.0F;
         this.func_70538_b(var2 * var5, var3 * var5, var4 * var5);
      }

      public void func_92046_g(int var1) {
         this.field_92050_aA = (float)((var1 & 16711680) >> 16) / 255.0F;
         this.field_92051_aB = (float)((var1 & '\uff00') >> 8) / 255.0F;
         this.field_92052_aC = (float)((var1 & 255) >> 0) / 255.0F;
         this.field_92053_aD = true;
      }

      public AxisAlignedBB func_70046_E() {
         return null;
      }

      public boolean func_70104_M() {
         return false;
      }

      public void func_180434_a(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
         if (!this.field_92048_ay || this.field_70546_d < this.field_70547_e / 3 || (this.field_70546_d + this.field_70547_e) / 3 % 2 == 0) {
            super.func_180434_a(var1, var2, var3, var4, var5, var6, var7, var8);
         }

      }

      public void func_70071_h_() {
         this.field_70169_q = this.field_70165_t;
         this.field_70167_r = this.field_70163_u;
         this.field_70166_s = this.field_70161_v;
         if (this.field_70546_d++ >= this.field_70547_e) {
            this.func_70106_y();
         }

         if (this.field_70546_d > this.field_70547_e / 2) {
            this.func_82338_g(1.0F - ((float)this.field_70546_d - (float)(this.field_70547_e / 2)) / (float)this.field_70547_e);
            if (this.field_92053_aD) {
               this.field_70552_h += (this.field_92050_aA - this.field_70552_h) * 0.2F;
               this.field_70553_i += (this.field_92051_aB - this.field_70553_i) * 0.2F;
               this.field_70551_j += (this.field_92052_aC - this.field_70551_j) * 0.2F;
            }
         }

         this.func_70536_a(this.field_92049_a + (7 - this.field_70546_d * 8 / this.field_70547_e));
         this.field_70181_x -= 0.004D;
         this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
         this.field_70159_w *= 0.9100000262260437D;
         this.field_70181_x *= 0.9100000262260437D;
         this.field_70179_y *= 0.9100000262260437D;
         if (this.field_70122_E) {
            this.field_70159_w *= 0.699999988079071D;
            this.field_70179_y *= 0.699999988079071D;
         }

         if (this.field_92054_ax && this.field_70546_d < this.field_70547_e / 2 && (this.field_70546_d + this.field_70547_e) % 2 == 0) {
            EntityFirework.SparkFX var1 = new EntityFirework.SparkFX(this.field_70170_p, this.field_70165_t, this.field_70163_u, this.field_70161_v, 0.0D, 0.0D, 0.0D, this.field_92047_az);
            var1.func_82338_g(0.99F);
            var1.func_70538_b(this.field_70552_h, this.field_70553_i, this.field_70551_j);
            var1.field_70546_d = var1.field_70547_e / 2;
            if (this.field_92053_aD) {
               var1.field_92053_aD = true;
               var1.field_92050_aA = this.field_92050_aA;
               var1.field_92051_aB = this.field_92051_aB;
               var1.field_92052_aC = this.field_92052_aC;
            }

            var1.field_92048_ay = this.field_92048_ay;
            this.field_92047_az.func_78873_a(var1);
         }

      }

      public int func_70070_b(float var1) {
         return 15728880;
      }

      public float func_70013_c(float var1) {
         return 1.0F;
      }
   }

   public static class StarterFX extends EntityFX {
      private int field_92042_ax;
      private final EffectRenderer field_92040_ay;
      private NBTTagList field_92039_az;
      boolean field_92041_a;

      public StarterFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12, EffectRenderer var14, NBTTagCompound var15) {
         super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
         this.field_70159_w = var8;
         this.field_70181_x = var10;
         this.field_70179_y = var12;
         this.field_92040_ay = var14;
         this.field_70547_e = 8;
         if (var15 != null) {
            this.field_92039_az = var15.func_150295_c("Explosions", 10);
            if (this.field_92039_az.func_74745_c() == 0) {
               this.field_92039_az = null;
            } else {
               this.field_70547_e = this.field_92039_az.func_74745_c() * 2 - 1;

               for(int var16 = 0; var16 < this.field_92039_az.func_74745_c(); ++var16) {
                  NBTTagCompound var17 = this.field_92039_az.func_150305_b(var16);
                  if (var17.func_74767_n("Flicker")) {
                     this.field_92041_a = true;
                     this.field_70547_e += 15;
                     break;
                  }
               }
            }
         }

      }

      public void func_180434_a(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      }

      public void func_70071_h_() {
         boolean var1;
         if (this.field_92042_ax == 0 && this.field_92039_az != null) {
            var1 = this.func_92037_i();
            boolean var2 = false;
            if (this.field_92039_az.func_74745_c() >= 3) {
               var2 = true;
            } else {
               for(int var3 = 0; var3 < this.field_92039_az.func_74745_c(); ++var3) {
                  NBTTagCompound var4 = this.field_92039_az.func_150305_b(var3);
                  if (var4.func_74771_c("Type") == 1) {
                     var2 = true;
                     break;
                  }
               }
            }

            String var15 = "fireworks." + (var2 ? "largeBlast" : "blast") + (var1 ? "_far" : "");
            this.field_70170_p.func_72980_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, var15, 20.0F, 0.95F + this.field_70146_Z.nextFloat() * 0.1F, true);
         }

         if (this.field_92042_ax % 2 == 0 && this.field_92039_az != null && this.field_92042_ax / 2 < this.field_92039_az.func_74745_c()) {
            int var13 = this.field_92042_ax / 2;
            NBTTagCompound var14 = this.field_92039_az.func_150305_b(var13);
            byte var17 = var14.func_74771_c("Type");
            boolean var18 = var14.func_74767_n("Trail");
            boolean var5 = var14.func_74767_n("Flicker");
            int[] var6 = var14.func_74759_k("Colors");
            int[] var7 = var14.func_74759_k("FadeColors");
            if (var6.length == 0) {
               var6 = new int[]{ItemDye.field_150922_c[0]};
            }

            if (var17 == 1) {
               this.func_92035_a(0.5D, 4, var6, var7, var18, var5);
            } else if (var17 == 2) {
               this.func_92038_a(0.5D, new double[][]{{0.0D, 1.0D}, {0.3455D, 0.309D}, {0.9511D, 0.309D}, {0.3795918367346939D, -0.12653061224489795D}, {0.6122448979591837D, -0.8040816326530612D}, {0.0D, -0.35918367346938773D}}, var6, var7, var18, var5, false);
            } else if (var17 == 3) {
               this.func_92038_a(0.5D, new double[][]{{0.0D, 0.2D}, {0.2D, 0.2D}, {0.2D, 0.6D}, {0.6D, 0.6D}, {0.6D, 0.2D}, {0.2D, 0.2D}, {0.2D, 0.0D}, {0.4D, 0.0D}, {0.4D, -0.6D}, {0.2D, -0.6D}, {0.2D, -0.4D}, {0.0D, -0.4D}}, var6, var7, var18, var5, true);
            } else if (var17 == 4) {
               this.func_92036_a(var6, var7, var18, var5);
            } else {
               this.func_92035_a(0.25D, 2, var6, var7, var18, var5);
            }

            int var8 = var6[0];
            float var9 = (float)((var8 & 16711680) >> 16) / 255.0F;
            float var10 = (float)((var8 & '\uff00') >> 8) / 255.0F;
            float var11 = (float)((var8 & 255) >> 0) / 255.0F;
            EntityFirework.OverlayFX var12 = new EntityFirework.OverlayFX(this.field_70170_p, this.field_70165_t, this.field_70163_u, this.field_70161_v);
            var12.func_70538_b(var9, var10, var11);
            this.field_92040_ay.func_78873_a(var12);
         }

         ++this.field_92042_ax;
         if (this.field_92042_ax > this.field_70547_e) {
            if (this.field_92041_a) {
               var1 = this.func_92037_i();
               String var16 = "fireworks." + (var1 ? "twinkle_far" : "twinkle");
               this.field_70170_p.func_72980_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, var16, 20.0F, 0.9F + this.field_70146_Z.nextFloat() * 0.15F, true);
            }

            this.func_70106_y();
         }

      }

      private boolean func_92037_i() {
         Minecraft var1 = Minecraft.func_71410_x();
         return var1 == null || var1.func_175606_aa() == null || var1.func_175606_aa().func_70092_e(this.field_70165_t, this.field_70163_u, this.field_70161_v) >= 256.0D;
      }

      private void func_92034_a(double var1, double var3, double var5, double var7, double var9, double var11, int[] var13, int[] var14, boolean var15, boolean var16) {
         EntityFirework.SparkFX var17 = new EntityFirework.SparkFX(this.field_70170_p, var1, var3, var5, var7, var9, var11, this.field_92040_ay);
         var17.func_82338_g(0.99F);
         var17.func_92045_e(var15);
         var17.func_92043_f(var16);
         int var18 = this.field_70146_Z.nextInt(var13.length);
         var17.func_92044_a(var13[var18]);
         if (var14 != null && var14.length > 0) {
            var17.func_92046_g(var14[this.field_70146_Z.nextInt(var14.length)]);
         }

         this.field_92040_ay.func_78873_a(var17);
      }

      private void func_92035_a(double var1, int var3, int[] var4, int[] var5, boolean var6, boolean var7) {
         double var8 = this.field_70165_t;
         double var10 = this.field_70163_u;
         double var12 = this.field_70161_v;

         for(int var14 = -var3; var14 <= var3; ++var14) {
            for(int var15 = -var3; var15 <= var3; ++var15) {
               for(int var16 = -var3; var16 <= var3; ++var16) {
                  double var17 = (double)var15 + (this.field_70146_Z.nextDouble() - this.field_70146_Z.nextDouble()) * 0.5D;
                  double var19 = (double)var14 + (this.field_70146_Z.nextDouble() - this.field_70146_Z.nextDouble()) * 0.5D;
                  double var21 = (double)var16 + (this.field_70146_Z.nextDouble() - this.field_70146_Z.nextDouble()) * 0.5D;
                  double var23 = (double)MathHelper.func_76133_a(var17 * var17 + var19 * var19 + var21 * var21) / var1 + this.field_70146_Z.nextGaussian() * 0.05D;
                  this.func_92034_a(var8, var10, var12, var17 / var23, var19 / var23, var21 / var23, var4, var5, var6, var7);
                  if (var14 != -var3 && var14 != var3 && var15 != -var3 && var15 != var3) {
                     var16 += var3 * 2 - 1;
                  }
               }
            }
         }

      }

      private void func_92038_a(double var1, double[][] var3, int[] var4, int[] var5, boolean var6, boolean var7, boolean var8) {
         double var9 = var3[0][0];
         double var11 = var3[0][1];
         this.func_92034_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, var9 * var1, var11 * var1, 0.0D, var4, var5, var6, var7);
         float var13 = this.field_70146_Z.nextFloat() * 3.1415927F;
         double var14 = var8 ? 0.034D : 0.34D;

         for(int var16 = 0; var16 < 3; ++var16) {
            double var17 = (double)var13 + (double)((float)var16 * 3.1415927F) * var14;
            double var19 = var9;
            double var21 = var11;

            for(int var23 = 1; var23 < var3.length; ++var23) {
               double var24 = var3[var23][0];
               double var26 = var3[var23][1];

               for(double var28 = 0.25D; var28 <= 1.0D; var28 += 0.25D) {
                  double var30 = (var19 + (var24 - var19) * var28) * var1;
                  double var32 = (var21 + (var26 - var21) * var28) * var1;
                  double var34 = var30 * Math.sin(var17);
                  var30 *= Math.cos(var17);

                  for(double var36 = -1.0D; var36 <= 1.0D; var36 += 2.0D) {
                     this.func_92034_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, var30 * var36, var32, var34 * var36, var4, var5, var6, var7);
                  }
               }

               var19 = var24;
               var21 = var26;
            }
         }

      }

      private void func_92036_a(int[] var1, int[] var2, boolean var3, boolean var4) {
         double var5 = this.field_70146_Z.nextGaussian() * 0.05D;
         double var7 = this.field_70146_Z.nextGaussian() * 0.05D;

         for(int var9 = 0; var9 < 70; ++var9) {
            double var10 = this.field_70159_w * 0.5D + this.field_70146_Z.nextGaussian() * 0.15D + var5;
            double var12 = this.field_70179_y * 0.5D + this.field_70146_Z.nextGaussian() * 0.15D + var7;
            double var14 = this.field_70181_x * 0.5D + this.field_70146_Z.nextDouble() * 0.5D;
            this.func_92034_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, var10, var14, var12, var1, var2, var3, var4);
         }

      }

      public int func_70537_b() {
         return 0;
      }
   }
}
