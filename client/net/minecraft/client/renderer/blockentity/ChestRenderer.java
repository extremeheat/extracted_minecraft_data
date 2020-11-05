package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Calendar;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
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
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

public class ChestRenderer<T extends BlockEntity & LidBlockEntity> implements BlockEntityRenderer<T> {
   private final ModelPart lid;
   private final ModelPart bottom;
   private final ModelPart lock;
   private final ModelPart doubleLeftLid;
   private final ModelPart doubleLeftBottom;
   private final ModelPart doubleLeftLock;
   private final ModelPart doubleRightLid;
   private final ModelPart doubleRightBottom;
   private final ModelPart doubleRightLock;
   private boolean xmasTextures;

   public ChestRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      Calendar var2 = Calendar.getInstance();
      if (var2.get(2) + 1 == 12 && var2.get(5) >= 24 && var2.get(5) <= 26) {
         this.xmasTextures = true;
      }

      ModelPart var3 = var1.getLayer(ModelLayers.CHEST);
      this.bottom = var3.getChild("bottom");
      this.lid = var3.getChild("lid");
      this.lock = var3.getChild("lock");
      ModelPart var4 = var1.getLayer(ModelLayers.DOUBLE_CHEST_LEFT);
      this.doubleLeftBottom = var4.getChild("bottom");
      this.doubleLeftLid = var4.getChild("lid");
      this.doubleLeftLock = var4.getChild("lock");
      ModelPart var5 = var1.getLayer(ModelLayers.DOUBLE_CHEST_RIGHT);
      this.doubleRightBottom = var5.getChild("bottom");
      this.doubleRightLid = var5.getChild("lid");
      this.doubleRightLock = var5.getChild("lock");
   }

   public static LayerDefinition createSingleBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F), PartPose.ZERO);
      var1.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
      var1.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 8.0F, 0.0F));
      return LayerDefinition.create(var0, 64, 64);
   }

   public static LayerDefinition createDoubleBodyRightLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(1.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F), PartPose.ZERO);
      var1.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
      var1.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(15.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 8.0F, 0.0F));
      return LayerDefinition.create(var0, 64, 64);
   }

   public static LayerDefinition createDoubleBodyLeftLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(0.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F), PartPose.ZERO);
      var1.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
      var1.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 8.0F, 0.0F));
      return LayerDefinition.create(var0, 64, 64);
   }

   public void render(T var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      Level var7 = var1.getLevel();
      boolean var8 = var7 != null;
      BlockState var9 = var8 ? var1.getBlockState() : (BlockState)Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
      ChestType var10 = var9.hasProperty(ChestBlock.TYPE) ? (ChestType)var9.getValue(ChestBlock.TYPE) : ChestType.SINGLE;
      Block var11 = var9.getBlock();
      if (var11 instanceof AbstractChestBlock) {
         AbstractChestBlock var12 = (AbstractChestBlock)var11;
         boolean var13 = var10 != ChestType.SINGLE;
         var3.pushPose();
         float var14 = ((Direction)var9.getValue(ChestBlock.FACING)).toYRot();
         var3.translate(0.5D, 0.5D, 0.5D);
         var3.mulPose(Vector3f.YP.rotationDegrees(-var14));
         var3.translate(-0.5D, -0.5D, -0.5D);
         DoubleBlockCombiner.NeighborCombineResult var15;
         if (var8) {
            var15 = var12.combine(var9, var7, var1.getBlockPos(), true);
         } else {
            var15 = DoubleBlockCombiner.Combiner::acceptNone;
         }

         float var16 = ((Float2FloatFunction)var15.apply(ChestBlock.opennessCombiner((LidBlockEntity)var1))).get(var2);
         var16 = 1.0F - var16;
         var16 = 1.0F - var16 * var16 * var16;
         int var17 = ((Int2IntFunction)var15.apply(new BrightnessCombiner())).applyAsInt(var5);
         Material var18 = Sheets.chooseMaterial(var1, var10, this.xmasTextures);
         VertexConsumer var19 = var18.buffer(var4, RenderType::entityCutout);
         if (var13) {
            if (var10 == ChestType.LEFT) {
               this.render(var3, var19, this.doubleLeftLid, this.doubleLeftLock, this.doubleLeftBottom, var16, var17, var6);
            } else {
               this.render(var3, var19, this.doubleRightLid, this.doubleRightLock, this.doubleRightBottom, var16, var17, var6);
            }
         } else {
            this.render(var3, var19, this.lid, this.lock, this.bottom, var16, var17, var6);
         }

         var3.popPose();
      }
   }

   private void render(PoseStack var1, VertexConsumer var2, ModelPart var3, ModelPart var4, ModelPart var5, float var6, int var7, int var8) {
      var3.xRot = -(var6 * 1.5707964F);
      var4.xRot = var3.xRot;
      var3.render(var1, var2, var7, var8);
      var4.render(var1, var2, var7, var8);
      var5.render(var1, var2, var7, var8);
   }
}
