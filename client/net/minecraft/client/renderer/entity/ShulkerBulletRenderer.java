package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.ShulkerBulletModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.ShulkerBullet;

public class ShulkerBulletRenderer extends EntityRenderer<ShulkerBullet> {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/shulker/spark.png");
   private final ShulkerBulletModel<ShulkerBullet> model = new ShulkerBulletModel();

   public ShulkerBulletRenderer(EntityRenderDispatcher var1) {
      super(var1);
   }

   private float rotlerp(float var1, float var2, float var3) {
      float var4;
      for(var4 = var2 - var1; var4 < -180.0F; var4 += 360.0F) {
      }

      while(var4 >= 180.0F) {
         var4 -= 360.0F;
      }

      return var1 + var3 * var4;
   }

   public void render(ShulkerBullet var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.pushMatrix();
      float var10 = this.rotlerp(var1.yRotO, var1.yRot, var9);
      float var11 = Mth.lerp(var9, var1.xRotO, var1.xRot);
      float var12 = (float)var1.tickCount + var9;
      GlStateManager.translatef((float)var2, (float)var4 + 0.15F, (float)var6);
      GlStateManager.rotatef(Mth.sin(var12 * 0.1F) * 180.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(Mth.cos(var12 * 0.1F) * 180.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(Mth.sin(var12 * 0.15F) * 360.0F, 0.0F, 0.0F, 1.0F);
      float var13 = 0.03125F;
      GlStateManager.enableRescaleNormal();
      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
      this.bindTexture(var1);
      this.model.render(var1, 0.0F, 0.0F, 0.0F, var10, var11, 0.03125F);
      GlStateManager.enableBlend();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.5F);
      GlStateManager.scalef(1.5F, 1.5F, 1.5F);
      this.model.render(var1, 0.0F, 0.0F, 0.0F, var10, var11, 0.03125F);
      GlStateManager.disableBlend();
      GlStateManager.popMatrix();
      super.render(var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation getTextureLocation(ShulkerBullet var1) {
      return TEXTURE_LOCATION;
   }
}
