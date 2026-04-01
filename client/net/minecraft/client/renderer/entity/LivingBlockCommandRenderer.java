package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.EnumMap;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingBlockCommandRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingBlockCommand;

public class LivingBlockCommandRenderer extends EntityRenderer<LivingBlockCommand, LivingBlockCommandRenderState> {
   private final EnumMap<LivingBlockCommand.Type, RenderType> renderTypes = new EnumMap(LivingBlockCommand.Type.class);

   protected LivingBlockCommandRenderer(final EntityRendererProvider.Context context) {
      super(context);
   }

   public LivingBlockCommandRenderState createRenderState() {
      return new LivingBlockCommandRenderState();
   }

   public void extractRenderState(final LivingBlockCommand entity, final LivingBlockCommandRenderState state, final float partialTicks) {
      super.extractRenderState(entity, state, partialTicks);
      state.visibility = Mth.clamp(1.0F - ((float)(entity.level().getGameTime() - entity.getSpawnTime()) + partialTicks) / 100.0F, 0.0F, 1.0F);
      state.commandType = entity.getCommandType();
   }

   public void submit(final LivingBlockCommandRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
      RenderType renderType = (RenderType)this.renderTypes.computeIfAbsent(state.commandType, (type) -> RenderTypes.entityTranslucent(Identifier.withDefaultNamespace("textures/entity/command/" + type.getSerializedName() + ".png")));
      poseStack.pushPose();
      submitNodeCollector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
         vertex(buffer, pose, 0.0F, 0, 0, 1, state.visibility);
         vertex(buffer, pose, 1.0F, 0, 1, 1, state.visibility);
         vertex(buffer, pose, 1.0F, 1, 1, 0, state.visibility);
         vertex(buffer, pose, 0.0F, 1, 0, 0, state.visibility);
      });
      poseStack.popPose();
      super.submit(state, poseStack, submitNodeCollector, camera);
   }

   private static void vertex(final VertexConsumer builder, final PoseStack.Pose pose, final float x, final int z, final int u, final int v, final float a) {
      builder.addVertex(pose, x - 0.5F, 0.0F, (float)z - 0.5F).setColor(255, 255, 255, (int)(a * 255.0F)).setUv((float)u, (float)v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(pose, 0.0F, 1.0F, 0.0F);
   }
}
