package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;

public class WolfCollarLayer extends RenderLayer<Wolf, WolfModel<Wolf>> {
   private static final ResourceLocation WOLF_COLLAR_LOCATION = new ResourceLocation("textures/entity/wolf/wolf_collar.png");

   public WolfCollarLayer(RenderLayerParent<Wolf, WolfModel<Wolf>> var1) {
      super(var1);
   }

   public void render(Wolf var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (var1.isTame() && !var1.isInvisible()) {
         this.bindTexture(WOLF_COLLAR_LOCATION);
         float[] var9 = var1.getCollarColor().getTextureDiffuseColors();
         GlStateManager.color3f(var9[0], var9[1], var9[2]);
         ((WolfModel)this.getParentModel()).render(var1, var2, var3, var5, var6, var7, var8);
      }
   }

   public boolean colorsOnDamage() {
      return true;
   }
}
