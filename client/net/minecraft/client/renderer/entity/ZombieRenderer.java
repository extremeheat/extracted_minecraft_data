package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ZombieModel;
import net.minecraft.world.entity.monster.Zombie;

public class ZombieRenderer extends AbstractZombieRenderer<Zombie, ZombieModel<Zombie>> {
   public ZombieRenderer(EntityRenderDispatcher var1) {
      super(var1, new ZombieModel(), new ZombieModel(0.5F, true), new ZombieModel(1.0F, true));
   }
}
