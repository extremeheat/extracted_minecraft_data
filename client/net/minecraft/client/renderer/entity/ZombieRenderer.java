package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.world.entity.monster.Zombie;

public class ZombieRenderer extends AbstractZombieRenderer<Zombie, ZombieRenderState, ZombieModel<ZombieRenderState>> {
   public ZombieRenderer(EntityRendererProvider.Context var1) {
      this(
         var1,
         ModelLayers.ZOMBIE,
         ModelLayers.ZOMBIE_BABY,
         ModelLayers.ZOMBIE_INNER_ARMOR,
         ModelLayers.ZOMBIE_OUTER_ARMOR,
         ModelLayers.ZOMBIE_BABY_INNER_ARMOR,
         ModelLayers.ZOMBIE_BABY_OUTER_ARMOR
      );
   }

   public ZombieRenderState createRenderState() {
      return new ZombieRenderState();
   }

   @Override
   public void extractRenderState(Zombie var1, ZombieRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.isAggressive = var1.isAggressive();
   }

   public ZombieRenderer(
      EntityRendererProvider.Context var1,
      ModelLayerLocation var2,
      ModelLayerLocation var3,
      ModelLayerLocation var4,
      ModelLayerLocation var5,
      ModelLayerLocation var6,
      ModelLayerLocation var7
   ) {
      super(
         var1,
         new ZombieModel<>(var1.bakeLayer(var2)),
         new ZombieModel<>(var1.bakeLayer(var3)),
         new ZombieModel<>(var1.bakeLayer(var4)),
         new ZombieModel<>(var1.bakeLayer(var5)),
         new ZombieModel<>(var1.bakeLayer(var6)),
         new ZombieModel<>(var1.bakeLayer(var7))
      );
   }
}
