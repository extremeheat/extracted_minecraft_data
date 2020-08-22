package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ZombieModel;

public class ZombieRenderer extends AbstractZombieRenderer {
   public ZombieRenderer(EntityRenderDispatcher var1) {
      super(var1, new ZombieModel(0.0F, false), new ZombieModel(0.5F, true), new ZombieModel(1.0F, true));
   }
}
