package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.core.Direction;
import net.minecraft.util.Unit;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class BedRenderer implements BlockEntityRenderer<BedBlockEntity> {
   private final MaterialSet materials;
   private final Model.Simple headModel;
   private final Model.Simple footModel;

   public BedRenderer(BlockEntityRendererProvider.Context var1) {
      this(var1.materials(), var1.entityModelSet());
   }

   public BedRenderer(SpecialModelRenderer.BakingContext var1) {
      this(var1.materials(), var1.entityModelSet());
   }

   public BedRenderer(MaterialSet var1, EntityModelSet var2) {
      super();
      this.materials = var1;
      this.headModel = new Model.Simple(var2.bakeLayer(ModelLayers.BED_HEAD), RenderType::entitySolid);
      this.footModel = new Model.Simple(var2.bakeLayer(ModelLayers.BED_FOOT), RenderType::entitySolid);
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

   public void submit(BedBlockEntity var1, float var2, PoseStack var3, int var4, int var5, Vec3 var6, @Nullable ModelFeatureRenderer.CrumblingOverlay var7, SubmitNodeCollector var8) {
      Level var9 = var1.getLevel();
      if (var9 != null) {
         Material var10 = Sheets.getBedMaterial(var1.getColor());
         BlockState var11 = var1.getBlockState();
         DoubleBlockCombiner.NeighborCombineResult var12 = DoubleBlockCombiner.combineWithNeigbour(BlockEntityType.BED, BedBlock::getBlockType, BedBlock::getConnectedDirection, ChestBlock.FACING, var11, var9, var1.getBlockPos(), (var0, var1x) -> false);
         int var13 = ((Int2IntFunction)var12.apply(new BrightnessCombiner())).get(var4);
         this.submitPiece(var3, var8, var11.getValue(BedBlock.PART) == BedPart.HEAD ? this.headModel : this.footModel, (Direction)var11.getValue(BedBlock.FACING), var10, var13, var5, false, var7);
      }

   }

   public void submitSpecial(PoseStack var1, SubmitNodeCollector var2, int var3, int var4, Material var5) {
      this.submitPiece(var1, var2, this.headModel, Direction.SOUTH, var5, var3, var4, false, (ModelFeatureRenderer.CrumblingOverlay)null);
      this.submitPiece(var1, var2, this.footModel, Direction.SOUTH, var5, var3, var4, true, (ModelFeatureRenderer.CrumblingOverlay)null);
   }

   private void submitPiece(PoseStack var1, SubmitNodeCollector var2, Model.Simple var3, Direction var4, Material var5, int var6, int var7, boolean var8, @Nullable ModelFeatureRenderer.CrumblingOverlay var9) {
      var1.pushPose();
      preparePose(var1, var8, var4);
      var2.submitModel(var3, Unit.INSTANCE, var1, var5.renderType(RenderType::entitySolid), var6, var7, -1, this.materials.get(var5), 0, var9);
      var1.popPose();
   }

   private static void preparePose(PoseStack var0, boolean var1, Direction var2) {
      var0.translate(0.0F, 0.5625F, var1 ? -1.0F : 0.0F);
      var0.mulPose((Quaternionfc)Axis.XP.rotationDegrees(90.0F));
      var0.translate(0.5F, 0.5F, 0.5F);
      var0.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(180.0F + var2.toYRot()));
      var0.translate(-0.5F, -0.5F, -0.5F);
   }

   public void getExtents(Set<Vector3f> var1) {
      PoseStack var2 = new PoseStack();
      preparePose(var2, false, Direction.SOUTH);
      this.headModel.root().getExtentsForGui(var2, var1);
      var2.setIdentity();
      preparePose(var2, true, Direction.SOUTH);
      this.footModel.root().getExtentsForGui(var2, var1);
   }
}
