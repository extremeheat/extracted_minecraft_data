package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.LavaSlimeModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.MagmaCube;

public class LavaSlimeRenderer extends MobRenderer<MagmaCube, LavaSlimeModel<MagmaCube>> {
   private static final ResourceLocation MAGMACUBE_LOCATION = new ResourceLocation("textures/entity/slime/magmacube.png");

   public LavaSlimeRenderer(EntityRenderDispatcher var1) {
      super(var1, new LavaSlimeModel(), 0.25F);
   }

   protected ResourceLocation getTextureLocation(MagmaCube var1) {
      return MAGMACUBE_LOCATION;
   }

   protected void scale(MagmaCube var1, float var2) {
      int var3 = var1.getSize();
      float var4 = Mth.lerp(var2, var1.oSquish, var1.squish) / ((float)var3 * 0.5F + 1.0F);
      float var5 = 1.0F / (var4 + 1.0F);
      GlStateManager.scalef(var5 * (float)var3, 1.0F / var5 * (float)var3, var5 * (float)var3);
   }
}
