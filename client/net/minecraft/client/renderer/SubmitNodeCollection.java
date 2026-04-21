package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.BlockModelFeatureRenderer;
import net.minecraft.client.renderer.feature.CustomFeatureRenderer;
import net.minecraft.client.renderer.feature.FlameFeatureRenderer;
import net.minecraft.client.renderer.feature.GizmoFeatureRenderer;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.feature.LeashFeatureRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.feature.MovingBlockFeatureRenderer;
import net.minecraft.client.renderer.feature.NameTagFeatureRenderer;
import net.minecraft.client.renderer.feature.ShadowFeatureRenderer;
import net.minecraft.client.renderer.feature.ShapeOutlineFeatureRenderer;
import net.minecraft.client.renderer.feature.TextFeatureRenderer;
import net.minecraft.client.renderer.gizmos.DrawableGizmoPrimitives;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

public class SubmitNodeCollection implements OrderedSubmitNodeCollector {
   private final List<ShadowFeatureRenderer.Submit> shadowSubmits = new ArrayList();
   private final List<FlameFeatureRenderer.Submit> flameSubmits = new ArrayList();
   private final NameTagFeatureRenderer.Storage nameTagSubmits = new NameTagFeatureRenderer.Storage();
   private final List<TextFeatureRenderer.Submit> textSubmits = new ArrayList();
   private final List<LeashFeatureRenderer.Submit> leashSubmits = new ArrayList();
   private final List<MovingBlockFeatureRenderer.Submit> movingBlockSubmits = new ArrayList();
   private final List<BlockModelFeatureRenderer.Submit> blockModelSubmits = new ArrayList();
   private final List<ShapeOutlineFeatureRenderer.Submit> shapeOutlineSubmits = new ArrayList();
   private final List<ItemFeatureRenderer.Submit> itemSubmits = new ArrayList();
   private final List<QuadParticleRenderState> quadParticleGroups = new ArrayList();
   private final ModelFeatureRenderer.Storage modelSubmits = new ModelFeatureRenderer.Storage();
   private final CustomFeatureRenderer.Storage customGeometrySubmits = new CustomFeatureRenderer.Storage();
   private final List<GizmoFeatureRenderer.Submit> gizmoSubmits = new ArrayList();
   private final SubmitNodeStorage submitNodeStorage;
   private boolean wasUsed = false;

   public SubmitNodeCollection(final SubmitNodeStorage submitNodeStorage) {
      super();
      this.submitNodeStorage = submitNodeStorage;
   }

   public void submitShadow(final PoseStack poseStack, final float radius, final List<EntityRenderState.ShadowPiece> pieces) {
      this.wasUsed = true;
      PoseStack.Pose pose = poseStack.last();
      this.shadowSubmits.add(new ShadowFeatureRenderer.Submit(new Matrix4f(pose.pose()), radius, pieces));
   }

   public void submitNameTag(final PoseStack poseStack, final @Nullable Vec3 nameTagAttachment, final int offset, final Component name, final boolean seeThrough, final int lightCoords, final CameraRenderState camera) {
      this.wasUsed = true;
      this.nameTagSubmits.add(poseStack, nameTagAttachment, offset, name, seeThrough, lightCoords, camera);
   }

   public void submitText(final PoseStack poseStack, final float x, final float y, final FormattedCharSequence string, final boolean dropShadow, final Font.DisplayMode displayMode, final int lightCoords, final int color, final int backgroundColor, final int outlineColor) {
      this.wasUsed = true;
      this.textSubmits.add(new TextFeatureRenderer.Submit(new Matrix4f(poseStack.last().pose()), x, y, string, dropShadow, displayMode, lightCoords, color, backgroundColor, outlineColor));
   }

   public void submitFlame(final PoseStack poseStack, final EntityRenderState renderState, final Quaternionf rotation) {
      this.wasUsed = true;
      this.flameSubmits.add(new FlameFeatureRenderer.Submit(poseStack.last().copy(), renderState, rotation));
   }

   public void submitLeash(final PoseStack poseStack, final EntityRenderState.LeashState leashState) {
      this.wasUsed = true;
      this.leashSubmits.add(new LeashFeatureRenderer.Submit(new Matrix4f(poseStack.last().pose()), leashState));
   }

   public <S> void submitModel(final Model<? super S> model, final S state, final PoseStack poseStack, final RenderType renderType, final int lightCoords, final int overlayCoords, final int tintedColor, final @Nullable TextureAtlasSprite sprite, final int outlineColor, final ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
      this.wasUsed = true;
      ModelFeatureRenderer.Submit<S> submit = new ModelFeatureRenderer.Submit<S>(renderType, poseStack.last().copy(), model, state, lightCoords, overlayCoords, tintedColor, sprite, outlineColor, crumblingOverlay);
      this.modelSubmits.add(renderType, submit);
   }

   public void submitMovingBlock(final PoseStack poseStack, final MovingBlockRenderState movingBlockRenderState) {
      this.wasUsed = true;
      this.movingBlockSubmits.add(new MovingBlockFeatureRenderer.Submit(new Matrix4f(poseStack.last().pose()), movingBlockRenderState));
   }

