package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.state.ParticleGroupRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import org.joml.Quaternionfc;

public class ElderGuardianParticleGroup extends ParticleGroup<ElderGuardianParticle> {
   public ElderGuardianParticleGroup(ParticleEngine var1) {
      super(var1);
   }

   public ParticleGroupRenderState extractRenderState(Frustum var1, Camera var2, float var3) {
      return new State(this.particles.stream().map((var2x) -> ElderGuardianParticleGroup.ElderGuardianParticleRenderState.fromParticle(var2x, var2, var3)).toList());
   }

   static record State(List<ElderGuardianParticleRenderState> states) implements ParticleGroupRenderState {
      State(List<ElderGuardianParticleRenderState> var1) {
         super();
         this.states = var1;
      }

      public void submit(SubmitNodeCollector var1, CameraRenderState var2) {
         for(ElderGuardianParticleRenderState var4 : this.states) {
            var1.submitModel(var4.model, Unit.INSTANCE, var4.poseStack, var4.renderType, 15728880, OverlayTexture.NO_OVERLAY, var4.color, (TextureAtlasSprite)null, 0, (ModelFeatureRenderer.CrumblingOverlay)null);
         }

      }
   }

   static record ElderGuardianParticleRenderState(Model<Unit> model, PoseStack poseStack, RenderType renderType, int color) {
      final Model<Unit> model;
      final PoseStack poseStack;
      final RenderType renderType;
      final int color;

      private ElderGuardianParticleRenderState(Model<Unit> var1, PoseStack var2, RenderType var3, int var4) {
         super();
         this.model = var1;
         this.poseStack = var2;
         this.renderType = var3;
         this.color = var4;
      }

      public static ElderGuardianParticleRenderState fromParticle(ElderGuardianParticle var0, Camera var1, float var2) {
         float var3 = ((float)var0.age + var2) / (float)var0.lifetime;
         float var4 = 0.05F + 0.5F * Mth.sin((double)(var3 * 3.1415927F));
         int var5 = ARGB.colorFromFloat(var4, 1.0F, 1.0F, 1.0F);
         PoseStack var6 = new PoseStack();
         var6.pushPose();
         var6.mulPose((Quaternionfc)var1.rotation());
         var6.mulPose((Quaternionfc)Axis.XP.rotationDegrees(60.0F - 150.0F * var3));
         float var7 = 0.42553192F;
         var6.scale(0.42553192F, -0.42553192F, -0.42553192F);
         var6.translate(0.0F, -0.56F, 3.5F);
         return new ElderGuardianParticleRenderState(var0.model, var6, var0.renderType, var5);
      }
   }
}
