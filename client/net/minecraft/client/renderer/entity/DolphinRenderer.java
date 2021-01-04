package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.layers.DolphinCarryingItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Dolphin;

public class DolphinRenderer extends MobRenderer<Dolphin, DolphinModel<Dolphin>> {
   private static final ResourceLocation DOLPHIN_LOCATION = new ResourceLocation("textures/entity/dolphin.png");

   public DolphinRenderer(EntityRenderDispatcher var1) {
      super(var1, new DolphinModel(), 0.7F);
      this.addLayer(new DolphinCarryingItemLayer(this));
   }

   protected ResourceLocation getTextureLocation(Dolphin var1) {
      return DOLPHIN_LOCATION;
   }

   protected void scale(Dolphin var1, float var2) {
      float var3 = 1.0F;
      GlStateManager.scalef(1.0F, 1.0F, 1.0F);
   }

   protected void setupRotations(Dolphin var1, float var2, float var3, float var4) {
      super.setupRotations(var1, var2, var3, var4);
   }
}
