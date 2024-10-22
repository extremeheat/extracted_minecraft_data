package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.Calendar;
import net.minecraft.client.model.ChestModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

public class ChestRenderer<T extends BlockEntity & LidBlockEntity> implements BlockEntityRenderer<T> {
   private final ChestModel singleModel;
   private final ChestModel doubleLeftModel;
   private final ChestModel doubleRightModel;
   private boolean xmasTextures;

   public ChestRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      Calendar var2 = Calendar.getInstance();
      if (var2.get(2) + 1 == 12 && var2.get(5) >= 24 && var2.get(5) <= 26) {
         this.xmasTextures = true;
      }

      this.singleModel = new ChestModel(var1.bakeLayer(ModelLayers.CHEST));
      this.doubleLeftModel = new ChestModel(var1.bakeLayer(ModelLayers.DOUBLE_CHEST_LEFT));
      this.doubleRightModel = new ChestModel(var1.bakeLayer(ModelLayers.DOUBLE_CHEST_RIGHT));
   }

   @Override
   public void render(T var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      Level var7 = var1.getLevel();
      boolean var8 = var7 != null;
      BlockState var9 = var8 ? var1.getBlockState() : Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
      ChestType var10 = var9.hasProperty(ChestBlock.TYPE) ? var9.getValue(ChestBlock.TYPE) : ChestType.SINGLE;
      if (var9.getBlock() instanceof AbstractChestBlock var12) {
         boolean var13 = var10 != ChestType.SINGLE;
         var3.pushPose();
         float var14 = var9.getValue(ChestBlock.FACING).toYRot();
         var3.translate(0.5F, 0.5F, 0.5F);
         var3.mulPose(Axis.YP.rotationDegrees(-var14));
         var3.translate(-0.5F, -0.5F, -0.5F);
         DoubleBlockCombiner.NeighborCombineResult var15;
         if (var8) {
            var15 = var12.combine(var9, var7, var1.getBlockPos(), true);
         } else {
            var15 = DoubleBlockCombiner.Combiner::acceptNone;
         }

         float var16 = var15.apply(ChestBlock.opennessCombiner((LidBlockEntity)var1)).get(var2);
         var16 = 1.0F - var16;
         var16 = 1.0F - var16 * var16 * var16;
         int var17 = var15.apply(new BrightnessCombiner()).applyAsInt(var5);
         Material var18 = Sheets.chooseMaterial(var1, var10, this.xmasTextures);
         VertexConsumer var19 = var18.buffer(var4, RenderType::entityCutout);
         if (var13) {
            if (var10 == ChestType.LEFT) {
               this.render(var3, var19, this.doubleLeftModel, var16, var17, var6);
            } else {
               this.render(var3, var19, this.doubleRightModel, var16, var17, var6);
            }
         } else {
            this.render(var3, var19, this.singleModel, var16, var17, var6);
         }

         var3.popPose();
      }
   }

   private void render(PoseStack var1, VertexConsumer var2, ChestModel var3, float var4, int var5, int var6) {
      var3.setupAnim(var4);
      var3.renderToBuffer(var1, var2, var5, var6);
   }
}
