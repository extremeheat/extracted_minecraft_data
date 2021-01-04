package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.model.SalmonModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Salmon;

public class SalmonRenderer extends MobRenderer<Salmon, SalmonModel<Salmon>> {
   private static final ResourceLocation SALMON_LOCATION = new ResourceLocation("textures/entity/fish/salmon.png");

   public SalmonRenderer(EntityRenderDispatcher var1) {
      super(var1, new SalmonModel(), 0.4F);
   }

   @Nullable
   protected ResourceLocation getTextureLocation(Salmon var1) {
      return SALMON_LOCATION;
   }

   protected void setupRotations(Salmon var1, float var2, float var3, float var4) {
      super.setupRotations(var1, var2, var3, var4);
      float var5 = 1.0F;
      float var6 = 1.0F;
      if (!var1.isInWater()) {
         var5 = 1.3F;
         var6 = 1.7F;
      }

      float var7 = var5 * 4.3F * Mth.sin(var6 * 0.6F * var2);
      GlStateManager.rotatef(var7, 0.0F, 1.0F, 0.0F);
      GlStateManager.translatef(0.0F, 0.0F, -0.4F);
      if (!var1.isInWater()) {
         GlStateManager.translatef(0.2F, 0.1F, 0.0F);
         GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
      }

   }
}
