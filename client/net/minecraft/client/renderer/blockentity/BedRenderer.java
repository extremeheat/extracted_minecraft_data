package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

public class BedRenderer implements BlockEntityRenderer<BedBlockEntity> {
   private final Model headModel;
   private final Model footModel;

   public BedRenderer(BlockEntityRendererProvider.Context var1) {
      this(var1.getModelSet());
   }

   public BedRenderer(EntityModelSet var1) {
      super();
      this.headModel = new Model.Simple(var1.bakeLayer(ModelLayers.BED_HEAD), RenderType::entitySolid);
      this.footModel = new Model.Simple(var1.bakeLayer(ModelLayers.BED_FOOT), RenderType::entitySolid);
   }

   public static LayerDefinition createHeadLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F), PartPose.ZERO);
      var1.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(50, 6).addBox(0.0F, 6.0F, 0.0F, 3.0F, 3.0F, 3.0F), PartPose.rotation(1.5707964F, 0.0F, 1.5707964F));
      var1.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(50, 18).addBox(-16.0F, 6.0F, 0.0F, 3.0F, 3.0F, 3.0F), PartPose.rotation(1.5707964F, 0.0F, 3.1415927F));
      return LayerDefinition.create(var0, 64, 64);
   }

   public static LayerDefinition createFootLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 22).addBox(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F), PartPose.ZERO);
      var1.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(50, 0).addBox(0.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F), PartPose.rotation(1.5707964F, 0.0F, 0.0F));
      var1.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(50, 12).addBox(-16.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F), PartPose.rotation(1.5707964F, 0.0F, 4.712389F));
      return LayerDefinition.create(var0, 64, 64);
   }

   public void render(BedBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      Level var7 = var1.getLevel();
      if (var7 != null) {
         Material var8 = Sheets.getBedMaterial(var1.getColor());
         BlockState var9 = var1.getBlockState();
         DoubleBlockCombiner.NeighborCombineResult var10 = DoubleBlockCombiner.combineWithNeigbour(BlockEntityType.BED, BedBlock::getBlockType, BedBlock::getConnectedDirection, ChestBlock.FACING, var9, var7, var1.getBlockPos(), (var0, var1x) -> {
            return false;
         });
         int var11 = ((Int2IntFunction)var10.apply(new BrightnessCombiner())).get(var5);
         this.renderPiece(var3, var4, var9.getValue(BedBlock.PART) == BedPart.HEAD ? this.headModel : this.footModel, (Direction)var9.getValue(BedBlock.FACING), var8, var11, var6, false);
      }

   }

   public void renderInHand(PoseStack var1, MultiBufferSource var2, int var3, int var4, Material var5) {
      this.renderPiece(var1, var2, this.headModel, Direction.SOUTH, var5, var3, var4, false);
      this.renderPiece(var1, var2, this.footModel, Direction.SOUTH, var5, var3, var4, true);
   }

   private void renderPiece(PoseStack var1, MultiBufferSource var2, Model var3, Direction var4, Material var5, int var6, int var7, boolean var8) {
      var1.pushPose();
      var1.translate(0.0F, 0.5625F, var8 ? -1.0F : 0.0F);
      var1.mulPose(Axis.XP.rotationDegrees(90.0F));
      var1.translate(0.5F, 0.5F, 0.5F);
      var1.mulPose(Axis.ZP.rotationDegrees(180.0F + var4.toYRot()));
      var1.translate(-0.5F, -0.5F, -0.5F);
      VertexConsumer var9 = var5.buffer(var2, RenderType::entitySolid);
      var3.renderToBuffer(var1, var9, var6, var7);
      var1.popPose();
   }
}
