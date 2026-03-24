package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.projectile.hurtingprojectile.DragonFireball;
import org.joml.Quaternionfc;

public class DragonFireballRenderer extends EntityRenderer<DragonFireball, EntityRenderState> {
   private static final Identifier TEXTURE_LOCATION = Identifier.withDefaultNamespace("textures/entity/enderdragon/dragon_fireball.png");
   private static final RenderType RENDER_TYPE;

   public DragonFireballRenderer(final EntityRendererProvider.Context context) {
      super(context);
   }

   protected int getBlockLightLevel(final DragonFireball entity, final BlockPos blockPos) {
      return 15;
   }

   public void submit(final EntityRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
      poseStack.pushPose();
      poseStack.scale(2.0F, 2.0F, 2.0F);
      poseStack.mulPose((Quaternionfc)camera.orientation);
      submitNodeCollector.submitCustomGeometry(poseStack, RENDER_TYPE, (pose, buffer) -> {
         vertex(buffer, pose, state.lightCoords, 0.0F, 0, 0, 1);
         vertex(buffer, pose, state.lightCoords, 1.0F, 0, 1, 1);
         vertex(buffer, pose, state.lightCoords, 1.0F, 1, 1, 0);
         vertex(buffer, pose, state.lightCoords, 0.0F, 1, 0, 0);
      });
      poseStack.popPose();
      super.submit(state, poseStack, submitNodeCollector, camera);
   }

   private static void vertex(final VertexConsumer builder, final PoseStack.Pose pose, final int lightCoords, final float x, final int y, final int u, final int v) {
      builder.addVertex(pose, x - 0.5F, (float)y - 0.25F, 0.0F).setColor(-1).setUv((float)u, (float)v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightCoords).setNormal(pose, 0.0F, 1.0F, 0.0F);
   }

   public EntityRenderState createRenderState() {
      return new EntityRenderState();
   }

   static {
      RENDER_TYPE = RenderTypes.entityCutout(TEXTURE_LOCATION);
   }
}
