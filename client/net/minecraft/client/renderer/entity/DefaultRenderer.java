package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class DefaultRenderer extends EntityRenderer<Entity> {
   public DefaultRenderer(EntityRenderDispatcher var1) {
      super(var1);
   }

   public void render(Entity var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.pushMatrix();
      render(var1.getBoundingBox(), var2 - var1.xOld, var4 - var1.yOld, var6 - var1.zOld);
      GlStateManager.popMatrix();
      super.render(var1, var2, var4, var6, var8, var9);
   }

   @Nullable
   protected ResourceLocation getTextureLocation(Entity var1) {
      return null;
   }
}
