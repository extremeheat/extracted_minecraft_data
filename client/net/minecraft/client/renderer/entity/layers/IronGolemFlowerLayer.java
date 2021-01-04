package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.level.block.Blocks;

public class IronGolemFlowerLayer extends RenderLayer<IronGolem, IronGolemModel<IronGolem>> {
   public IronGolemFlowerLayer(RenderLayerParent<IronGolem, IronGolemModel<IronGolem>> var1) {
      super(var1);
   }

   public void render(IronGolem var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (var1.getOfferFlowerTick() != 0) {
         GlStateManager.enableRescaleNormal();
         GlStateManager.pushMatrix();
         GlStateManager.rotatef(5.0F + 180.0F * ((IronGolemModel)this.getParentModel()).getFlowerHoldingArm().xRot / 3.1415927F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.translatef(-0.9375F, -0.625F, -0.9375F);
         float var9 = 0.5F;
         GlStateManager.scalef(0.5F, -0.5F, 0.5F);
         int var10 = var1.getLightColor();
         int var11 = var10 % 65536;
         int var12 = var10 / 65536;
         GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)var11, (float)var12);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.bindTexture(TextureAtlas.LOCATION_BLOCKS);
         Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.POPPY.defaultBlockState(), 1.0F);
         GlStateManager.popMatrix();
         GlStateManager.disableRescaleNormal();
      }
   }

   public boolean colorsOnDamage() {
      return false;
   }
}
