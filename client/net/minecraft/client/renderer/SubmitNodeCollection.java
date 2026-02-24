package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.CustomFeatureRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.feature.ModelPartFeatureRenderer;
import net.minecraft.client.renderer.feature.NameTagFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

public class SubmitNodeCollection implements OrderedSubmitNodeCollector {
   private final List<SubmitNodeStorage.ShadowSubmit> shadowSubmits = new ArrayList();
   private final List<SubmitNodeStorage.FlameSubmit> flameSubmits = new ArrayList();
   private final NameTagFeatureRenderer.Storage nameTagSubmits = new NameTagFeatureRenderer.Storage();
   private final List<SubmitNodeStorage.TextSubmit> textSubmits = new ArrayList();
   private final List<SubmitNodeStorage.LeashSubmit> leashSubmits = new ArrayList();
   private final List<SubmitNodeStorage.MovingBlockSubmit> movingBlockSubmits = new ArrayList();
   private final List<SubmitNodeStorage.BlockModelSubmit> blockModelSubmits = new ArrayList();
   private final List<SubmitNodeStorage.ItemSubmit> itemSubmits = new ArrayList();
   private final List<SubmitNodeCollector.ParticleGroupRenderer> particleGroupRenderers = new ArrayList();
   private final ModelFeatureRenderer.Storage modelSubmits = new ModelFeatureRenderer.Storage();
   private final ModelPartFeatureRenderer.Storage modelPartSubmits = new ModelPartFeatureRenderer.Storage();
   private final CustomFeatureRenderer.Storage customGeometrySubmits = new CustomFeatureRenderer.Storage();
   private final SubmitNodeStorage submitNodeStorage;
   private boolean wasUsed = false;

   public SubmitNodeCollection(final SubmitNodeStorage submitNodeStorage) {
      super();
      this.submitNodeStorage = submitNodeStorage;
   }

   public void submitShadow(final PoseStack poseStack, final float radius, final List<EntityRenderState.ShadowPiece> pieces) {
      this.wasUsed = true;
      PoseStack.Pose pose = poseStack.last();
      this.shadowSubmits.add(new SubmitNodeStorage.ShadowSubmit(new Matrix4f(pose.pose()), radius, pieces));
   }

   public void submitNameTag(final PoseStack poseStack, final @Nullable Vec3 nameTagAttachment, final int offset, final Component name, final boolean seeThrough, final int lightCoords, final double distanceToCameraSq, final CameraRenderState camera) {
      this.wasUsed = true;
      this.nameTagSubmits.add(poseStack, nameTagAttachment, offset, name, seeThrough, lightCoords, distanceToCameraSq, camera);
   }

   public void submitText(final PoseStack poseStack, final float x, final float y, final FormattedCharSequence string, final boolean dropShadow, final Font.DisplayMode displayMode, final int lightCoords, final int color, final int backgroundColor, final int outlineColor) {
      this.wasUsed = true;
      this.textSubmits.add(new SubmitNodeStorage.TextSubmit(new Matrix4f(poseStack.last().pose()), x, y, string, dropShadow, displayMode, lightCoords, color, backgroundColor, outlineColor));
   }

   public void submitFlame(final PoseStack poseStack, final EntityRenderState renderState, final Quaternionf rotation) {
      this.wasUsed = true;
      this.flameSubmits.add(new SubmitNodeStorage.FlameSubmit(poseStack.last().copy(), renderState, rotation));
   }

   public void submitLeash(final PoseStack poseStack, final EntityRenderState.LeashState leashState) {
      this.wasUsed = true;
      this.leashSubmits.add(new SubmitNodeStorage.LeashSubmit(new Matrix4f(poseStack.last().pose()), leashState));
   }

   public <S> void submitModel(final Model<? super S> model, final S state, final PoseStack poseStack, final RenderType renderType, final int lightCoords, final int overlayCoords, final int tintedColor, final @Nullable TextureAtlasSprite sprite, final int outlineColor, final ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
      this.wasUsed = true;
      SubmitNodeStorage.ModelSubmit<S> modelSubmit = new SubmitNodeStorage.ModelSubmit<S>(poseStack.last().copy(), model, state, lightCoords, overlayCoords, tintedColor, sprite, outlineColor, crumblingOverlay);
      this.modelSubmits.add(renderType, modelSubmit);
   }

