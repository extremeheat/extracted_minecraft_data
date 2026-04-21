package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.gizmos.DrawableGizmoPrimitives;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

public class SubmitNodeStorage implements SubmitNodeCollector {
   private final Int2ObjectAVLTreeMap<SubmitNodeCollection> submitsPerOrder = new Int2ObjectAVLTreeMap();

   public SubmitNodeStorage() {
      super();
   }

   public SubmitNodeCollection order(final int order) {
      return (SubmitNodeCollection)this.submitsPerOrder.computeIfAbsent(order, (ignored) -> new SubmitNodeCollection(this));
   }

   public void submitShadow(final PoseStack poseStack, final float radius, final List<EntityRenderState.ShadowPiece> pieces) {
      this.order(0).submitShadow(poseStack, radius, pieces);
   }

   public void submitNameTag(final PoseStack poseStack, final @Nullable Vec3 nameTagAttachment, final int offset, final Component name, final boolean seeThrough, final int lightCoords, final CameraRenderState camera) {
      this.order(0).submitNameTag(poseStack, nameTagAttachment, offset, name, seeThrough, lightCoords, camera);
   }

   public void submitText(final PoseStack poseStack, final float x, final float y, final FormattedCharSequence string, final boolean dropShadow, final Font.DisplayMode displayMode, final int lightCoords, final int color, final int backgroundColor, final int outlineColor) {
      this.order(0).submitText(poseStack, x, y, string, dropShadow, displayMode, lightCoords, color, backgroundColor, outlineColor);
   }

   public void submitFlame(final PoseStack poseStack, final EntityRenderState renderState, final Quaternionf rotation) {
      this.order(0).submitFlame(poseStack, renderState, rotation);
   }

   public void submitLeash(final PoseStack poseStack, final EntityRenderState.LeashState leashState) {
      this.order(0).submitLeash(poseStack, leashState);
   }

   public <S> void submitModel(final Model<? super S> model, final S state, final PoseStack poseStack, final RenderType renderType, final int lightCoords, final int overlayCoords, final int tintedColor, final @Nullable TextureAtlasSprite sprite, final int outlineColor, final ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
      this.order(0).submitModel(model, state, poseStack, renderType, lightCoords, overlayCoords, tintedColor, sprite, outlineColor, crumblingOverlay);
   }

   public void submitMovingBlock(final PoseStack poseStack, final MovingBlockRenderState movingBlockRenderState) {
      this.order(0).submitMovingBlock(poseStack, movingBlockRenderState);
   }

   public void submitBlockModel(final PoseStack poseStack, final RenderType renderType, final List<BlockStateModelPart> modelParts, final int[] tintLayers, final int lightCoords, final int overlayCoords, final int outlineColor) {
      this.order(0).submitBlockModel(poseStack, renderType, modelParts, tintLayers, lightCoords, overlayCoords, outlineColor);
   }

   public void submitBreakingBlockModel(final PoseStack poseStack, final List<BlockStateModelPart> parts, final int progress) {
      this.order(0).submitBreakingBlockModel(poseStack, parts, progress);
   }

   public void submitShapeOutline(final PoseStack poseStack, final VoxelShape shape, final RenderType renderType, final int color, final float width, final boolean afterTerrain) {
      this.order(0).submitShapeOutline(poseStack, shape, renderType, color, width, afterTerrain);
   }

   public void submitItem(final PoseStack poseStack, final ItemDisplayContext displayContext, final int lightCoords, final int overlayCoords, final int outlineColor, final int[] tintLayers, final List<BakedQuad> quads, final ItemStackRenderState.FoilType foilType) {
      this.order(0).submitItem(poseStack, displayContext, lightCoords, overlayCoords, outlineColor, tintLayers, quads, foilType);
   }

   public void submitCustomGeometry(final PoseStack poseStack, final RenderType renderType, final int outlineColor, final SubmitNodeCollector.CustomGeometryRenderer customGeometryRenderer) {
      this.order(0).submitCustomGeometry(poseStack, renderType, outlineColor, customGeometryRenderer);
   }

   public void submitQuadParticleGroup(final QuadParticleRenderState particles) {
      this.order(0).submitQuadParticleGroup(particles);
   }

   public void submitGizmoPrimitives(final DrawableGizmoPrimitives.Group group, final CameraRenderState camera, final boolean onTop) {
      this.order(0).submitGizmoPrimitives(group, camera, onTop);
   }

   public void clear() {
      this.submitsPerOrder.values().forEach(SubmitNodeCollection::clear);
   }

   public void endFrame() {
      this.submitsPerOrder.values().removeIf((collection) -> !collection.wasUsed());
      this.submitsPerOrder.values().forEach(SubmitNodeCollection::endFrame);
   }

   public Int2ObjectAVLTreeMap<SubmitNodeCollection> getSubmitsPerOrder() {
      return this.submitsPerOrder;
   }
}
