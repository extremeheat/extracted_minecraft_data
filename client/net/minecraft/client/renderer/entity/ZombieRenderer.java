package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.zombie.ZombieModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.world.entity.monster.zombie.Zombie;

public class ZombieRenderer extends AbstractZombieRenderer<Zombie, ZombieRenderState, ZombieModel<ZombieRenderState>> {
   public ZombieRenderer(EntityRendererProvider.Context var1) {
      this(var1, ModelLayers.ZOMBIE, ModelLayers.ZOMBIE_BABY, ModelLayers.ZOMBIE_ARMOR, ModelLayers.ZOMBIE_BABY_ARMOR);
   }

   public ZombieRenderState createRenderState() {
      return new ZombieRenderState();
   }

   public ZombieRenderer(EntityRendererProvider.Context var1, ModelLayerLocation var2, ModelLayerLocation var3, ArmorModelSet<ModelLayerLocation> var4, ArmorModelSet<ModelLayerLocation> var5) {
      super(var1, new ZombieModel(var1.bakeLayer(var2)), new ZombieModel(var1.bakeLayer(var3)), ArmorModelSet.bake(var4, var1.getModelSet(), ZombieModel::new), ArmorModelSet.bake(var5, var1.getModelSet(), ZombieModel::new));
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
