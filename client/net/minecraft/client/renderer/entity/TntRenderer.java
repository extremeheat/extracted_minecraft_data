package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.block.Blocks;

public class TntRenderer extends EntityRenderer<PrimedTnt> {
   public TntRenderer(EntityRenderDispatcher var1) {
      super(var1);
      this.shadowRadius = 0.5F;
   }

   public void render(PrimedTnt var1, double var2, double var4, double var6, float var8, float var9) {
      BlockRenderDispatcher var10 = Minecraft.getInstance().getBlockRenderer();
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)var2, (float)var4 + 0.5F, (float)var6);
      float var11;
      if ((float)var1.getLife() - var9 + 1.0F < 10.0F) {
         var11 = 1.0F - ((float)var1.getLife() - var9 + 1.0F) / 10.0F;
         var11 = Mth.clamp(var11, 0.0F, 1.0F);
         var11 *= var11;
         var11 *= var11;
         float var12 = 1.0F + var11 * 0.3F;
         GlStateManager.scalef(var12, var12, var12);
      }

      var11 = (1.0F - ((float)var1.getLife() - var9 + 1.0F) / 100.0F) * 0.8F;
      this.bindTexture(var1);
      GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.translatef(-0.5F, -0.5F, 0.5F);
      var10.renderSingleBlock(Blocks.TNT.defaultBlockState(), var1.getBrightness());
      GlStateManager.translatef(0.0F, 0.0F, 1.0F);
      if (this.solidRender) {
         GlStateManager.enableColorMaterial();
         GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(var1));
         var10.renderSingleBlock(Blocks.TNT.defaultBlockState(), 1.0F);
         GlStateManager.tearDownSolidRenderingTextureCombine();
         GlStateManager.disableColorMaterial();
      } else if (var1.getLife() / 5 % 2 == 0) {
         GlStateManager.disableTexture();
         GlStateManager.disableLighting();
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, var11);
         GlStateManager.polygonOffset(-3.0F, -3.0F);
         GlStateManager.enablePolygonOffset();
         var10.renderSingleBlock(Blocks.TNT.defaultBlockState(), 1.0F);
         GlStateManager.polygonOffset(0.0F, 0.0F);
         GlStateManager.disablePolygonOffset();
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableBlend();
         GlStateManager.enableLighting();
         GlStateManager.enableTexture();
      }

      GlStateManager.popMatrix();
      super.render(var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation getTextureLocation(PrimedTnt var1) {
      return TextureAtlas.LOCATION_BLOCKS;
   }
}
