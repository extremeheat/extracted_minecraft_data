package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ZombieVillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;

public class ZombieVillagerRenderer extends HumanoidMobRenderer<ZombieVillager, ZombieVillagerModel<ZombieVillager>> {
   private static final ResourceLocation ZOMBIE_VILLAGER_LOCATION = new ResourceLocation("textures/entity/zombie_villager/zombie_villager.png");

   public ZombieVillagerRenderer(EntityRendererProvider.Context var1) {
      super(var1, new ZombieVillagerModel(var1.bakeLayer(ModelLayers.ZOMBIE_VILLAGER)), 0.5F);
      this.addLayer(new HumanoidArmorLayer(this, new ZombieVillagerModel(var1.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_INNER_ARMOR)), new ZombieVillagerModel(var1.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_OUTER_ARMOR))));
      this.addLayer(new VillagerProfessionLayer(this, var1.getResourceManager(), "zombie_villager"));
   }

   public ResourceLocation getTextureLocation(ZombieVillager var1) {
      return ZOMBIE_VILLAGER_LOCATION;
   }

   protected boolean isShaking(ZombieVillager var1) {
      return super.isShaking(var1) || var1.isConverting();
   }

   // $FF: synthetic method
   protected boolean isShaking(LivingEntity var1) {
      return this.isShaking((ZombieVillager)var1);
   }
}
