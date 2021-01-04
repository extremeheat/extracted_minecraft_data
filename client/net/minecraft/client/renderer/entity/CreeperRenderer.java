package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.renderer.entity.layers.CreeperPowerLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperRenderer extends MobRenderer<Creeper, CreeperModel<Creeper>> {
   private static final ResourceLocation CREEPER_LOCATION = new ResourceLocation("textures/entity/creeper/creeper.png");

   public CreeperRenderer(EntityRenderDispatcher var1) {
      super(var1, new CreeperModel(), 0.5F);
      this.addLayer(new CreeperPowerLayer(this));
   }

   protected void scale(Creeper var1, float var2) {
      float var3 = var1.getSwelling(var2);
      float var4 = 1.0F + Mth.sin(var3 * 100.0F) * var3 * 0.01F;
      var3 = Mth.clamp(var3, 0.0F, 1.0F);
      var3 *= var3;
      var3 *= var3;
      float var5 = (1.0F + var3 * 0.4F) * var4;
      float var6 = (1.0F + var3 * 0.1F) / var4;
      GlStateManager.scalef(var5, var6, var5);
   }

   protected int getOverlayColor(Creeper var1, float var2, float var3) {
      float var4 = var1.getSwelling(var3);
      if ((int)(var4 * 10.0F) % 2 == 0) {
         return 0;
      } else {
         int var5 = (int)(var4 * 0.2F * 255.0F);
         var5 = Mth.clamp(var5, 0, 255);
         return var5 << 24 | 822083583;
      }
   }

   protected ResourceLocation getTextureLocation(Creeper var1) {
      return CREEPER_LOCATION;
   }
}
