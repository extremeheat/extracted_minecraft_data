package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Calendar;
import net.minecraft.client.model.geom.ModelPart;
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

public class ChestRenderer<T extends BlockEntity & LidBlockEntity> extends BlockEntityRenderer<T> {
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

   public ChestRenderer(BlockEntityRenderDispatcher var1) {
      super(var1);
      Calendar var2 = Calendar.getInstance();
      if (var2.get(2) + 1 == 12 && var2.get(5) >= 24 && var2.get(5) <= 26) {
         this.xmasTextures = true;
      }

      this.bottom = new ModelPart(64, 64, 0, 19);
      this.bottom.addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, 0.0F);
      this.lid = new ModelPart(64, 64, 0, 0);
      this.lid.addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F);
      this.lid.y = 9.0F;
      this.lid.z = 1.0F;
      this.lock = new ModelPart(64, 64, 0, 0);
      this.lock.addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
      this.lock.y = 8.0F;
      this.doubleLeftBottom = new ModelPart(64, 64, 0, 19);
      this.doubleLeftBottom.addBox(1.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, 0.0F);
      this.doubleLeftLid = new ModelPart(64, 64, 0, 0);
      this.doubleLeftLid.addBox(1.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, 0.0F);
      this.doubleLeftLid.y = 9.0F;
      this.doubleLeftLid.z = 1.0F;
      this.doubleLeftLock = new ModelPart(64, 64, 0, 0);
      this.doubleLeftLock.addBox(15.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F, 0.0F);
      this.doubleLeftLock.y = 8.0F;
      this.doubleRightBottom = new ModelPart(64, 64, 0, 19);
      this.doubleRightBottom.addBox(0.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, 0.0F);
      this.doubleRightLid = new ModelPart(64, 64, 0, 0);
      this.doubleRightLid.addBox(0.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, 0.0F);
      this.doubleRightLid.y = 9.0F;
      this.doubleRightLid.z = 1.0F;
      this.doubleRightLock = new ModelPart(64, 64, 0, 0);
      this.doubleRightLock.addBox(0.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F, 0.0F);
      this.doubleRightLock.y = 8.0F;
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
               this.render(var3, var19, this.doubleRightLid, this.doubleRightLock, this.doubleRightBottom, var16, var17, var6);
            } else {
               this.render(var3, var19, this.doubleLeftLid, this.doubleLeftLock, this.doubleLeftBottom, var16, var17, var6);
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
