package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;

public class StructureBlockRenderer implements BlockEntityRenderer<StructureBlockEntity> {
   public StructureBlockRenderer(BlockEntityRendererProvider.Context var1) {
      super();
   }

   public void render(StructureBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      if (Minecraft.getInstance().player.canUseGameMasterBlocks() || Minecraft.getInstance().player.isSpectator()) {
         BlockPos var7 = var1.getStructurePos();
         Vec3i var8 = var1.getStructureSize();
         if (var8.getX() >= 1 && var8.getY() >= 1 && var8.getZ() >= 1) {
            if (var1.getMode() == StructureMode.SAVE || var1.getMode() == StructureMode.LOAD) {
               double var9 = (double)var7.getX();
               double var11 = (double)var7.getZ();
               double var19 = (double)var7.getY();
               double var25 = var19 + (double)var8.getY();
               double var13;
               double var15;
               switch (var1.getMirror()) {
                  case LEFT_RIGHT:
                     var13 = (double)var8.getX();
                     var15 = (double)(-var8.getZ());
                     break;
                  case FRONT_BACK:
                     var13 = (double)(-var8.getX());
                     var15 = (double)var8.getZ();
                     break;
                  default:
                     var13 = (double)var8.getX();
                     var15 = (double)var8.getZ();
               }

               double var17;
               double var21;
               double var23;
               double var27;
               switch (var1.getRotation()) {
                  case CLOCKWISE_90:
                     var17 = var15 < 0.0 ? var9 : var9 + 1.0;
                     var21 = var13 < 0.0 ? var11 + 1.0 : var11;
                     var23 = var17 - var15;
                     var27 = var21 + var13;
                     break;
                  case CLOCKWISE_180:
                     var17 = var13 < 0.0 ? var9 : var9 + 1.0;
                     var21 = var15 < 0.0 ? var11 : var11 + 1.0;
                     var23 = var17 - var13;
                     var27 = var21 - var15;
                     break;
                  case COUNTERCLOCKWISE_90:
                     var17 = var15 < 0.0 ? var9 + 1.0 : var9;
                     var21 = var13 < 0.0 ? var11 : var11 + 1.0;
                     var23 = var17 + var15;
                     var27 = var21 - var13;
                     break;
                  default:
                     var17 = var13 < 0.0 ? var9 + 1.0 : var9;
                     var21 = var15 < 0.0 ? var11 + 1.0 : var11;
                     var23 = var17 + var13;
                     var27 = var21 + var15;
               }

               float var29 = 1.0F;
               float var30 = 0.9F;
               float var31 = 0.5F;
               VertexConsumer var32 = var4.getBuffer(RenderType.lines());
               if (var1.getMode() == StructureMode.SAVE || var1.getShowBoundingBox()) {
                  LevelRenderer.renderLineBox(var3, var32, var17, var19, var21, var23, var25, var27, 0.9F, 0.9F, 0.9F, 1.0F, 0.5F, 0.5F, 0.5F);
               }

               if (var1.getMode() == StructureMode.SAVE && var1.getShowAir()) {
                  this.renderInvisibleBlocks(var1, var32, var7, var3);
               }

            }
         }
      }
   }

   private void renderInvisibleBlocks(StructureBlockEntity var1, VertexConsumer var2, BlockPos var3, PoseStack var4) {
      Level var5 = var1.getLevel();
      BlockPos var6 = var1.getBlockPos();
      BlockPos var7 = var6.offset(var3);
      Iterator var8 = BlockPos.betweenClosed(var7, var7.offset(var1.getStructureSize()).offset(-1, -1, -1)).iterator();

      while(true) {
         BlockPos var9;
         boolean var11;
         boolean var12;
         boolean var13;
         boolean var14;
         boolean var15;
         do {
            if (!var8.hasNext()) {
               return;
            }

            var9 = (BlockPos)var8.next();
            BlockState var10 = var5.getBlockState(var9);
            var11 = var10.isAir();
            var12 = var10.is(Blocks.STRUCTURE_VOID);
            var13 = var10.is(Blocks.BARRIER);
            var14 = var10.is(Blocks.LIGHT);
            var15 = var12 || var13 || var14;
         } while(!var11 && !var15);

         float var16 = var11 ? 0.05F : 0.0F;
         double var17 = (double)((float)(var9.getX() - var6.getX()) + 0.45F - var16);
         double var19 = (double)((float)(var9.getY() - var6.getY()) + 0.45F - var16);
         double var21 = (double)((float)(var9.getZ() - var6.getZ()) + 0.45F - var16);
         double var23 = (double)((float)(var9.getX() - var6.getX()) + 0.55F + var16);
         double var25 = (double)((float)(var9.getY() - var6.getY()) + 0.55F + var16);
         double var27 = (double)((float)(var9.getZ() - var6.getZ()) + 0.55F + var16);
         if (var11) {
            LevelRenderer.renderLineBox(var4, var2, var17, var19, var21, var23, var25, var27, 0.5F, 0.5F, 1.0F, 1.0F, 0.5F, 0.5F, 1.0F);
         } else if (var12) {
            LevelRenderer.renderLineBox(var4, var2, var17, var19, var21, var23, var25, var27, 1.0F, 0.75F, 0.75F, 1.0F, 1.0F, 0.75F, 0.75F);
         } else if (var13) {
            LevelRenderer.renderLineBox(var4, var2, var17, var19, var21, var23, var25, var27, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F);
         } else if (var14) {
            LevelRenderer.renderLineBox(var4, var2, var17, var19, var21, var23, var25, var27, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F);
         }
      }
   }

   public boolean shouldRenderOffScreen(StructureBlockEntity var1) {
      return true;
   }

   public int getViewDistance() {
      return 96;
   }
}
