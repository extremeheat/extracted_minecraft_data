package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.level.block.state.BlockState;

public class MushroomCowMushroomLayer<T extends MushroomCow> extends RenderLayer<T, CowModel<T>> {
   public MushroomCowMushroomLayer(RenderLayerParent<T, CowModel<T>> var1) {
      super(var1);
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (!var1.isBaby() && !var1.isInvisible()) {
         BlockState var9 = var1.getMushroomType().getBlockState();
         this.bindTexture(TextureAtlas.LOCATION_BLOCKS);
         GlStateManager.enableCull();
         GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(1.0F, -1.0F, 1.0F);
         GlStateManager.translatef(0.2F, 0.35F, 0.5F);
         GlStateManager.rotatef(42.0F, 0.0F, 1.0F, 0.0F);
         BlockRenderDispatcher var10 = Minecraft.getInstance().getBlockRenderer();
         GlStateManager.pushMatrix();
         GlStateManager.translatef(-0.5F, -0.5F, 0.5F);
         var10.renderSingleBlock(var9, 1.0F);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.1F, 0.0F, -0.6F);
         GlStateManager.rotatef(42.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.translatef(-0.5F, -0.5F, 0.5F);
         var10.renderSingleBlock(var9, 1.0F);
         GlStateManager.popMatrix();
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         ((CowModel)this.getParentModel()).getHead().translateTo(0.0625F);
         GlStateManager.scalef(1.0F, -1.0F, 1.0F);
         GlStateManager.translatef(0.0F, 0.7F, -0.2F);
         GlStateManager.rotatef(12.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.translatef(-0.5F, -0.5F, 0.5F);
         var10.renderSingleBlock(var9, 1.0F);
         GlStateManager.popMatrix();
         GlStateManager.cullFace(GlStateManager.CullFace.BACK);
         GlStateManager.disableCull();
      }
   }

   public boolean colorsOnDamage() {
      return true;
   }
}
