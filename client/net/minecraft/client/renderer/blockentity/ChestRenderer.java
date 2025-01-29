package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.Vec3;

public class ChestRenderer<T extends BlockEntity & LidBlockEntity> implements BlockEntityRenderer<T> {
   private final ChestModel singleModel;
   private final ChestModel doubleLeftModel;
   private final ChestModel doubleRightModel;
   private final boolean xmasTextures = xmasTextures();

   public ChestRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.singleModel = new ChestModel(var1.bakeLayer(ModelLayers.CHEST));
      this.doubleLeftModel = new ChestModel(var1.bakeLayer(ModelLayers.DOUBLE_CHEST_LEFT));
      this.doubleRightModel = new ChestModel(var1.bakeLayer(ModelLayers.DOUBLE_CHEST_RIGHT));
   }

   public static boolean xmasTextures() {
      Calendar var0 = Calendar.getInstance();
      return var0.get(2) + 1 == 12 && var0.get(5) >= 24 && var0.get(5) <= 26;
   }

   public void render(T var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, Vec3 var7) {
      Level var8 = var1.getLevel();
      boolean var9 = var8 != null;
      BlockState var10 = var9 ? var1.getBlockState() : (BlockState)Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
      ChestType var11 = var10.hasProperty(ChestBlock.TYPE) ? (ChestType)var10.getValue(ChestBlock.TYPE) : ChestType.SINGLE;
      Block var12 = var10.getBlock();
      if (var12 instanceof AbstractChestBlock var13) {
         boolean var14 = var11 != ChestType.SINGLE;
         var3.pushPose();
         float var15 = ((Direction)var10.getValue(ChestBlock.FACING)).toYRot();
         var3.translate(0.5F, 0.5F, 0.5F);
         var3.mulPose(Axis.YP.rotationDegrees(-var15));
         var3.translate(-0.5F, -0.5F, -0.5F);
         DoubleBlockCombiner.NeighborCombineResult var16;
         if (var9) {
            var16 = var13.combine(var10, var8, var1.getBlockPos(), true);
         } else {
            var16 = DoubleBlockCombiner.Combiner::acceptNone;
         }

         float var17 = ((Float2FloatFunction)var16.apply(ChestBlock.opennessCombiner((LidBlockEntity)var1))).get(var2);
         var17 = 1.0F - var17;
         var17 = 1.0F - var17 * var17 * var17;
         int var18 = ((Int2IntFunction)var16.apply(new BrightnessCombiner())).applyAsInt(var5);
         Material var19 = Sheets.chooseMaterial(var1, var11, this.xmasTextures);
         VertexConsumer var20 = var19.buffer(var4, RenderType::entityCutout);
         if (var14) {
            if (var11 == ChestType.LEFT) {
               this.render(var3, var20, this.doubleLeftModel, var17, var18, var6);
            } else {
               this.render(var3, var20, this.doubleRightModel, var17, var18, var6);
            }
         } else {
            this.render(var3, var20, this.singleModel, var17, var18, var6);
         }

         var3.popPose();
      }
   }

   private void render(PoseStack var1, VertexConsumer var2, ChestModel var3, float var4, int var5, int var6) {
      var3.setupAnim(var4);
      var3.renderToBuffer(var1, var2, var5, var6);
   }
}
