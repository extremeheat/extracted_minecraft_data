package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.function.Consumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.banner.BannerFlagModel;
import net.minecraft.client.model.object.banner.BannerModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BannerRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Unit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class BannerRenderer implements BlockEntityRenderer<BannerBlockEntity, BannerRenderState> {
   private static final int MAX_PATTERNS = 16;
   private static final float SIZE = 0.6666667F;
   private final MaterialSet materials;
   private final BannerModel standingModel;
   private final BannerModel wallModel;
   private final BannerFlagModel standingFlagModel;
   private final BannerFlagModel wallFlagModel;

   public BannerRenderer(final BlockEntityRendererProvider.Context context) {
      this(context.entityModelSet(), context.materials());
   }

   public BannerRenderer(final SpecialModelRenderer.BakingContext context) {
      this(context.entityModelSet(), context.materials());
   }

   public BannerRenderer(final EntityModelSet modelSet, final MaterialSet materials) {
      super();
      this.materials = materials;
      this.standingModel = new BannerModel(modelSet.bakeLayer(ModelLayers.STANDING_BANNER));
      this.wallModel = new BannerModel(modelSet.bakeLayer(ModelLayers.WALL_BANNER));
      this.standingFlagModel = new BannerFlagModel(modelSet.bakeLayer(ModelLayers.STANDING_BANNER_FLAG));
      this.wallFlagModel = new BannerFlagModel(modelSet.bakeLayer(ModelLayers.WALL_BANNER_FLAG));
   }

   public BannerRenderState createRenderState() {
      return new BannerRenderState();
   }

   public void extractRenderState(final BannerBlockEntity blockEntity, final BannerRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
      BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
      state.baseColor = blockEntity.getBaseColor();
      state.patterns = blockEntity.getPatterns();
      BlockState blockState = blockEntity.getBlockState();
      if (blockState.getBlock() instanceof BannerBlock) {
         state.angle = -RotationSegment.convertToDegrees((Integer)blockState.getValue(BannerBlock.ROTATION));
         state.standing = true;
      } else {
         state.angle = -((Direction)blockState.getValue(WallBannerBlock.FACING)).toYRot();
         state.standing = false;
      }

      long gameTime = blockEntity.getLevel() != null ? blockEntity.getLevel().getGameTime() : 0L;
      BlockPos blockPos = blockEntity.getBlockPos();
      state.phase = ((float)Math.floorMod((long)(blockPos.getX() * 7 + blockPos.getY() * 9 + blockPos.getZ() * 13) + gameTime, 100L) + partialTicks) / 100.0F;
   }

   public void submit(final BannerRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
      BannerModel model;
      BannerFlagModel flagModel;
      if (state.standing) {
         model = this.standingModel;
         flagModel = this.standingFlagModel;
      } else {
         model = this.wallModel;
         flagModel = this.wallFlagModel;
      }

      submitBanner(this.materials, poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.angle, model, flagModel, state.phase, state.baseColor, state.patterns, state.breakProgress, 0);
   }

   public void submitSpecial(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final DyeColor baseColor, final BannerPatternLayers patterns, final int outlineColor) {
      submitBanner(this.materials, poseStack, submitNodeCollector, lightCoords, overlayCoords, 0.0F, this.standingModel, this.standingFlagModel, 0.0F, baseColor, patterns, (ModelFeatureRenderer.CrumblingOverlay)null, outlineColor);
   }

   private static void submitBanner(final MaterialSet materials, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final float angle, final BannerModel model, final BannerFlagModel flagModel, final float phase, final DyeColor baseColor, final BannerPatternLayers patterns, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress, final int outlineColor) {
      poseStack.pushPose();
      poseStack.translate(0.5F, 0.0F, 0.5F);
      poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(angle));
      poseStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
      Material material = ModelBakery.BANNER_BASE;
      submitNodeCollector.submitModel(model, Unit.INSTANCE, poseStack, material.renderType(RenderTypes::entitySolid), lightCoords, overlayCoords, -1, materials.get(material), outlineColor, breakProgress);
      submitPatterns(materials, poseStack, submitNodeCollector, lightCoords, overlayCoords, flagModel, phase, material, true, baseColor, patterns, false, breakProgress, outlineColor);
      poseStack.popPose();
   }

   public static <S> void submitPatterns(final MaterialSet materials, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final Model<S> model, final S state, final Material baseMaterial, final boolean banner, final DyeColor baseColor, final BannerPatternLayers patterns, final boolean hasFoil, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress, final int outlineColor) {
      submitNodeCollector.submitModel(model, state, poseStack, baseMaterial.renderType(RenderTypes::entitySolid), lightCoords, overlayCoords, -1, materials.get(baseMaterial), outlineColor, breakProgress);
      if (hasFoil) {
         submitNodeCollector.submitModel(model, state, poseStack, RenderTypes.entityGlint(), lightCoords, overlayCoords, -1, materials.get(baseMaterial), 0, breakProgress);
      }

      submitPatternLayer(materials, poseStack, submitNodeCollector, lightCoords, overlayCoords, model, state, banner ? Sheets.BANNER_BASE : Sheets.SHIELD_BASE, baseColor, breakProgress);

      for(int maskIndex = 0; maskIndex < 16 && maskIndex < patterns.layers().size(); ++maskIndex) {
         BannerPatternLayers.Layer layer = (BannerPatternLayers.Layer)patterns.layers().get(maskIndex);
         Material material = banner ? Sheets.getBannerMaterial(layer.pattern()) : Sheets.getShieldMaterial(layer.pattern());
         submitPatternLayer(materials, poseStack, submitNodeCollector, lightCoords, overlayCoords, model, state, material, layer.color(), (ModelFeatureRenderer.CrumblingOverlay)null);
      }

   }

   private static <S> void submitPatternLayer(final MaterialSet materials, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final Model<S> model, final S state, final Material material, final DyeColor color, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
      int diffuseColor = color.getTextureDiffuseColor();
      submitNodeCollector.submitModel(model, state, poseStack, material.renderType(RenderTypes::entityNoOutline), lightCoords, overlayCoords, diffuseColor, materials.get(material), 0, breakProgress);
   }

   public void getExtents(final Consumer<Vector3fc> output) {
      PoseStack poseStack = new PoseStack();
      poseStack.translate(0.5F, 0.0F, 0.5F);
      poseStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
      this.standingModel.root().getExtentsForGui(poseStack, output);
      this.standingFlagModel.setupAnim(0.0F);
      this.standingFlagModel.root().getExtentsForGui(poseStack, output);
   }
}
