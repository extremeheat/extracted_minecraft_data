package net.minecraft.client.particle;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.level.Level;

public class MobAppearanceParticle extends Particle {
   private LivingEntity displayEntity;

   private MobAppearanceParticle(Level var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
      this.gravity = 0.0F;
      this.lifetime = 30;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.CUSTOM;
   }

   public void tick() {
      super.tick();
      if (this.displayEntity == null) {
         ElderGuardian var1 = (ElderGuardian)EntityType.ELDER_GUARDIAN.create(this.level);
         var1.setGhost();
         this.displayEntity = var1;
      }

   }

   public void render(BufferBuilder var1, Camera var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (this.displayEntity != null) {
         EntityRenderDispatcher var9 = Minecraft.getInstance().getEntityRenderDispatcher();
         var9.setPosition(Particle.xOff, Particle.yOff, Particle.zOff);
         float var10 = 1.0F / ElderGuardian.ELDER_SIZE_SCALE;
         float var11 = ((float)this.age + var3) / (float)this.lifetime;
         GlStateManager.depthMask(true);
         GlStateManager.enableBlend();
         GlStateManager.enableDepthTest();
         GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
         float var12 = 240.0F;
         GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0F, 240.0F);
         GlStateManager.pushMatrix();
         float var13 = 0.05F + 0.5F * Mth.sin(var11 * 3.1415927F);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, var13);
         GlStateManager.translatef(0.0F, 1.8F, 0.0F);
         GlStateManager.rotatef(180.0F - var2.getYRot(), 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(60.0F - 150.0F * var11 - var2.getXRot(), 1.0F, 0.0F, 0.0F);
         GlStateManager.translatef(0.0F, -0.4F, -1.5F);
         GlStateManager.scalef(var10, var10, var10);
         this.displayEntity.yRot = 0.0F;
         this.displayEntity.yHeadRot = 0.0F;
         this.displayEntity.yRotO = 0.0F;
         this.displayEntity.yHeadRotO = 0.0F;
         var9.render(this.displayEntity, 0.0D, 0.0D, 0.0D, 0.0F, var3, false);
         GlStateManager.popMatrix();
         GlStateManager.enableDepthTest();
      }
   }

   // $FF: synthetic method
   MobAppearanceParticle(Level var1, double var2, double var4, double var6, Object var8) {
      this(var1, var2, var4, var6);
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Provider() {
         super();
      }

      public Particle createParticle(SimpleParticleType var1, Level var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new MobAppearanceParticle(var2, var3, var5, var7);
      }
   }
}
