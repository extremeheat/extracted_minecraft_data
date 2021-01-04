package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.GiantZombieModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Giant;

public class GiantMobRenderer extends MobRenderer<Giant, HumanoidModel<Giant>> {
   private static final ResourceLocation ZOMBIE_LOCATION = new ResourceLocation("textures/entity/zombie/zombie.png");
   private final float scale;

   public GiantMobRenderer(EntityRenderDispatcher var1, float var2) {
      super(var1, new GiantZombieModel(), 0.5F * var2);
      this.scale = var2;
      this.addLayer(new ItemInHandLayer(this));
      this.addLayer(new HumanoidArmorLayer(this, new GiantZombieModel(0.5F, true), new GiantZombieModel(1.0F, true)));
   }

   protected void scale(Giant var1, float var2) {
      GlStateManager.scalef(this.scale, this.scale, this.scale);
   }

   protected ResourceLocation getTextureLocation(Giant var1) {
      return ZOMBIE_LOCATION;
   }
}
