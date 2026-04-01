package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BedRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class BedRenderer implements BlockEntityRenderer<BedBlockEntity, BedRenderState> {
   private static final Map<Direction, Transformation> TRANSFORMATIONS = Util.<Direction, Transformation>makeEnumMap(Direction.class, BedRenderer::createModelTransform);
   private final SpriteGetter sprites;
   private final Model.Simple headModel;
   private final Model.Simple footModel;

   public BedRenderer(final BlockEntityRendererProvider.Context context) {
      this(context.sprites(), context.entityModelSet());
   }

   public BedRenderer(final SpecialModelRenderer.BakingContext context) {
      this(context.sprites(), context.entityModelSet());
   }

   public BedRenderer(final SpriteGetter sprites, final EntityModelSet entityModelSet) {
      super();
      this.sprites = sprites;
      this.headModel = new Model.Simple(entityModelSet.bakeLayer(ModelLayers.BED_HEAD), RenderTypes::entitySolid);
      this.footModel = new Model.Simple(entityModelSet.bakeLayer(ModelLayers.BED_FOOT), RenderTypes::entitySolid);
   }

   public static LayerDefinition createHeadLayer() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      root.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F), PartPose.ZERO);
      root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(50, 6).addBox(0.0F, 6.0F, 0.0F, 3.0F, 3.0F, 3.0F), PartPose.rotation(1.5707964F, 0.0F, 1.5707964F));
      root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(50, 18).addBox(-16.0F, 6.0F, 0.0F, 3.0F, 3.0F, 3.0F), PartPose.rotation(1.5707964F, 0.0F, 3.1415927F));
      return LayerDefinition.create(mesh, 64, 64);
   }

   public static LayerDefinition createFootLayer() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      root.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 22).addBox(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F), PartPose.ZERO);
      root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(50, 0).addBox(0.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F), PartPose.rotation(1.5707964F, 0.0F, 0.0F));
      root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(50, 12).addBox(-16.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F), PartPose.rotation(1.5707964F, 0.0F, 4.712389F));
      return LayerDefinition.create(mesh, 64, 64);
   }

   public BedRenderState createRenderState() {
      return new BedRenderState();
   }

   public void extractRenderState(final BedBlockEntity blockEntity, final BedRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
      BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
      state.color = blockEntity.getColor();
      state.facing = (Direction)blockEntity.getBlockState().getValue(BedBlock.FACING);
      state.isHead = blockEntity.getBlockState().getValue(BedBlock.PART) == BedPart.HEAD;
      if (blockEntity.getLevel() != null) {
         DoubleBlockCombiner.NeighborCombineResult<? extends BedBlockEntity> combineResult = DoubleBlockCombiner.<BedBlockEntity>combineWithNeigbour(BlockEntityType.BED, BedBlock::getBlockType, BedBlock::getConnectedDirection, ChestBlock.FACING, blockEntity.getBlockState(), blockEntity.getLevel(), blockEntity.getBlockPos(), (levelAccessor, blockPos) -> false);
         state.lightCoords = ((Int2IntFunction)combineResult.apply(new BrightnessCombiner())).get(state.lightCoords);
      }

   }

   public void submit(final BedRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
      SpriteId sprite = Sheets.getBedSprite(state.color);
      poseStack.pushPose();
      poseStack.mulPose(modelTransform(state.facing));
      this.submitPiece(poseStack, submitNodeCollector, state.isHead ? this.headModel : this.footModel, sprite, state.lightCoords, OverlayTexture.NO_OVERLAY, state.breakProgress, 0);
      poseStack.popPose();
   }

   public void submitSpecial(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final SpriteId sprite, final int outlineColor) {
      poseStack.pushPose();
      this.submitPiece(poseStack, submitNodeCollector, this.headModel, sprite, lightCoords, overlayCoords, (ModelFeatureRenderer.CrumblingOverlay)null, outlineColor);
      translateToFoot(poseStack);
      this.submitPiece(poseStack, submitNodeCollector, this.footModel, sprite, lightCoords, overlayCoords, (ModelFeatureRenderer.CrumblingOverlay)null, outlineColor);
      poseStack.popPose();
   }

   private void submitPiece(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final Model.Simple model, final SpriteId sprite, final int lightCoords, final int overlayCoords, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress, final int outlineColor) {
      submitNodeCollector.submitModel(model, Unit.INSTANCE, poseStack, sprite.renderType(RenderTypes::entitySolid), lightCoords, overlayCoords, -1, this.sprites.get(sprite), outlineColor, breakProgress);
   }

   private static Transformation createModelTransform(final Direction direction) {
      return new Transformation((new Matrix4f()).translation(0.0F, 0.5625F, 0.0F).rotate(Axis.XP.rotationDegrees(90.0F)).rotateAround(Axis.ZP.rotationDegrees(180.0F + direction.toYRot()), 0.5F, 0.5F, 0.5F));
   }

   public static Transformation modelTransform(final Direction direction) {
      return (Transformation)TRANSFORMATIONS.get(direction);
   }

   public void getExtents(final Consumer<Vector3fc> output) {
      PoseStack poseStack = new PoseStack();
      this.headModel.root().getExtentsForGui(poseStack, output);
      translateToFoot(poseStack);
      this.footModel.root().getExtentsForGui(poseStack, output);
   }

   private static void translateToFoot(final PoseStack poseStack) {
      poseStack.translate(0.0F, 1.0F, 0.0F);
   }
}
