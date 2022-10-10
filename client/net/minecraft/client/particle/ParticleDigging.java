package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ParticleDigging extends Particle {
   private final IBlockState field_174847_a;
   private BlockPos field_181019_az;

   protected ParticleDigging(World var1, double var2, double var4, double var6, double var8, double var10, double var12, IBlockState var14) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.field_174847_a = var14;
      this.func_187117_a(Minecraft.func_71410_x().func_175602_ab().func_175023_a().func_178122_a(var14));
      this.field_70545_g = 1.0F;
      this.field_70552_h = 0.6F;
      this.field_70553_i = 0.6F;
      this.field_70551_j = 0.6F;
      this.field_70544_f /= 2.0F;
   }

   public ParticleDigging func_174846_a(BlockPos var1) {
      this.field_181019_az = var1;
      if (this.field_174847_a.func_177230_c() == Blocks.field_196658_i) {
         return this;
      } else {
         this.func_187154_b(var1);
         return this;
      }
   }

   public ParticleDigging func_174845_l() {
      this.field_181019_az = new BlockPos(this.field_187126_f, this.field_187127_g, this.field_187128_h);
      Block var1 = this.field_174847_a.func_177230_c();
      if (var1 == Blocks.field_196658_i) {
         return this;
      } else {
         this.func_187154_b(this.field_181019_az);
         return this;
      }
   }

   protected void func_187154_b(@Nullable BlockPos var1) {
      int var2 = Minecraft.func_71410_x().func_184125_al().func_186724_a(this.field_174847_a, this.field_187122_b, var1, 0);
      this.field_70552_h *= (float)(var2 >> 16 & 255) / 255.0F;
      this.field_70553_i *= (float)(var2 >> 8 & 255) / 255.0F;
      this.field_70551_j *= (float)(var2 & 255) / 255.0F;
   }

   public int func_70537_b() {
      return 1;
   }

   public void func_180434_a(BufferBuilder var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = ((float)this.field_94054_b + this.field_70548_b / 4.0F) / 16.0F;
      float var10 = var9 + 0.015609375F;
      float var11 = ((float)this.field_94055_c + this.field_70549_c / 4.0F) / 16.0F;
      float var12 = var11 + 0.015609375F;
      float var13 = 0.1F * this.field_70544_f;
      if (this.field_187119_C != null) {
         var9 = this.field_187119_C.func_94214_a((double)(this.field_70548_b / 4.0F * 16.0F));
         var10 = this.field_187119_C.func_94214_a((double)((this.field_70548_b + 1.0F) / 4.0F * 16.0F));
         var11 = this.field_187119_C.func_94207_b((double)(this.field_70549_c / 4.0F * 16.0F));
         var12 = this.field_187119_C.func_94207_b((double)((this.field_70549_c + 1.0F) / 4.0F * 16.0F));
      }

      float var14 = (float)(this.field_187123_c + (this.field_187126_f - this.field_187123_c) * (double)var3 - field_70556_an);
      float var15 = (float)(this.field_187124_d + (this.field_187127_g - this.field_187124_d) * (double)var3 - field_70554_ao);
      float var16 = (float)(this.field_187125_e + (this.field_187128_h - this.field_187125_e) * (double)var3 - field_70555_ap);
      int var17 = this.func_189214_a(var3);
      int var18 = var17 >> 16 & '\uffff';
      int var19 = var17 & '\uffff';
      var1.func_181662_b((double)(var14 - var4 * var13 - var7 * var13), (double)(var15 - var5 * var13), (double)(var16 - var6 * var13 - var8 * var13)).func_187315_a((double)var9, (double)var12).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0F).func_187314_a(var18, var19).func_181675_d();
      var1.func_181662_b((double)(var14 - var4 * var13 + var7 * var13), (double)(var15 + var5 * var13), (double)(var16 - var6 * var13 + var8 * var13)).func_187315_a((double)var9, (double)var11).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0F).func_187314_a(var18, var19).func_181675_d();
      var1.func_181662_b((double)(var14 + var4 * var13 + var7 * var13), (double)(var15 + var5 * var13), (double)(var16 + var6 * var13 + var8 * var13)).func_187315_a((double)var10, (double)var11).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0F).func_187314_a(var18, var19).func_181675_d();
      var1.func_181662_b((double)(var14 + var4 * var13 - var7 * var13), (double)(var15 - var5 * var13), (double)(var16 + var6 * var13 - var8 * var13)).func_187315_a((double)var10, (double)var12).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0F).func_187314_a(var18, var19).func_181675_d();
   }

   public int func_189214_a(float var1) {
      int var2 = super.func_189214_a(var1);
      int var3 = 0;
      if (this.field_187122_b.func_175667_e(this.field_181019_az)) {
         var3 = this.field_187122_b.func_175626_b(this.field_181019_az, 0);
      }

      return var2 == 0 ? var3 : var2;
   }

   public static class Factory implements IParticleFactory<BlockParticleData> {
      public Factory() {
         super();
      }

      public Particle func_199234_a(BlockParticleData var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         IBlockState var15 = var1.func_197584_c();
         return !var15.func_196958_f() && var15.func_177230_c() != Blocks.field_196603_bb ? (new ParticleDigging(var2, var3, var5, var7, var9, var11, var13, var15)).func_174845_l() : null;
      }
   }
}
