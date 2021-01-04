package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.PhantomModel;
import net.minecraft.client.renderer.entity.layers.PhantomEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Phantom;

public class PhantomRenderer extends MobRenderer<Phantom, PhantomModel<Phantom>> {
   private static final ResourceLocation PHANTOM_LOCATION = new ResourceLocation("textures/entity/phantom.png");

   public PhantomRenderer(EntityRenderDispatcher var1) {
      super(var1, new PhantomModel(), 0.75F);
      this.addLayer(new PhantomEyesLayer(this));
   }

   protected ResourceLocation getTextureLocation(Phantom var1) {
      return PHANTOM_LOCATION;
   }

   protected void scale(Phantom var1, float var2) {
      int var3 = var1.getPhantomSize();
      float var4 = 1.0F + 0.15F * (float)var3;
      GlStateManager.scalef(var4, var4, var4);
      GlStateManager.translatef(0.0F, 1.3125F, 0.1875F);
   }

   protected void setupRotations(Phantom var1, float var2, float var3, float var4) {
      super.setupRotations(var1, var2, var3, var4);
      GlStateManager.rotatef(var1.xRot, 1.0F, 0.0F, 0.0F);
   }
}
