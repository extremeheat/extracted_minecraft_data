package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.PigZombie;

public class PigZombieRenderer extends HumanoidMobRenderer {
   private static final ResourceLocation ZOMBIE_PIGMAN_LOCATION = new ResourceLocation("textures/entity/zombie_pigman.png");

   public PigZombieRenderer(EntityRenderDispatcher var1) {
      super(var1, new ZombieModel(0.0F, false), 0.5F);
      this.addLayer(new HumanoidArmorLayer(this, new ZombieModel(0.5F, true), new ZombieModel(1.0F, true)));
   }

   public ResourceLocation getTextureLocation(PigZombie var1) {
      return ZOMBIE_PIGMAN_LOCATION;
   }
}
