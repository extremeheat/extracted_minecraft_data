package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleMobAppearance extends Particle {
   private EntityLivingBase field_174844_a;

   protected ParticleMobAppearance(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.field_70552_h = 1.0F;
      this.field_70553_i = 1.0F;
      this.field_70551_j = 1.0F;
      this.field_187129_i = 0.0D;
      this.field_187130_j = 0.0D;
      this.field_187131_k = 0.0D;
      this.field_70545_g = 0.0F;
      this.field_70547_e = 30;
   }

   public int func_70537_b() {
      return 3;
   }

   public void func_189213_a() {
      super.func_189213_a();
      if (this.field_174844_a == null) {
         EntityElderGuardian var1 = new EntityElderGuardian(this.field_187122_b);
         var1.func_190767_di();
         this.field_174844_a = var1;
      }

   }

   public void func_180434_a(BufferBuilder var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (this.field_174844_a != null) {
         RenderManager var9 = Minecraft.func_71410_x().func_175598_ae();
         var9.func_178628_a(Particle.field_70556_an, Particle.field_70554_ao, Particle.field_70555_ap);
         float var10 = 0.42553192F;
         float var11 = ((float)this.field_70546_d + var3) / (float)this.field_70547_e;
         GlStateManager.func_179132_a(true);
         GlStateManager.func_179147_l();
         GlStateManager.func_179126_j();
         GlStateManager.func_187401_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
         float var12 = 240.0F;
         OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, 240.0F, 240.0F);
         GlStateManager.func_179094_E();
         float var13 = 0.05F + 0.5F * MathHelper.func_76126_a(var11 * 3.1415927F);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, var13);
         GlStateManager.func_179109_b(0.0F, 1.8F, 0.0F);
         GlStateManager.func_179114_b(180.0F - var2.field_70177_z, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(60.0F - 150.0F * var11 - var2.field_70125_A, 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179109_b(0.0F, -0.4F, -1.5F);
         GlStateManager.func_179152_a(0.42553192F, 0.42553192F, 0.42553192F);
         this.field_174844_a.field_70177_z = 0.0F;
         this.field_174844_a.field_70759_as = 0.0F;
         this.field_174844_a.field_70126_B = 0.0F;
         this.field_174844_a.field_70758_at = 0.0F;
         var9.func_188391_a(this.field_174844_a, 0.0D, 0.0D, 0.0D, 0.0F, var3, false);
         GlStateManager.func_179121_F();
         GlStateManager.func_179126_j();
      }
   }

   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Factory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new ParticleMobAppearance(var2, var3, var5, var7);
      }
   }
}
