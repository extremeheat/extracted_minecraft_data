package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.SkullModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.WitherSkull;

public class WitherSkullRenderer extends EntityRenderer<WitherSkull> {
   private static final ResourceLocation WITHER_INVULNERABLE_LOCATION = new ResourceLocation("textures/entity/wither/wither_invulnerable.png");
   private static final ResourceLocation WITHER_LOCATION = new ResourceLocation("textures/entity/wither/wither.png");
   private final SkullModel model = new SkullModel();

   public WitherSkullRenderer(EntityRenderDispatcher var1) {
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

   public void render(WitherSkull var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      float var10 = this.rotlerp(var1.yRotO, var1.yRot, var9);
      float var11 = Mth.lerp(var9, var1.xRotO, var1.xRot);
      GlStateManager.translatef((float)var2, (float)var4, (float)var6);
      float var12 = 0.0625F;
      GlStateManager.enableRescaleNormal();
      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
      GlStateManager.enableAlphaTest();
      this.bindTexture(var1);
      if (this.solidRender) {
         GlStateManager.enableColorMaterial();
         GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(var1));
      }

      this.model.render(0.0F, 0.0F, 0.0F, var10, var11, 0.0625F);
      if (this.solidRender) {
         GlStateManager.tearDownSolidRenderingTextureCombine();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.popMatrix();
      super.render(var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation getTextureLocation(WitherSkull var1) {
      return var1.isDangerous() ? WITHER_INVULNERABLE_LOCATION : WITHER_LOCATION;
   }
}
