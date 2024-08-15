package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ZombieVillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.client.renderer.entity.state.ZombieVillagerRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.ZombieVillager;

public class ZombieVillagerRenderer extends HumanoidMobRenderer<ZombieVillager, ZombieVillagerRenderState, ZombieVillagerModel<ZombieVillagerRenderState>> {
   private static final ResourceLocation ZOMBIE_VILLAGER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/zombie_villager/zombie_villager.png");

   public ZombieVillagerRenderer(EntityRendererProvider.Context var1) {
      super(
         var1,
         new ZombieVillagerModel<>(var1.bakeLayer(ModelLayers.ZOMBIE_VILLAGER)),
         new ZombieVillagerModel<>(var1.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_BABY)),
         0.5F,
         VillagerRenderer.CUSTOM_HEAD_TRANSFORMS
      );
      this.addLayer(
         new HumanoidArmorLayer<>(
            this,
            new ZombieVillagerModel(var1.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_INNER_ARMOR)),
            new ZombieVillagerModel(var1.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_OUTER_ARMOR)),
            new ZombieVillagerModel(var1.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_BABY_INNER_ARMOR)),
            new ZombieVillagerModel(var1.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_BABY_OUTER_ARMOR)),
            var1.getModelManager()
         )
      );
      this.addLayer(new VillagerProfessionLayer<>(this, var1.getResourceManager(), "zombie_villager"));
   }

   public ResourceLocation getTextureLocation(ZombieVillagerRenderState var1) {
      return ZOMBIE_VILLAGER_LOCATION;
   }

   public ZombieVillagerRenderState createRenderState() {
      return new ZombieVillagerRenderState();
   }

   public void extractRenderState(ZombieVillager var1, ZombieVillagerRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.isConverting = var1.isConverting();
      var2.villagerData = var1.getVillagerData();
   }

   protected boolean isShaking(ZombieVillagerRenderState var1) {
      return super.isShaking(var1) || var1.isConverting;
   }
}
