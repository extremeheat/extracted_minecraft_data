package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ParticleExplosionLarge extends Particle {
   private static final ResourceLocation field_110127_a = new ResourceLocation("textures/entity/explosion.png");
   private static final VertexFormat field_181549_az;
   private int field_70581_a;
   private final int field_70584_aq;
   private final TextureManager field_70583_ar;
   private final float field_70582_as;

   protected ParticleExplosionLarge(TextureManager var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
      super(var2, var3, var5, var7, 0.0D, 0.0D, 0.0D);
      this.field_70583_ar = var1;
      this.field_70584_aq = 6 + this.field_187136_p.nextInt(4);
      float var15 = this.field_187136_p.nextFloat() * 0.6F + 0.4F;
      this.field_70552_h = var15;
      this.field_70553_i = var15;
      this.field_70551_j = var15;
      this.field_70582_as = 1.0F - (float)var9 * 0.5F;
   }

   public void func_180434_a(BufferBuilder var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      int var9 = (int)(((float)this.field_70581_a + var3) * 15.0F / (float)this.field_70584_aq);
      if (var9 <= 15) {
         this.field_70583_ar.func_110577_a(field_110127_a);
         float var10 = (float)(var9 % 4) / 4.0F;
         float var11 = var10 + 0.24975F;
         float var12 = (float)(var9 / 4) / 4.0F;
         float var13 = var12 + 0.24975F;
         float var14 = 2.0F * this.field_70582_as;
         float var15 = (float)(this.field_187123_c + (this.field_187126_f - this.field_187123_c) * (double)var3 - field_70556_an);
         float var16 = (float)(this.field_187124_d + (this.field_187127_g - this.field_187124_d) * (double)var3 - field_70554_ao);
         float var17 = (float)(this.field_187125_e + (this.field_187128_h - this.field_187125_e) * (double)var3 - field_70555_ap);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.func_179140_f();
         RenderHelper.func_74518_a();
         var1.func_181668_a(7, field_181549_az);
         var1.func_181662_b((double)(var15 - var4 * var14 - var7 * var14), (double)(var16 - var5 * var14), (double)(var17 - var6 * var14 - var8 * var14)).func_187315_a((double)var11, (double)var13).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0F).func_187314_a(0, 240).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
         var1.func_181662_b((double)(var15 - var4 * var14 + var7 * var14), (double)(var16 + var5 * var14), (double)(var17 - var6 * var14 + var8 * var14)).func_187315_a((double)var11, (double)var12).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0F).func_187314_a(0, 240).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
         var1.func_181662_b((double)(var15 + var4 * var14 + var7 * var14), (double)(var16 + var5 * var14), (double)(var17 + var6 * var14 + var8 * var14)).func_187315_a((double)var10, (double)var12).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0F).func_187314_a(0, 240).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
         var1.func_181662_b((double)(var15 + var4 * var14 - var7 * var14), (double)(var16 - var5 * var14), (double)(var17 + var6 * var14 - var8 * var14)).func_187315_a((double)var10, (double)var13).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0F).func_187314_a(0, 240).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
         Tessellator.func_178181_a().func_78381_a();
         GlStateManager.func_179145_e();
      }
   }

   public int func_189214_a(float var1) {
      return 61680;
   }

   public void func_189213_a() {
      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      ++this.field_70581_a;
      if (this.field_70581_a == this.field_70584_aq) {
         this.func_187112_i();
      }

   }

   public int func_70537_b() {
      return 3;
   }

   static {
      field_181549_az = (new VertexFormat()).func_181721_a(DefaultVertexFormats.field_181713_m).func_181721_a(DefaultVertexFormats.field_181715_o).func_181721_a(DefaultVertexFormats.field_181714_n).func_181721_a(DefaultVertexFormats.field_181716_p).func_181721_a(DefaultVertexFormats.field_181717_q).func_181721_a(DefaultVertexFormats.field_181718_r);
   }

   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Factory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new ParticleExplosionLarge(Minecraft.func_71410_x().func_110434_K(), var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
