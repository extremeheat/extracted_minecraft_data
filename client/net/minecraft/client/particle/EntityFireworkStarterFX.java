package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFireworkStarterFX extends EntityFX {
   private int field_92042_ax;
   private final EffectRenderer field_92040_ay;
   private NBTTagList field_92039_az;
   boolean field_92041_a;

   public EntityFireworkStarterFX(
      World var1, double var2, double var4, double var6, double var8, double var10, double var12, EffectRenderer var14, NBTTagCompound var15
   ) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
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

   @Override
   public void func_70539_a(Tessellator var1, float var2, float var3, float var4, float var5, float var6, float var7) {
   }

   @Override
   public void func_70071_h_() {
      if (this.field_92042_ax == 0 && this.field_92039_az != null) {
         boolean var1 = this.func_92037_i();
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

         String var17 = "fireworks." + (var2 ? "largeBlast" : "blast") + (var1 ? "_far" : "");
         this.field_70170_p
            .func_72980_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, var17, 20.0F, 0.95F + this.field_70146_Z.nextFloat() * 0.1F, true);
      }

      if (this.field_92042_ax % 2 == 0 && this.field_92039_az != null && this.field_92042_ax / 2 < this.field_92039_az.func_74745_c()) {
         int var13 = this.field_92042_ax / 2;
         NBTTagCompound var15 = this.field_92039_az.func_150305_b(var13);
         byte var18 = var15.func_74771_c("Type");
         boolean var19 = var15.func_74767_n("Trail");
         boolean var5 = var15.func_74767_n("Flicker");
         int[] var6 = var15.func_74759_k("Colors");
         int[] var7 = var15.func_74759_k("FadeColors");
         if (var18 == 1) {
            this.func_92035_a(0.5, 4, var6, var7, var19, var5);
         } else if (var18 == 2) {
            this.func_92038_a(
               0.5,
               new double[][]{
                  {0.0, 1.0},
                  {0.3455, 0.309},
                  {0.9511, 0.309},
                  {0.3795918367346939, -0.12653061224489795},
                  {0.6122448979591837, -0.8040816326530612},
                  {0.0, -0.35918367346938773}
               },
               var6,
               var7,
               var19,
               var5,
               false
            );
         } else if (var18 == 3) {
            this.func_92038_a(
               0.5,
               new double[][]{
                  {0.0, 0.2},
                  {0.2, 0.2},
                  {0.2, 0.6},
                  {0.6, 0.6},
                  {0.6, 0.2},
                  {0.2, 0.2},
                  {0.2, 0.0},
                  {0.4, 0.0},
                  {0.4, -0.6},
                  {0.2, -0.6},
                  {0.2, -0.4},
                  {0.0, -0.4}
               },
               var6,
               var7,
               var19,
               var5,
               true
            );
         } else if (var18 == 4) {
            this.func_92036_a(var6, var7, var19, var5);
         } else {
            this.func_92035_a(0.25, 2, var6, var7, var19, var5);
         }

         int var8 = var6[0];
         float var9 = (float)((var8 & 0xFF0000) >> 16) / 255.0F;
         float var10 = (float)((var8 & 0xFF00) >> 8) / 255.0F;
         float var11 = (float)((var8 & 0xFF) >> 0) / 255.0F;
         EntityFireworkOverlayFX var12 = new EntityFireworkOverlayFX(this.field_70170_p, this.field_70165_t, this.field_70163_u, this.field_70161_v);
         var12.func_70538_b(var9, var10, var11);
         this.field_92040_ay.func_78873_a(var12);
      }

      ++this.field_92042_ax;
      if (this.field_92042_ax > this.field_70547_e) {
         if (this.field_92041_a) {
            boolean var14 = this.func_92037_i();
            String var16 = "fireworks." + (var14 ? "twinkle_far" : "twinkle");
            this.field_70170_p
               .func_72980_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, var16, 20.0F, 0.9F + this.field_70146_Z.nextFloat() * 0.15F, true);
         }

         this.func_70106_y();
      }
   }

   private boolean func_92037_i() {
      Minecraft var1 = Minecraft.func_71410_x();
      return var1 == null
         || var1.field_71451_h == null
         || !(var1.field_71451_h.func_70092_e(this.field_70165_t, this.field_70163_u, this.field_70161_v) < 256.0);
   }

   private void func_92034_a(
      double var1, double var3, double var5, double var7, double var9, double var11, int[] var13, int[] var14, boolean var15, boolean var16
   ) {
      EntityFireworkSparkFX var17 = new EntityFireworkSparkFX(this.field_70170_p, var1, var3, var5, var7, var9, var11, this.field_92040_ay);
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
               double var17 = (double)var15 + (this.field_70146_Z.nextDouble() - this.field_70146_Z.nextDouble()) * 0.5;
               double var19 = (double)var14 + (this.field_70146_Z.nextDouble() - this.field_70146_Z.nextDouble()) * 0.5;
               double var21 = (double)var16 + (this.field_70146_Z.nextDouble() - this.field_70146_Z.nextDouble()) * 0.5;
               double var23 = (double)MathHelper.func_76133_a(var17 * var17 + var19 * var19 + var21 * var21) / var1 + this.field_70146_Z.nextGaussian() * 0.05;
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
      this.func_92034_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, var9 * var1, var11 * var1, 0.0, var4, var5, var6, var7);
      float var13 = this.field_70146_Z.nextFloat() * 3.1415927F;
      double var14 = var8 ? 0.034 : 0.34;

      for(int var16 = 0; var16 < 3; ++var16) {
         double var17 = (double)var13 + (double)((float)var16 * 3.1415927F) * var14;
         double var19 = var9;
         double var21 = var11;

         for(int var23 = 1; var23 < var3.length; ++var23) {
            double var24 = var3[var23][0];
            double var26 = var3[var23][1];

            for(double var28 = 0.25; var28 <= 1.0; var28 += 0.25) {
               double var30 = (var19 + (var24 - var19) * var28) * var1;
               double var32 = (var21 + (var26 - var21) * var28) * var1;
               double var34 = var30 * Math.sin(var17);
               var30 *= Math.cos(var17);

               for(double var36 = -1.0; var36 <= 1.0; var36 += 2.0) {
                  this.func_92034_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, var30 * var36, var32, var34 * var36, var4, var5, var6, var7);
               }
            }

            var19 = var24;
            var21 = var26;
         }
      }
   }

   private void func_92036_a(int[] var1, int[] var2, boolean var3, boolean var4) {
      double var5 = this.field_70146_Z.nextGaussian() * 0.05;
      double var7 = this.field_70146_Z.nextGaussian() * 0.05;

      for(int var9 = 0; var9 < 70; ++var9) {
         double var10 = this.field_70159_w * 0.5 + this.field_70146_Z.nextGaussian() * 0.15 + var5;
         double var12 = this.field_70179_y * 0.5 + this.field_70146_Z.nextGaussian() * 0.15 + var7;
         double var14 = this.field_70181_x * 0.5 + this.field_70146_Z.nextDouble() * 0.5;
         this.func_92034_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, var10, var14, var12, var1, var2, var3, var4);
      }
   }

   @Override
   public int func_70537_b() {
      return 0;
   }
}
