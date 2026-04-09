package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.slime.SulfurCubeModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.SulfurCubeRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.joml.Quaternionfc;

public class SulfurCubeInnerLayer extends RenderLayer<SulfurCubeRenderState, SulfurCubeModel> {
   private static final Identifier SULFUR_CUBE_INNER_LOCATION = Identifier.withDefaultNamespace("textures/entity/sulfur_cube/sulfur_cube_inner.png");
   private final SulfurCubeModel model;

   public SulfurCubeInnerLayer(final RenderLayerParent<SulfurCubeRenderState, SulfurCubeModel> renderer, final EntityModelSet modelSet) {
      super(renderer);
      this.model = new SulfurCubeModel(modelSet.bakeLayer(ModelLayers.SULFUR_CUBE_INNER));
   }

   public void submit(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final SulfurCubeRenderState state, final float yRot, final float xRot) {
      int overlayCoords = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
      if (!state.containedBlock.isEmpty()) {
         poseStack.pushPose();
         poseStack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(180.0F));
         poseStack.translate(-0.5F, -0.518F, -0.5F);
         state.containedBlock.submit(poseStack, submitNodeCollector, state.lightCoords, overlayCoords, state.outlineColor);
         poseStack.popPose();
      } else if (!state.isInvisible) {
         RenderType renderType = RenderTypes.entityTranslucent(SULFUR_CUBE_INNER_LOCATION);
         submitNodeCollector.order(-1).submitModel(this.model, state, poseStack, renderType, lightCoords, overlayCoords, -1, (TextureAtlasSprite)null, state.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      }

   }
}
