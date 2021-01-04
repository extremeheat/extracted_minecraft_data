package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class FallingBlockRenderer extends EntityRenderer<FallingBlockEntity> {
   public FallingBlockRenderer(EntityRenderDispatcher var1) {
      super(var1);
      this.shadowRadius = 0.5F;
   }

   public void render(FallingBlockEntity var1, double var2, double var4, double var6, float var8, float var9) {
      BlockState var10 = var1.getBlockState();
      if (var10.getRenderShape() == RenderShape.MODEL) {
         Level var11 = var1.getLevel();
         if (var10 != var11.getBlockState(new BlockPos(var1)) && var10.getRenderShape() != RenderShape.INVISIBLE) {
            this.bindTexture(TextureAtlas.LOCATION_BLOCKS);
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            Tesselator var12 = Tesselator.getInstance();
            BufferBuilder var13 = var12.getBuilder();
            if (this.solidRender) {
               GlStateManager.enableColorMaterial();
               GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(var1));
            }

            var13.begin(7, DefaultVertexFormat.BLOCK);
            BlockPos var14 = new BlockPos(var1.x, var1.getBoundingBox().maxY, var1.z);
            GlStateManager.translatef((float)(var2 - (double)var14.getX() - 0.5D), (float)(var4 - (double)var14.getY()), (float)(var6 - (double)var14.getZ() - 0.5D));
            BlockRenderDispatcher var15 = Minecraft.getInstance().getBlockRenderer();
            var15.getModelRenderer().tesselateBlock(var11, var15.getBlockModel(var10), var10, var14, var13, false, new Random(), var10.getSeed(var1.getStartPos()));
            var12.end();
            if (this.solidRender) {
               GlStateManager.tearDownSolidRenderingTextureCombine();
               GlStateManager.disableColorMaterial();
            }

            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
            super.render(var1, var2, var4, var6, var8, var9);
         }
      }
   }

   protected ResourceLocation getTextureLocation(FallingBlockEntity var1) {
      return TextureAtlas.LOCATION_BLOCKS;
   }
}