   public void submitModelPart(final ModelPart modelPart, final PoseStack poseStack, final RenderType renderType, final int lightCoords, final int overlayCoords, final @Nullable TextureAtlasSprite sprite, final boolean sheeted, final boolean hasFoil, final int tintedColor, final ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay, final int outlineColor) {
      this.wasUsed = true;
      this.modelPartSubmits.add(renderType, new SubmitNodeStorage.ModelPartSubmit(poseStack.last().copy(), modelPart, lightCoords, overlayCoords, sprite, sheeted, hasFoil, tintedColor, crumblingOverlay, outlineColor));
   }

   public void submitMovingBlock(final PoseStack poseStack, final MovingBlockRenderState movingBlockRenderState) {
      this.wasUsed = true;
      this.movingBlockSubmits.add(new SubmitNodeStorage.MovingBlockSubmit(new Matrix4f(poseStack.last().pose()), movingBlockRenderState));
   }

   public void submitBlockModel(final PoseStack poseStack, final RenderType renderType, final BlockStateModel model, final int[] tintLayers, final int lightCoords, final int overlayCoords, final int outlineColor) {
      this.wasUsed = true;
      this.blockModelSubmits.add(new SubmitNodeStorage.BlockModelSubmit(poseStack.last().copy(), renderType, model, tintLayers, lightCoords, overlayCoords, outlineColor));
   }

   public void submitItem(final PoseStack poseStack, final ItemDisplayContext displayContext, final int lightCoords, final int overlayCoords, final int outlineColor, final int[] tintLayers, final List<BakedQuad> quads, final ItemStackRenderState.FoilType foilType) {
      this.wasUsed = true;
      this.itemSubmits.add(new SubmitNodeStorage.ItemSubmit(poseStack.last().copy(), displayContext, lightCoords, overlayCoords, outlineColor, tintLayers, quads, foilType));
   }

   public void submitCustomGeometry(final PoseStack poseStack, final RenderType renderType, final SubmitNodeCollector.CustomGeometryRenderer customGeometryRenderer) {
      this.wasUsed = true;
      this.customGeometrySubmits.add(poseStack, renderType, customGeometryRenderer);
   }

   public void submitParticleGroup(final SubmitNodeCollector.ParticleGroupRenderer particleGroupRenderer) {
      this.wasUsed = true;
      this.particleGroupRenderers.add(particleGroupRenderer);
   }

   public List<SubmitNodeStorage.ShadowSubmit> getShadowSubmits() {
      return this.shadowSubmits;
   }

   public List<SubmitNodeStorage.FlameSubmit> getFlameSubmits() {
      return this.flameSubmits;
   }

   public NameTagFeatureRenderer.Storage getNameTagSubmits() {
      return this.nameTagSubmits;
   }

   public List<SubmitNodeStorage.TextSubmit> getTextSubmits() {
      return this.textSubmits;
   }

   public List<SubmitNodeStorage.LeashSubmit> getLeashSubmits() {
      return this.leashSubmits;
   }

   public List<SubmitNodeStorage.MovingBlockSubmit> getMovingBlockSubmits() {
      return this.movingBlockSubmits;
   }

   public List<SubmitNodeStorage.BlockModelSubmit> getBlockModelSubmits() {
      return this.blockModelSubmits;
   }

   public ModelPartFeatureRenderer.Storage getModelPartSubmits() {
      return this.modelPartSubmits;
   }

   public List<SubmitNodeStorage.ItemSubmit> getItemSubmits() {
      return this.itemSubmits;
   }

   public List<SubmitNodeCollector.ParticleGroupRenderer> getParticleGroupRenderers() {
      return this.particleGroupRenderers;
   }

   public ModelFeatureRenderer.Storage getModelSubmits() {
      return this.modelSubmits;
   }

   public CustomFeatureRenderer.Storage getCustomGeometrySubmits() {
      return this.customGeometrySubmits;
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
      this.particleGroupRenderers.clear();
      this.modelSubmits.clear();
      this.customGeometrySubmits.clear();
      this.modelPartSubmits.clear();
   }

   public void endFrame() {
      this.modelSubmits.endFrame();
      this.modelPartSubmits.endFrame();
      this.customGeometrySubmits.endFrame();
      this.wasUsed = false;
   }
}