   public void submitBlockModel(final PoseStack poseStack, final RenderType renderType, final List<BlockStateModelPart> modelParts, final int[] tintLayers, final int lightCoords, final int overlayCoords, final int outlineColor) {
      this.wasUsed = true;
      this.blockModelSubmits.add(new BlockModelFeatureRenderer.Submit(poseStack.last().copy(), renderType, modelParts, tintLayers, lightCoords, overlayCoords, outlineColor, (PoseStack.Pose)null));
   }

   public void submitBreakingBlockModel(final PoseStack poseStack, final List<BlockStateModelPart> parts, final int progress) {
      this.wasUsed = true;
      PoseStack.Pose pose = poseStack.last().copy();
      this.blockModelSubmits.add(new BlockModelFeatureRenderer.Submit(pose, (RenderType)ModelBakery.DESTROY_TYPES.get(progress), List.copyOf(parts), BlockModelRenderState.EMPTY_TINTS, 15728880, OverlayTexture.NO_OVERLAY, 0, pose));
   }

   public void submitShapeOutline(final PoseStack poseStack, final VoxelShape shape, final RenderType renderType, final int color, final float width, final boolean afterTerrain) {
      this.wasUsed = true;
      this.shapeOutlineSubmits.add(new ShapeOutlineFeatureRenderer.Submit(poseStack.last().copy(), shape, renderType, color, width, afterTerrain));
   }

   public void submitItem(final PoseStack poseStack, final ItemDisplayContext displayContext, final int lightCoords, final int overlayCoords, final int outlineColor, final int[] tintLayers, final List<BakedQuad> quads, final ItemStackRenderState.FoilType foilType) {
      this.wasUsed = true;
      this.itemSubmits.add(new ItemFeatureRenderer.Submit(poseStack.last().copy(), displayContext, lightCoords, overlayCoords, outlineColor, tintLayers, quads, foilType));
   }

   public void submitCustomGeometry(final PoseStack poseStack, final RenderType renderType, final int outlineColor, final SubmitNodeCollector.CustomGeometryRenderer customGeometryRenderer) {
      this.wasUsed = true;
      this.customGeometrySubmits.add(poseStack, renderType, outlineColor, customGeometryRenderer);
   }

   public void submitQuadParticleGroup(final QuadParticleRenderState particles) {
      this.wasUsed = true;
      this.quadParticleGroups.add(particles);
   }

   public void submitGizmoPrimitives(final DrawableGizmoPrimitives.Group group, final CameraRenderState camera, final boolean onTop) {
      this.wasUsed = true;
      this.gizmoSubmits.add(new GizmoFeatureRenderer.Submit(group, camera, onTop));
   }

   public List<ShadowFeatureRenderer.Submit> getShadowSubmits() {
      return this.shadowSubmits;
   }

   public List<FlameFeatureRenderer.Submit> getFlameSubmits() {
      return this.flameSubmits;
   }

   public NameTagFeatureRenderer.Storage getNameTagSubmits() {
      return this.nameTagSubmits;
   }

   public List<TextFeatureRenderer.Submit> getTextSubmits() {
      return this.textSubmits;
   }

   public List<LeashFeatureRenderer.Submit> getLeashSubmits() {
      return this.leashSubmits;
   }

   public List<MovingBlockFeatureRenderer.Submit> getMovingBlockSubmits() {
      return this.movingBlockSubmits;
   }

   public List<BlockModelFeatureRenderer.Submit> getBlockModelSubmits() {
      return this.blockModelSubmits;
   }

   public List<ItemFeatureRenderer.Submit> getItemSubmits() {
      return this.itemSubmits;
   }

   public List<QuadParticleRenderState> getQuadParticleGroups() {
      return this.quadParticleGroups;
   }

   public ModelFeatureRenderer.Storage getModelSubmits() {
      return this.modelSubmits;
   }

   public CustomFeatureRenderer.Storage getCustomGeometrySubmits() {
      return this.customGeometrySubmits;
   }

   public List<ShapeOutlineFeatureRenderer.Submit> getShapeOutlineSubmits() {
      return this.shapeOutlineSubmits;
   }

   public List<GizmoFeatureRenderer.Submit> getGizmoSubmits() {
      return this.gizmoSubmits;
   }

   public boolean wasUsed() {
      return this.wasUsed;
   }

   public void clear() {
      this.shadowSubmits.clear();
      this.flameSubmits.clear();
      this.nameTagSubmits.clear();
      this.textSubmits.clear();
      this.leashSubmits.clear();
      this.movingBlockSubmits.clear();
      this.blockModelSubmits.clear();
      this.itemSubmits.clear();
      this.quadParticleGroups.clear();
      this.modelSubmits.clear();
      this.customGeometrySubmits.clear();
      this.shapeOutlineSubmits.clear();
      this.gizmoSubmits.clear();
   }

   public void endFrame() {
      this.modelSubmits.endFrame();
      this.customGeometrySubmits.endFrame();
      this.wasUsed = false;
   }
}
