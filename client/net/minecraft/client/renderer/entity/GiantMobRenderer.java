package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.GiantZombieModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Giant;

public class GiantMobRenderer extends MobRenderer<Giant, ZombieRenderState, HumanoidModel<ZombieRenderState>> {
   private static final ResourceLocation ZOMBIE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/zombie/zombie.png");

   public GiantMobRenderer(EntityRendererProvider.Context var1, float var2) {
      super(var1, new GiantZombieModel(var1.bakeLayer(ModelLayers.GIANT)), 0.5F * var2);
      this.addLayer(new ItemInHandLayer<>(this, var1.getItemRenderer()));
      this.addLayer(
         new HumanoidArmorLayer<>(
            this,
            new GiantZombieModel(var1.bakeLayer(ModelLayers.GIANT_INNER_ARMOR)),
            new GiantZombieModel(var1.bakeLayer(ModelLayers.GIANT_OUTER_ARMOR)),
            var1.getModelManager()
         )
      );
   }

   public ResourceLocation getTextureLocation(ZombieRenderState var1) {
      return ZOMBIE_LOCATION;
   }

   public ZombieRenderState createRenderState() {
      return new ZombieRenderState();
   }

   public void extractRenderState(Giant var1, ZombieRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      HumanoidMobRenderer.extractHumanoidRenderState(var1, var2, var3);
   }
}
