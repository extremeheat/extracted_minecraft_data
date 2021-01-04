package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.util.Mth;

public class Deadmau5EarsLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
   public Deadmau5EarsLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> var1) {
      super(var1);
   }

   public void render(AbstractClientPlayer var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if ("deadmau5".equals(var1.getName().getString()) && var1.isSkinLoaded() && !var1.isInvisible()) {
         this.bindTexture(var1.getSkinTextureLocation());

         for(int var9 = 0; var9 < 2; ++var9) {
            float var10 = Mth.lerp(var4, var1.yRotO, var1.yRot) - Mth.lerp(var4, var1.yBodyRotO, var1.yBodyRot);
            float var11 = Mth.lerp(var4, var1.xRotO, var1.xRot);
            GlStateManager.pushMatrix();
            GlStateManager.rotatef(var10, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(var11, 1.0F, 0.0F, 0.0F);
            GlStateManager.translatef(0.375F * (float)(var9 * 2 - 1), 0.0F, 0.0F);
            GlStateManager.translatef(0.0F, -0.375F, 0.0F);
            GlStateManager.rotatef(-var11, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(-var10, 0.0F, 1.0F, 0.0F);
            float var12 = 1.3333334F;
            GlStateManager.scalef(1.3333334F, 1.3333334F, 1.3333334F);
            ((PlayerModel)this.getParentModel()).renderEars(0.0625F);
            GlStateManager.popMatrix();
         }

      }
   }

   public boolean colorsOnDamage() {
      return true;
   }
}
