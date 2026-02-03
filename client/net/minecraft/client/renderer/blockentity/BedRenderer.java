package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
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
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.core.Direction;
import net.minecraft.util.Unit;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class BedRenderer implements BlockEntityRenderer<BedBlockEntity, BedRenderState> {
   private final MaterialSet materials;
   private final Model.Simple headModel;
   private final Model.Simple footModel;

   public BedRenderer(final BlockEntityRendererProvider.Context context) {
      this(context.materials(), context.entityModelSet());
   }

   public BedRenderer(final SpecialModelRenderer.BakingContext context) {
      this(context.materials(), context.entityModelSet());
   }

   public BedRenderer(final MaterialSet materials, final EntityModelSet entityModelSet) {
      super();
      this.materials = materials;
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
      Material material = Sheets.getBedMaterial(state.color);
      this.submitPiece(poseStack, submitNodeCollector, state.isHead ? this.headModel : this.footModel, state.facing, material, state.lightCoords, OverlayTexture.NO_OVERLAY, false, state.breakProgress, 0);
   }

   public void submitSpecial(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final Material material, final int outlineColor) {
      this.submitPiece(poseStack, submitNodeCollector, this.headModel, Direction.SOUTH, material, lightCoords, overlayCoords, false, (ModelFeatureRenderer.CrumblingOverlay)null, outlineColor);
      this.submitPiece(poseStack, submitNodeCollector, this.footModel, Direction.SOUTH, material, lightCoords, overlayCoords, true, (ModelFeatureRenderer.CrumblingOverlay)null, outlineColor);
   }

   private void submitPiece(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final Model.Simple model, final Direction direction, final Material material, final int lightCoords, final int overlayCoords, final boolean translateZ, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress, final int outlineColor) {
      poseStack.pushPose();
      preparePose(poseStack, translateZ, direction);
      submitNodeCollector.submitModel(model, Unit.INSTANCE, poseStack, material.renderType(RenderTypes::entitySolid), lightCoords, overlayCoords, -1, this.materials.get(material), outlineColor, breakProgress);
      poseStack.popPose();
   }

   private static void preparePose(final PoseStack poseStack, final boolean translateZ, final Direction direction) {
      poseStack.translate(0.0F, 0.5625F, translateZ ? -1.0F : 0.0F);
      poseStack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(90.0F));
      poseStack.translate(0.5F, 0.5F, 0.5F);
      poseStack.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(180.0F + direction.toYRot()));
      poseStack.translate(-0.5F, -0.5F, -0.5F);
   }

   public void getExtents(final Consumer<Vector3fc> output) {
      PoseStack poseStack = new PoseStack();
      preparePose(poseStack, false, Direction.SOUTH);
      this.headModel.root().getExtentsForGui(poseStack, output);
      poseStack.setIdentity();
      preparePose(poseStack, true, Direction.SOUTH);
      this.footModel.root().getExtentsForGui(poseStack, output);
   }
}
