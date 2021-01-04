package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;

public class StructureBlockRenderer extends BlockEntityRenderer<StructureBlockEntity> {
   public StructureBlockRenderer() {
      super();
   }

   public void render(StructureBlockEntity var1, double var2, double var4, double var6, float var8, int var9) {
      if (Minecraft.getInstance().player.canUseGameMasterBlocks() || Minecraft.getInstance().player.isSpectator()) {
         super.render(var1, var2, var4, var6, var8, var9);
         BlockPos var10 = var1.getStructurePos();
         BlockPos var11 = var1.getStructureSize();
         if (var11.getX() >= 1 && var11.getY() >= 1 && var11.getZ() >= 1) {
            if (var1.getMode() == StructureMode.SAVE || var1.getMode() == StructureMode.LOAD) {
               double var12 = 0.01D;
               double var14 = (double)var10.getX();
               double var16 = (double)var10.getZ();
               double var24 = var4 + (double)var10.getY() - 0.01D;
               double var30 = var24 + (double)var11.getY() + 0.02D;
               double var18;
               double var20;
               switch(var1.getMirror()) {
               case LEFT_RIGHT:
                  var18 = (double)var11.getX() + 0.02D;
                  var20 = -((double)var11.getZ() + 0.02D);
                  break;
               case FRONT_BACK:
                  var18 = -((double)var11.getX() + 0.02D);
                  var20 = (double)var11.getZ() + 0.02D;
                  break;
               default:
                  var18 = (double)var11.getX() + 0.02D;
                  var20 = (double)var11.getZ() + 0.02D;
               }

               double var22;
               double var26;
               double var28;
               double var32;
               switch(var1.getRotation()) {
               case CLOCKWISE_90:
                  var22 = var2 + (var20 < 0.0D ? var14 - 0.01D : var14 + 1.0D + 0.01D);
                  var26 = var6 + (var18 < 0.0D ? var16 + 1.0D + 0.01D : var16 - 0.01D);
                  var28 = var22 - var20;
                  var32 = var26 + var18;
                  break;
               case CLOCKWISE_180:
                  var22 = var2 + (var18 < 0.0D ? var14 - 0.01D : var14 + 1.0D + 0.01D);
                  var26 = var6 + (var20 < 0.0D ? var16 - 0.01D : var16 + 1.0D + 0.01D);
                  var28 = var22 - var18;
                  var32 = var26 - var20;
                  break;
               case COUNTERCLOCKWISE_90:
                  var22 = var2 + (var20 < 0.0D ? var14 + 1.0D + 0.01D : var14 - 0.01D);
                  var26 = var6 + (var18 < 0.0D ? var16 - 0.01D : var16 + 1.0D + 0.01D);
                  var28 = var22 + var20;
                  var32 = var26 - var18;
                  break;
               default:
                  var22 = var2 + (var18 < 0.0D ? var14 + 1.0D + 0.01D : var14 - 0.01D);
                  var26 = var6 + (var20 < 0.0D ? var16 + 1.0D + 0.01D : var16 - 0.01D);
                  var28 = var22 + var18;
                  var32 = var26 + var20;
               }

               boolean var34 = true;
               boolean var35 = true;
               boolean var36 = true;
               Tesselator var37 = Tesselator.getInstance();
               BufferBuilder var38 = var37.getBuilder();
               GlStateManager.disableFog();
               GlStateManager.disableLighting();
               GlStateManager.disableTexture();
               GlStateManager.enableBlend();
               GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
               this.setOverlayRenderState(true);
               if (var1.getMode() == StructureMode.SAVE || var1.getShowBoundingBox()) {
                  this.renderBox(var37, var38, var22, var24, var26, var28, var30, var32, 255, 223, 127);
               }

               if (var1.getMode() == StructureMode.SAVE && var1.getShowAir()) {
                  this.renderInvisibleBlocks(var1, var2, var4, var6, var10, var37, var38, true);
                  this.renderInvisibleBlocks(var1, var2, var4, var6, var10, var37, var38, false);
               }

               this.setOverlayRenderState(false);
               GlStateManager.lineWidth(1.0F);
               GlStateManager.enableLighting();
               GlStateManager.enableTexture();
               GlStateManager.enableDepthTest();
               GlStateManager.depthMask(true);
               GlStateManager.enableFog();
            }
         }
      }
   }

