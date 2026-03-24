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
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.ParticleGroupRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import org.joml.Quaternionfc;

public class ElderGuardianParticleGroup extends ParticleGroup<ElderGuardianParticle> {
   public ElderGuardianParticleGroup(final ParticleEngine engine) {
      super(engine);
   }

   public ParticleGroupRenderState extractRenderState(final Frustum frustum, final Camera camera, final float partialTickTime) {
      return new State(this.particles.stream().map((particle) -> ElderGuardianParticleGroup.ElderGuardianParticleRenderState.fromParticle(particle, camera, partialTickTime)).toList());
   }

   private static record State(List<ElderGuardianParticleRenderState> states) implements ParticleGroupRenderState {
      private State {
         super();
      }

      public void submit(final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
         for(ElderGuardianParticleRenderState state : this.states) {
            submitNodeCollector.submitModel(state.model, Unit.INSTANCE, state.poseStack, state.renderType, 15728880, OverlayTexture.NO_OVERLAY, state.color, (TextureAtlasSprite)null, 0, (ModelFeatureRenderer.CrumblingOverlay)null);
         }

      }
   }

   private static record ElderGuardianParticleRenderState(Model<Unit> model, PoseStack poseStack, RenderType renderType, int color) {
      private ElderGuardianParticleRenderState {
         super();
      }

      public static ElderGuardianParticleRenderState fromParticle(final ElderGuardianParticle particle, final Camera camera, final float partialTickTime) {
         float ageScale = ((float)particle.age + partialTickTime) / (float)particle.lifetime;
         float alpha = 0.05F + 0.5F * Mth.sin((double)(ageScale * 3.1415927F));
         int color = ARGB.colorFromFloat(alpha, 1.0F, 1.0F, 1.0F);
         PoseStack poseStack = new PoseStack();
         poseStack.pushPose();
         poseStack.mulPose((Quaternionfc)camera.rotation());
         poseStack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(60.0F - 150.0F * ageScale));
         float scale = 0.42553192F;
         poseStack.scale(0.42553192F, -0.42553192F, -0.42553192F);
         poseStack.translate(0.0F, -0.56F, 3.5F);
         return new ElderGuardianParticleRenderState(particle.model, poseStack, particle.renderType, color);
      }
   }
}
