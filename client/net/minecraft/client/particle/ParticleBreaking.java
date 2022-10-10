package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.world.World;

public class ParticleBreaking extends Particle {
   protected ParticleBreaking(World var1, double var2, double var4, double var6, double var8, double var10, double var12, ItemStack var14) {
      this(var1, var2, var4, var6, var14);
      this.field_187129_i *= 0.10000000149011612D;
      this.field_187130_j *= 0.10000000149011612D;
      this.field_187131_k *= 0.10000000149011612D;
      this.field_187129_i += var8;
      this.field_187130_j += var10;
      this.field_187131_k += var12;
   }

   protected ParticleBreaking(World var1, double var2, double var4, double var6, ItemStack var8) {
      super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.func_187117_a(Minecraft.func_71410_x().func_175599_af().func_175037_a().func_199309_a(var8));
      this.field_70552_h = 1.0F;
      this.field_70553_i = 1.0F;
      this.field_70551_j = 1.0F;
      this.field_70545_g = 1.0F;
      this.field_70544_f /= 2.0F;
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

   public static class SnowballFactory implements IParticleFactory<BasicParticleType> {
      public SnowballFactory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new ParticleBreaking(var2, var3, var5, var7, new ItemStack(Items.field_151126_ay));
      }
   }

   public static class SlimeFactory implements IParticleFactory<BasicParticleType> {
      public SlimeFactory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new ParticleBreaking(var2, var3, var5, var7, new ItemStack(Items.field_151123_aH));
      }
   }

   public static class Factory implements IParticleFactory<ItemParticleData> {
      public Factory() {
         super();
      }

      public Particle func_199234_a(ItemParticleData var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new ParticleBreaking(var2, var3, var5, var7, var9, var11, var13, var1.func_197556_c());
      }
   }
}
