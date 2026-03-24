package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector3f;
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

   public void submitNameTag(final PoseStack poseStack, final @Nullable Vec3 nameTagAttachment, final int offset, final Component name, final boolean seeThrough, final int lightCoords, final double distanceToCameraSq, final CameraRenderState camera) {
      this.order(0).submitNameTag(poseStack, nameTagAttachment, offset, name, seeThrough, lightCoords, distanceToCameraSq, camera);
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

   public void submitModelPart(final ModelPart modelPart, final PoseStack poseStack, final RenderType renderType, final int lightCoords, final int overlayCoords, final @Nullable TextureAtlasSprite sprite, final boolean sheeted, final boolean hasFoil, final int tintedColor, final ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay, final int outlineColor) {
      this.order(0).submitModelPart(modelPart, poseStack, renderType, lightCoords, overlayCoords, sprite, sheeted, hasFoil, tintedColor, crumblingOverlay, outlineColor);
   }

   public void submitMovingBlock(final PoseStack poseStack, final MovingBlockRenderState movingBlockRenderState) {
      this.order(0).submitMovingBlock(poseStack, movingBlockRenderState);
   }

   public void submitBlockModel(final PoseStack poseStack, final RenderType renderType, final List<BlockStateModelPart> modelParts, final int[] tintLayers, final int lightCoords, final int overlayCoords, final int outlineColor) {
      this.order(0).submitBlockModel(poseStack, renderType, modelParts, tintLayers, lightCoords, overlayCoords, outlineColor);
   }

   public void submitBreakingBlockModel(final PoseStack poseStack, final BlockStateModel model, final long seed, final int progress) {
      this.order(0).submitBreakingBlockModel(poseStack, model, seed, progress);
   }

   public void submitItem(final PoseStack poseStack, final ItemDisplayContext displayContext, final int lightCoords, final int overlayCoords, final int outlineColor, final int[] tintLayers, final List<BakedQuad> quads, final ItemStackRenderState.FoilType foilType) {
      this.order(0).submitItem(poseStack, displayContext, lightCoords, overlayCoords, outlineColor, tintLayers, quads, foilType);
   }

   public void submitCustomGeometry(final PoseStack poseStack, final RenderType renderType, final SubmitNodeCollector.CustomGeometryRenderer customGeometryRenderer) {
      this.order(0).submitCustomGeometry(poseStack, renderType, customGeometryRenderer);
   }

   public void submitParticleGroup(final SubmitNodeCollector.ParticleGroupRenderer particleGroupRenderer) {
      this.order(0).submitParticleGroup(particleGroupRenderer);
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

   public static record ShadowSubmit(Matrix4fc pose, float radius, List<EntityRenderState.ShadowPiece> pieces) {
      public ShadowSubmit {
         super();
      }
   }

   public static record FlameSubmit(PoseStack.Pose pose, EntityRenderState entityRenderState, Quaternionf rotation) {
      public FlameSubmit {
         super();
      }
   }

   public static record NameTagSubmit(Matrix4fc pose, float x, float y, Component text, int lightCoords, int color, int backgroundColor, double distanceToCameraSq) {
      public NameTagSubmit {
         super();
      }
   }

   public static record TextSubmit(Matrix4fc pose, float x, float y, FormattedCharSequence string, boolean dropShadow, Font.DisplayMode displayMode, int lightCoords, int color, int backgroundColor, int outlineColor) {
      public TextSubmit {
         super();
      }
   }

   public static record LeashSubmit(Matrix4f pose, EntityRenderState.LeashState leashState) {
      public LeashSubmit {
         super();
      }
   }

   public static record ModelSubmit<S>(PoseStack.Pose pose, Model<? super S> model, S state, int lightCoords, int overlayCoords, int tintedColor, @Nullable TextureAtlasSprite sprite, int outlineColor, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
      public ModelSubmit {
         super();
      }
   }

   public static record ModelPartSubmit(PoseStack.Pose pose, ModelPart modelPart, int lightCoords, int overlayCoords, @Nullable TextureAtlasSprite sprite, boolean sheeted, boolean hasFoil, int tintedColor, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay, int outlineColor) {
      public ModelPartSubmit {
         super();
      }
   }

   public static record TranslucentModelSubmit<S>(ModelSubmit<S> modelSubmit, RenderType renderType, Vector3f position) {
      public TranslucentModelSubmit {
         super();
      }
   }

   public static record MovingBlockSubmit(Matrix4fc pose, MovingBlockRenderState movingBlockRenderState) {
      public MovingBlockSubmit {
         super();
      }
   }

   public static record BlockModelSubmit(PoseStack.Pose pose, RenderType renderType, List<BlockStateModelPart> modelParts, int[] tintLayers, int lightCoords, int overlayCoords, int outlineColor) {
      public BlockModelSubmit {
         super();
      }
   }

   public static record ItemSubmit(PoseStack.Pose pose, ItemDisplayContext displayContext, int lightCoords, int overlayCoords, int outlineColor, int[] tintLayers, List<BakedQuad> quads, ItemStackRenderState.FoilType foilType) {
      public ItemSubmit {
         super();
      }
   }

   public static record CustomGeometrySubmit(PoseStack.Pose pose, SubmitNodeCollector.CustomGeometryRenderer customGeometryRenderer) {
      public CustomGeometrySubmit {
         super();
      }
   }

   public static record BreakingBlockModelSubmit(PoseStack.Pose pose, BlockStateModel model, long seed, int progress) {
      public BreakingBlockModelSubmit {
         super();
      }
   }
}
