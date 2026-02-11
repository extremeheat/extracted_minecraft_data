package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

public interface OrderedSubmitNodeCollector {
   void submitShadow(PoseStack poseStack, float radius, List<EntityRenderState.ShadowPiece> pieces);

   void submitNameTag(PoseStack poseStack, @Nullable Vec3 nameTagAttachment, final int offset, Component name, boolean seeThrough, int lightCoords, double distanceToCameraSq, final CameraRenderState camera);

   void submitText(PoseStack poseStack, float x, float y, FormattedCharSequence string, boolean dropShadow, Font.DisplayMode displayMode, int lightCoords, int color, int backgroundColor, int outlineColor);

   void submitFlame(PoseStack poseStack, EntityRenderState renderState, Quaternionf rotation);

   void submitLeash(PoseStack poseStack, EntityRenderState.LeashState leashState);

   <S> void submitModel(Model<? super S> model, S state, PoseStack poseStack, RenderType renderType, int lightCoords, int overlayCoords, int tintedColor, @Nullable TextureAtlasSprite sprite, int outlineColor, final ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay);

   default <S> void submitModel(final Model<? super S> model, final S state, final PoseStack poseStack, final RenderType renderType, final int lightCoords, final int overlayCoords, final int outlineColor, final ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
      this.submitModel(model, state, poseStack, renderType, lightCoords, overlayCoords, -1, (TextureAtlasSprite)null, outlineColor, crumblingOverlay);
   }

   default void submitModelPart(final ModelPart modelPart, final PoseStack poseStack, final RenderType renderType, final int lightCoords, final int overlayCoords, final @Nullable TextureAtlasSprite sprite) {
      this.submitModelPart(modelPart, poseStack, renderType, lightCoords, overlayCoords, sprite, false, false, -1, (ModelFeatureRenderer.CrumblingOverlay)null, 0);
   }

   default void submitModelPart(final ModelPart modelPart, final PoseStack poseStack, final RenderType renderType, final int lightCoords, final int overlayCoords, final @Nullable TextureAtlasSprite sprite, final int tintedColor, final ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
      this.submitModelPart(modelPart, poseStack, renderType, lightCoords, overlayCoords, sprite, false, false, tintedColor, crumblingOverlay, 0);
   }

   default void submitModelPart(final ModelPart modelPart, final PoseStack poseStack, final RenderType renderType, final int lightCoords, final int overlayCoords, final @Nullable TextureAtlasSprite sprite, final boolean sheeted, final boolean hasFoil) {
      this.submitModelPart(modelPart, poseStack, renderType, lightCoords, overlayCoords, sprite, sheeted, hasFoil, -1, (ModelFeatureRenderer.CrumblingOverlay)null, 0);
   }

   void submitModelPart(ModelPart modelPart, PoseStack poseStack, RenderType renderType, int lightCoords, int overlayCoords, @Nullable TextureAtlasSprite sprite, boolean sheeted, boolean hasFoil, int tintedColor, final ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay, final int outlineColor);

   void submitBlock(PoseStack poseStack, BlockState state, int lightCoords, int overlayCoords, int outlineColor);

   void submitMovingBlock(PoseStack poseStack, MovingBlockRenderState movingBlockRenderState);

   void submitBlockModel(PoseStack poseStack, RenderType renderType, BlockStateModel model, int tintColor, int lightCoords, int overlayCoords, int outlineColor);

   void submitItem(PoseStack poseStack, ItemDisplayContext displayContext, int lightCoords, int overlayCoords, int outlineColor, int[] tintLayers, List<BakedQuad> quads, ItemStackRenderState.FoilType foilType);

   void submitCustomGeometry(PoseStack poseStack, RenderType renderType, SubmitNodeCollector.CustomGeometryRenderer customGeometryRenderer);

   void submitParticleGroup(SubmitNodeCollector.ParticleGroupRenderer particleGroupRenderer);
}
