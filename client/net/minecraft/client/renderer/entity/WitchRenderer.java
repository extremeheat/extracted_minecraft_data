package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.WitchModel;
import net.minecraft.client.renderer.entity.layers.WitchItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Witch;

public class WitchRenderer extends MobRenderer<Witch, WitchModel<Witch>> {
   private static final ResourceLocation WITCH_LOCATION = new ResourceLocation("textures/entity/witch.png");

   public WitchRenderer(EntityRenderDispatcher var1) {
      super(var1, new WitchModel(0.0F), 0.5F);
      this.addLayer(new WitchItemLayer(this));
   }

   public void render(Witch var1, double var2, double var4, double var6, float var8, float var9) {
      ((WitchModel)this.model).setHoldingItem(!var1.getMainHandItem().isEmpty());
      super.render((Mob)var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation getTextureLocation(Witch var1) {
      return WITCH_LOCATION;
   }

   protected void scale(Witch var1, float var2) {
      float var3 = 0.9375F;
      GlStateManager.scalef(0.9375F, 0.9375F, 0.9375F);
   }
}
