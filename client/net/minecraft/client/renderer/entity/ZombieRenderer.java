package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.world.entity.monster.Zombie;

public class ZombieRenderer extends AbstractZombieRenderer<Zombie, ZombieModel<Zombie>> {
   public ZombieRenderer(EntityRendererProvider.Context var1) {
      this(var1, ModelLayers.ZOMBIE, ModelLayers.ZOMBIE_INNER_ARMOR, ModelLayers.ZOMBIE_OUTER_ARMOR);
   }

   public ZombieRenderer(EntityRendererProvider.Context var1, ModelLayerLocation var2, ModelLayerLocation var3, ModelLayerLocation var4) {
      super(var1, new ZombieModel(var1.bakeLayer(var2)), new ZombieModel(var1.bakeLayer(var3)), new ZombieModel(var1.bakeLayer(var4)));
   }
}
