package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.GuardianModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ElderGuardianRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class MobAppearanceParticle extends Particle {
   private final Model model;
   private final RenderType renderType;

   MobAppearanceParticle(ClientLevel var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
      this.renderType = RenderType.entityTranslucent(ElderGuardianRenderer.GUARDIAN_ELDER_LOCATION);
      this.model = new GuardianModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.ELDER_GUARDIAN));
      this.gravity = 0.0F;
      this.lifetime = 30;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.CUSTOM;
   }

   public void renderCustom(PoseStack var1, MultiBufferSource var2, Camera var3, float var4) {
      float var5 = ((float)this.age + var4) / (float)this.lifetime;
      float var6 = 0.05F + 0.5F * Mth.sin(var5 * 3.1415927F);
      int var7 = ARGB.colorFromFloat(var6, 1.0F, 1.0F, 1.0F);
      var1.pushPose();
      var1.mulPose(var3.rotation());
      var1.mulPose(Axis.XP.rotationDegrees(60.0F - 150.0F * var5));
      float var8 = 0.42553192F;
      var1.scale(0.42553192F, -0.42553192F, -0.42553192F);
      var1.translate(0.0F, -0.56F, 3.5F);
      VertexConsumer var9 = var2.getBuffer(this.renderType);
      this.model.renderToBuffer(var1, var9, 15728880, OverlayTexture.NO_OVERLAY, var7);
      var1.popPose();
   }

   public void render(VertexConsumer var1, Camera var2, float var3) {
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Provider() {
         super();
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new MobAppearanceParticle(var2, var3, var5, var7);
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
