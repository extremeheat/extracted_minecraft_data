package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.GhastModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Ghast;

public class GhastRenderer extends MobRenderer<Ghast, GhastModel<Ghast>> {
   private static final ResourceLocation GHAST_LOCATION = new ResourceLocation("textures/entity/ghast/ghast.png");
   private static final ResourceLocation GHAST_SHOOTING_LOCATION = new ResourceLocation("textures/entity/ghast/ghast_shooting.png");

   public GhastRenderer(EntityRenderDispatcher var1) {
      super(var1, new GhastModel(), 1.5F);
   }

   protected ResourceLocation getTextureLocation(Ghast var1) {
      return var1.isCharging() ? GHAST_SHOOTING_LOCATION : GHAST_LOCATION;
   }

   protected void scale(Ghast var1, float var2) {
      float var3 = 1.0F;
      float var4 = 4.5F;
      float var5 = 4.5F;
      GlStateManager.scalef(4.5F, 4.5F, 4.5F);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
   }
}
