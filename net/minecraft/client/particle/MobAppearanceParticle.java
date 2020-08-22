package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.GuardianModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ElderGuardianRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class MobAppearanceParticle extends Particle {
   private final Model model;
   private final RenderType renderType;

   private MobAppearanceParticle(Level var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
      this.model = new GuardianModel();
      this.renderType = RenderType.entityTranslucent(ElderGuardianRenderer.GUARDIAN_ELDER_LOCATION);
      this.gravity = 0.0F;
      this.lifetime = 30;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.CUSTOM;
   }

   public void render(VertexConsumer var1, Camera var2, float var3) {
      float var4 = ((float)this.age + var3) / (float)this.lifetime;
      float var5 = 0.05F + 0.5F * Mth.sin(var4 * 3.1415927F);
      PoseStack var6 = new PoseStack();
      var6.mulPose(var2.rotation());
      var6.mulPose(Vector3f.XP.rotationDegrees(150.0F * var4 - 60.0F));
      var6.scale(-1.0F, -1.0F, 1.0F);
      var6.translate(0.0D, -1.1009999513626099D, 1.5D);
      MultiBufferSource.BufferSource var7 = Minecraft.getInstance().renderBuffers().bufferSource();
      VertexConsumer var8 = var7.getBuffer(this.renderType);
      this.model.renderToBuffer(var6, var8, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, var5);
      var7.endBatch();
   }

   // $FF: synthetic method
   MobAppearanceParticle(Level var1, double var2, double var4, double var6, Object var8) {
      this(var1, var2, var4, var6);
   }

   public static class Provider implements ParticleProvider {
      public Particle createParticle(SimpleParticleType var1, Level var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new MobAppearanceParticle(var2, var3, var5, var7);
      }
   }
}
