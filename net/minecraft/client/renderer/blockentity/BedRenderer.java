package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.client.model.geom.ModelPart;
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

public class BedRenderer extends BlockEntityRenderer {
   private final ModelPart headPiece = new ModelPart(64, 64, 0, 0);
   private final ModelPart footPiece;
   private final ModelPart[] legs = new ModelPart[4];

   public BedRenderer(BlockEntityRenderDispatcher var1) {
      super(var1);
      this.headPiece.addBox(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F, 0.0F);
      this.footPiece = new ModelPart(64, 64, 0, 22);
      this.footPiece.addBox(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F, 0.0F);
      this.legs[0] = new ModelPart(64, 64, 50, 0);
      this.legs[1] = new ModelPart(64, 64, 50, 6);
      this.legs[2] = new ModelPart(64, 64, 50, 12);
      this.legs[3] = new ModelPart(64, 64, 50, 18);
      this.legs[0].addBox(0.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F);
      this.legs[1].addBox(0.0F, 6.0F, 0.0F, 3.0F, 3.0F, 3.0F);
      this.legs[2].addBox(-16.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F);
      this.legs[3].addBox(-16.0F, 6.0F, 0.0F, 3.0F, 3.0F, 3.0F);
      this.legs[0].xRot = 1.5707964F;
      this.legs[1].xRot = 1.5707964F;
      this.legs[2].xRot = 1.5707964F;
      this.legs[3].xRot = 1.5707964F;
      this.legs[0].zRot = 0.0F;
      this.legs[1].zRot = 1.5707964F;
      this.legs[2].zRot = 4.712389F;
      this.legs[3].zRot = 3.1415927F;
   }

   public void render(BedBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      Material var7 = Sheets.BED_TEXTURES[var1.getColor().getId()];
      Level var8 = var1.getLevel();
      if (var8 != null) {
         BlockState var9 = var1.getBlockState();
         DoubleBlockCombiner.NeighborCombineResult var10 = DoubleBlockCombiner.combineWithNeigbour(BlockEntityType.BED, BedBlock::getBlockType, BedBlock::getConnectedDirection, ChestBlock.FACING, var9, var8, var1.getBlockPos(), (var0, var1x) -> {
            return false;
         });
         int var11 = ((Int2IntFunction)var10.apply(new BrightnessCombiner())).get(var5);
         this.renderPiece(var3, var4, var9.getValue(BedBlock.PART) == BedPart.HEAD, (Direction)var9.getValue(BedBlock.FACING), var7, var11, var6, false);
      } else {
         this.renderPiece(var3, var4, true, Direction.SOUTH, var7, var5, var6, false);
         this.renderPiece(var3, var4, false, Direction.SOUTH, var7, var5, var6, true);
      }

   }

   private void renderPiece(PoseStack var1, MultiBufferSource var2, boolean var3, Direction var4, Material var5, int var6, int var7, boolean var8) {
      this.headPiece.visible = var3;
      this.footPiece.visible = !var3;
      this.legs[0].visible = !var3;
      this.legs[1].visible = var3;
      this.legs[2].visible = !var3;
      this.legs[3].visible = var3;
      var1.pushPose();
      var1.translate(0.0D, 0.5625D, var8 ? -1.0D : 0.0D);
      var1.mulPose(Vector3f.XP.rotationDegrees(90.0F));
      var1.translate(0.5D, 0.5D, 0.5D);
      var1.mulPose(Vector3f.ZP.rotationDegrees(180.0F + var4.toYRot()));
      var1.translate(-0.5D, -0.5D, -0.5D);
      VertexConsumer var9 = var5.buffer(var2, RenderType::entitySolid);
      this.headPiece.render(var1, var9, var6, var7);
      this.footPiece.render(var1, var9, var6, var7);
      this.legs[0].render(var1, var9, var6, var7);
      this.legs[1].render(var1, var9, var6, var7);
      this.legs[2].render(var1, var9, var6, var7);
      this.legs[3].render(var1, var9, var6, var7);
      var1.popPose();
   }
}