   private void renderInvisibleBlocks(StructureBlockEntity var1, double var2, double var4, double var6, BlockPos var8, Tesselator var9, BufferBuilder var10, boolean var11) {
      GlStateManager.lineWidth(var11 ? 3.0F : 1.0F);
      var10.begin(3, DefaultVertexFormat.POSITION_COLOR);
      Level var12 = var1.getLevel();
      BlockPos var13 = var1.getBlockPos();
      BlockPos var14 = var13.offset(var8);
      Iterator var15 = BlockPos.betweenClosed(var14, var14.offset(var1.getStructureSize()).offset(-1, -1, -1)).iterator();

      while(true) {
         BlockPos var16;
         boolean var18;
         boolean var19;
         do {
            if (!var15.hasNext()) {
               var9.end();
               return;
            }

            var16 = (BlockPos)var15.next();
            BlockState var17 = var12.getBlockState(var16);
            var18 = var17.isAir();
            var19 = var17.getBlock() == Blocks.STRUCTURE_VOID;
         } while(!var18 && !var19);

         float var20 = var18 ? 0.05F : 0.0F;
         double var21 = (double)((float)(var16.getX() - var13.getX()) + 0.45F) + var2 - (double)var20;
         double var23 = (double)((float)(var16.getY() - var13.getY()) + 0.45F) + var4 - (double)var20;
         double var25 = (double)((float)(var16.getZ() - var13.getZ()) + 0.45F) + var6 - (double)var20;
         double var27 = (double)((float)(var16.getX() - var13.getX()) + 0.55F) + var2 + (double)var20;
         double var29 = (double)((float)(var16.getY() - var13.getY()) + 0.55F) + var4 + (double)var20;
         double var31 = (double)((float)(var16.getZ() - var13.getZ()) + 0.55F) + var6 + (double)var20;
         if (var11) {
            LevelRenderer.addChainedLineBoxVertices(var10, var21, var23, var25, var27, var29, var31, 0.0F, 0.0F, 0.0F, 1.0F);
         } else if (var18) {
            LevelRenderer.addChainedLineBoxVertices(var10, var21, var23, var25, var27, var29, var31, 0.5F, 0.5F, 1.0F, 1.0F);
         } else {
            LevelRenderer.addChainedLineBoxVertices(var10, var21, var23, var25, var27, var29, var31, 1.0F, 0.25F, 0.25F, 1.0F);
         }
      }
   }

   private void renderBox(Tesselator var1, BufferBuilder var2, double var3, double var5, double var7, double var9, double var11, double var13, int var15, int var16, int var17) {
      GlStateManager.lineWidth(2.0F);
      var2.begin(3, DefaultVertexFormat.POSITION_COLOR);
      var2.vertex(var3, var5, var7).color((float)var16, (float)var16, (float)var16, 0.0F).endVertex();
      var2.vertex(var3, var5, var7).color(var16, var16, var16, var15).endVertex();
      var2.vertex(var9, var5, var7).color(var16, var17, var17, var15).endVertex();
      var2.vertex(var9, var5, var13).color(var16, var16, var16, var15).endVertex();
      var2.vertex(var3, var5, var13).color(var16, var16, var16, var15).endVertex();
      var2.vertex(var3, var5, var7).color(var17, var17, var16, var15).endVertex();
      var2.vertex(var3, var11, var7).color(var17, var16, var17, var15).endVertex();
      var2.vertex(var9, var11, var7).color(var16, var16, var16, var15).endVertex();
      var2.vertex(var9, var11, var13).color(var16, var16, var16, var15).endVertex();
      var2.vertex(var3, var11, var13).color(var16, var16, var16, var15).endVertex();
      var2.vertex(var3, var11, var7).color(var16, var16, var16, var15).endVertex();
      var2.vertex(var3, var11, var13).color(var16, var16, var16, var15).endVertex();
      var2.vertex(var3, var5, var13).color(var16, var16, var16, var15).endVertex();
      var2.vertex(var9, var5, var13).color(var16, var16, var16, var15).endVertex();
      var2.vertex(var9, var11, var13).color(var16, var16, var16, var15).endVertex();
      var2.vertex(var9, var11, var7).color(var16, var16, var16, var15).endVertex();
      var2.vertex(var9, var5, var7).color(var16, var16, var16, var15).endVertex();
      var2.vertex(var9, var5, var7).color((float)var16, (float)var16, (float)var16, 0.0F).endVertex();
      var1.end();
      GlStateManager.lineWidth(1.0F);
   }

   public boolean shouldRenderOffScreen(StructureBlockEntity var1) {
      return true;
   }
}
