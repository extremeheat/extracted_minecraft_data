package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.DragonFireball;

public class DragonFireballRenderer extends EntityRenderer<DragonFireball> {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon_fireball.png");

   public DragonFireballRenderer(EntityRenderDispatcher var1) {
      super(var1);
   }

   public void render(DragonFireball var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.pushMatrix();
      this.bindTexture(var1);
      GlStateManager.translatef((float)var2, (float)var4, (float)var6);
      GlStateManager.enableRescaleNormal();
      GlStateManager.scalef(2.0F, 2.0F, 2.0F);
      Tesselator var10 = Tesselator.getInstance();
      BufferBuilder var11 = var10.getBuilder();
      float var12 = 1.0F;
      float var13 = 0.5F;
      float var14 = 0.25F;
      GlStateManager.rotatef(180.0F - this.entityRenderDispatcher.playerRotY, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef((float)(this.entityRenderDispatcher.options.thirdPersonView == 2 ? -1 : 1) * -this.entityRenderDispatcher.playerRotX, 1.0F, 0.0F, 0.0F);
      if (this.solidRender) {
         GlStateManager.enableColorMaterial();
         GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(var1));
      }

      var11.begin(7, DefaultVertexFormat.POSITION_TEX_NORMAL);
      var11.vertex(-0.5D, -0.25D, 0.0D).uv(0.0D, 1.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
      var11.vertex(0.5D, -0.25D, 0.0D).uv(1.0D, 1.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
      var11.vertex(0.5D, 0.75D, 0.0D).uv(1.0D, 0.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
      var11.vertex(-0.5D, 0.75D, 0.0D).uv(0.0D, 0.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
      var10.end();
      if (this.solidRender) {
         GlStateManager.tearDownSolidRenderingTextureCombine();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.render(var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation getTextureLocation(DragonFireball var1) {
      return TEXTURE_LOCATION;
   }
}
