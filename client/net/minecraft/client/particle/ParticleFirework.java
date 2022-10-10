package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemFireworkRocket;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleFirework {
   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Factory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         ParticleFirework.Spark var15 = new ParticleFirework.Spark(var2, var3, var5, var7, var9, var11, var13, Minecraft.func_71410_x().field_71452_i);
         var15.func_82338_g(0.99F);
         return var15;
      }
   }

   public static class Overlay extends Particle {
      protected Overlay(World var1, double var2, double var4, double var6) {
         super(var1, var2, var4, var6);
         this.field_70547_e = 4;
      }

      public void func_180434_a(BufferBuilder var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
         float var9 = 0.25F;
         float var10 = 0.5F;
         float var11 = 0.125F;
         float var12 = 0.375F;
         float var13 = 7.1F * MathHelper.func_76126_a(((float)this.field_70546_d + var3 - 1.0F) * 0.25F * 3.1415927F);
         this.func_82338_g(0.6F - ((float)this.field_70546_d + var3 - 1.0F) * 0.25F * 0.5F);
         float var14 = (float)(this.field_187123_c + (this.field_187126_f - this.field_187123_c) * (double)var3 - field_70556_an);
         float var15 = (float)(this.field_187124_d + (this.field_187127_g - this.field_187124_d) * (double)var3 - field_70554_ao);
         float var16 = (float)(this.field_187125_e + (this.field_187128_h - this.field_187125_e) * (double)var3 - field_70555_ap);
         int var17 = this.func_189214_a(var3);
         int var18 = var17 >> 16 & '\uffff';
         int var19 = var17 & '\uffff';
         var1.func_181662_b((double)(var14 - var4 * var13 - var7 * var13), (double)(var15 - var5 * var13), (double)(var16 - var6 * var13 - var8 * var13)).func_187315_a(0.5D, 0.375D).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_187314_a(var18, var19).func_181675_d();
         var1.func_181662_b((double)(var14 - var4 * var13 + var7 * var13), (double)(var15 + var5 * var13), (double)(var16 - var6 * var13 + var8 * var13)).func_187315_a(0.5D, 0.125D).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_187314_a(var18, var19).func_181675_d();
         var1.func_181662_b((double)(var14 + var4 * var13 + var7 * var13), (double)(var15 + var5 * var13), (double)(var16 + var6 * var13 + var8 * var13)).func_187315_a(0.25D, 0.125D).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_187314_a(var18, var19).func_181675_d();
         var1.func_181662_b((double)(var14 + var4 * var13 - var7 * var13), (double)(var15 - var5 * var13), (double)(var16 + var6 * var13 - var8 * var13)).func_187315_a(0.25D, 0.375D).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_187314_a(var18, var19).func_181675_d();
      }
   }

   public static class Spark extends ParticleSimpleAnimated {
      private boolean field_92054_ax;
      private boolean field_92048_ay;
      private final ParticleManager field_92047_az;
      private float field_92050_aA;
      private float field_92051_aB;
      private float field_92052_aC;
      private boolean field_92053_aD;

      public Spark(World var1, double var2, double var4, double var6, double var8, double var10, double var12, ParticleManager var14) {
         super(var1, var2, var4, var6, 160, 8, -0.004F);
         this.field_187129_i = var8;
         this.field_187130_j = var10;
         this.field_187131_k = var12;
         this.field_92047_az = var14;
         this.field_70544_f *= 0.75F;
         this.field_70547_e = 48 + this.field_187136_p.nextInt(12);
      }

      public void func_92045_e(boolean var1) {
         this.field_92054_ax = var1;
      }

      public void func_92043_f(boolean var1) {
         this.field_92048_ay = var1;
      }

      public boolean func_187111_c() {
         return true;
      }

      public void func_180434_a(BufferBuilder var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
         if (!this.field_92048_ay || this.field_70546_d < this.field_70547_e / 3 || (this.field_70546_d + this.field_70547_e) / 3 % 2 == 0) {
            super.func_180434_a(var1, var2, var3, var4, var5, var6, var7, var8);
         }

      }

      public void func_189213_a() {
         super.func_189213_a();
         if (this.field_92054_ax && this.field_70546_d < this.field_70547_e / 2 && (this.field_70546_d + this.field_70547_e) % 2 == 0) {
            ParticleFirework.Spark var1 = new ParticleFirework.Spark(this.field_187122_b, this.field_187126_f, this.field_187127_g, this.field_187128_h, 0.0D, 0.0D, 0.0D, this.field_92047_az);
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
   }

   public static class Starter extends Particle {
      private int field_92042_ax;
      private final ParticleManager field_92040_ay;
      private NBTTagList field_92039_az;
      private boolean field_92041_a;

      public Starter(World var1, double var2, double var4, double var6, double var8, double var10, double var12, ParticleManager var14, @Nullable NBTTagCompound var15) {
         super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
         this.field_187129_i = var8;
         this.field_187130_j = var10;
         this.field_187131_k = var12;
         this.field_92040_ay = var14;
         this.field_70547_e = 8;
         if (var15 != null) {
            this.field_92039_az = var15.func_150295_c("Explosions", 10);
            if (this.field_92039_az.isEmpty()) {
               this.field_92039_az = null;
            } else {
               this.field_70547_e = this.field_92039_az.size() * 2 - 1;

               for(int var16 = 0; var16 < this.field_92039_az.size(); ++var16) {
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

      public void func_180434_a(BufferBuilder var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      }

      public void func_189213_a() {
         boolean var1;
         if (this.field_92042_ax == 0 && this.field_92039_az != null) {
            var1 = this.func_92037_i();
            boolean var2 = false;
            if (this.field_92039_az.size() >= 3) {
               var2 = true;
            } else {
               for(int var3 = 0; var3 < this.field_92039_az.size(); ++var3) {
                  NBTTagCompound var4 = this.field_92039_az.func_150305_b(var3);
                  if (ItemFireworkRocket.Shape.func_196070_a(var4.func_74771_c("Type")) == ItemFireworkRocket.Shape.LARGE_BALL) {
                     var2 = true;
                     break;
                  }
               }
            }

            SoundEvent var15;
            if (var2) {
               var15 = var1 ? SoundEvents.field_187628_bn : SoundEvents.field_187625_bm;
            } else {
               var15 = var1 ? SoundEvents.field_187622_bl : SoundEvents.field_187619_bk;
            }

            this.field_187122_b.func_184134_a(this.field_187126_f, this.field_187127_g, this.field_187128_h, var15, SoundCategory.AMBIENT, 20.0F, 0.95F + this.field_187136_p.nextFloat() * 0.1F, true);
         }

         if (this.field_92042_ax % 2 == 0 && this.field_92039_az != null && this.field_92042_ax / 2 < this.field_92039_az.size()) {
            int var13 = this.field_92042_ax / 2;
            NBTTagCompound var14 = this.field_92039_az.func_150305_b(var13);
            ItemFireworkRocket.Shape var18 = ItemFireworkRocket.Shape.func_196070_a(var14.func_74771_c("Type"));
            boolean var17 = var14.func_74767_n("Trail");
            boolean var5 = var14.func_74767_n("Flicker");
            int[] var6 = var14.func_74759_k("Colors");
            int[] var7 = var14.func_74759_k("FadeColors");
            if (var6.length == 0) {
               var6 = new int[]{EnumDyeColor.BLACK.func_196060_f()};
            }

            switch(var18) {
            case SMALL_BALL:
            default:
               this.func_92035_a(0.25D, 2, var6, var7, var17, var5);
               break;
            case LARGE_BALL:
               this.func_92035_a(0.5D, 4, var6, var7, var17, var5);
               break;
            case STAR:
               this.func_92038_a(0.5D, new double[][]{{0.0D, 1.0D}, {0.3455D, 0.309D}, {0.9511D, 0.309D}, {0.3795918367346939D, -0.12653061224489795D}, {0.6122448979591837D, -0.8040816326530612D}, {0.0D, -0.35918367346938773D}}, var6, var7, var17, var5, false);
               break;
            case CREEPER:
               this.func_92038_a(0.5D, new double[][]{{0.0D, 0.2D}, {0.2D, 0.2D}, {0.2D, 0.6D}, {0.6D, 0.6D}, {0.6D, 0.2D}, {0.2D, 0.2D}, {0.2D, 0.0D}, {0.4D, 0.0D}, {0.4D, -0.6D}, {0.2D, -0.6D}, {0.2D, -0.4D}, {0.0D, -0.4D}}, var6, var7, var17, var5, true);
               break;
            case BURST:
               this.func_92036_a(var6, var7, var17, var5);
            }

            int var8 = var6[0];
            float var9 = (float)((var8 & 16711680) >> 16) / 255.0F;
            float var10 = (float)((var8 & '\uff00') >> 8) / 255.0F;
            float var11 = (float)((var8 & 255) >> 0) / 255.0F;
            ParticleFirework.Overlay var12 = new ParticleFirework.Overlay(this.field_187122_b, this.field_187126_f, this.field_187127_g, this.field_187128_h);
            var12.func_70538_b(var9, var10, var11);
            this.field_92040_ay.func_78873_a(var12);
         }

         ++this.field_92042_ax;
         if (this.field_92042_ax > this.field_70547_e) {
            if (this.field_92041_a) {
               var1 = this.func_92037_i();
               SoundEvent var16 = var1 ? SoundEvents.field_187640_br : SoundEvents.field_187637_bq;
               this.field_187122_b.func_184134_a(this.field_187126_f, this.field_187127_g, this.field_187128_h, var16, SoundCategory.AMBIENT, 20.0F, 0.9F + this.field_187136_p.nextFloat() * 0.15F, true);
            }

            this.func_187112_i();
         }

      }

      private boolean func_92037_i() {
         Minecraft var1 = Minecraft.func_71410_x();
         return var1.func_175606_aa() == null || var1.func_175606_aa().func_70092_e(this.field_187126_f, this.field_187127_g, this.field_187128_h) >= 256.0D;
      }

      private void func_92034_a(double var1, double var3, double var5, double var7, double var9, double var11, int[] var13, int[] var14, boolean var15, boolean var16) {
         ParticleFirework.Spark var17 = new ParticleFirework.Spark(this.field_187122_b, var1, var3, var5, var7, var9, var11, this.field_92040_ay);
         var17.func_82338_g(0.99F);
         var17.func_92045_e(var15);
         var17.func_92043_f(var16);
         int var18 = this.field_187136_p.nextInt(var13.length);
         var17.func_187146_c(var13[var18]);
         if (var14.length > 0) {
            var17.func_187145_d(var14[this.field_187136_p.nextInt(var14.length)]);
         }

         this.field_92040_ay.func_78873_a(var17);
      }

      private void func_92035_a(double var1, int var3, int[] var4, int[] var5, boolean var6, boolean var7) {
         double var8 = this.field_187126_f;
         double var10 = this.field_187127_g;
         double var12 = this.field_187128_h;

         for(int var14 = -var3; var14 <= var3; ++var14) {
            for(int var15 = -var3; var15 <= var3; ++var15) {
               for(int var16 = -var3; var16 <= var3; ++var16) {
                  double var17 = (double)var15 + (this.field_187136_p.nextDouble() - this.field_187136_p.nextDouble()) * 0.5D;
                  double var19 = (double)var14 + (this.field_187136_p.nextDouble() - this.field_187136_p.nextDouble()) * 0.5D;
                  double var21 = (double)var16 + (this.field_187136_p.nextDouble() - this.field_187136_p.nextDouble()) * 0.5D;
                  double var23 = (double)MathHelper.func_76133_a(var17 * var17 + var19 * var19 + var21 * var21) / var1 + this.field_187136_p.nextGaussian() * 0.05D;
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
         this.func_92034_a(this.field_187126_f, this.field_187127_g, this.field_187128_h, var9 * var1, var11 * var1, 0.0D, var4, var5, var6, var7);
         float var13 = this.field_187136_p.nextFloat() * 3.1415927F;
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
                     this.func_92034_a(this.field_187126_f, this.field_187127_g, this.field_187128_h, var30 * var36, var32, var34 * var36, var4, var5, var6, var7);
                  }
               }

               var19 = var24;
               var21 = var26;
            }
         }

      }

      private void func_92036_a(int[] var1, int[] var2, boolean var3, boolean var4) {
         double var5 = this.field_187136_p.nextGaussian() * 0.05D;
         double var7 = this.field_187136_p.nextGaussian() * 0.05D;

         for(int var9 = 0; var9 < 70; ++var9) {
            double var10 = this.field_187129_i * 0.5D + this.field_187136_p.nextGaussian() * 0.15D + var5;
            double var12 = this.field_187131_k * 0.5D + this.field_187136_p.nextGaussian() * 0.15D + var7;
            double var14 = this.field_187130_j * 0.5D + this.field_187136_p.nextDouble() * 0.5D;
            this.func_92034_a(this.field_187126_f, this.field_187127_g, this.field_187128_h, var10, var14, var12, var1, var2, var3, var4);
         }

      }

      public int func_70537_b() {
         return 0;
      }
   }
}
