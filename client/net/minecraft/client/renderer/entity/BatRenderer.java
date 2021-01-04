package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.BatModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ambient.Bat;

public class BatRenderer extends MobRenderer<Bat, BatModel> {
   private static final ResourceLocation BAT_LOCATION = new ResourceLocation("textures/entity/bat.png");

   public BatRenderer(EntityRenderDispatcher var1) {
      super(var1, new BatModel(), 0.25F);
   }

   protected ResourceLocation getTextureLocation(Bat var1) {
      return BAT_LOCATION;
   }

   protected void scale(Bat var1, float var2) {
      GlStateManager.scalef(0.35F, 0.35F, 0.35F);
   }

   protected void setupRotations(Bat var1, float var2, float var3, float var4) {
      if (var1.isResting()) {
         GlStateManager.translatef(0.0F, -0.1F, 0.0F);
      } else {
         GlStateManager.translatef(0.0F, Mth.cos(var2 * 0.3F) * 0.1F, 0.0F);
      }

      super.setupRotations(var1, var2, var3, var4);
   }
}
